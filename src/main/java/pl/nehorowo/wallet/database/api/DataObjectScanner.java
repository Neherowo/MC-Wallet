package pl.nehorowo.wallet.database.api;

import lombok.SneakyThrows;
import pl.nehorowo.wallet.database.DatabaseConnector;
import pl.nehorowo.wallet.database.api.stereotype.DataObject;
import pl.nehorowo.wallet.database.api.stereotype.ListSeparator;
import pl.nehorowo.wallet.database.api.stereotype.PrimaryKey;
import pl.nehorowo.wallet.database.api.stereotype.Value;
import pl.nehorowo.wallet.database.helper.ListHelper;
import pl.nehorowo.wallet.database.helper.ReflectionHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DataObjectScanner<T> {

    private final Class<T> clazz;
    private final DatabaseConnector connector;

    private DataObject dataObject;

    private Constructor<?> constructor;

    @SneakyThrows
    public DataObjectScanner(Class<T> clazz, DatabaseConnector connector){
        this.clazz = clazz;
        this.connector = connector;
        dataObject = clazz.getDeclaredAnnotation(DataObject.class);
        if(dataObject == null) return;
        constructor = ReflectionHelper.getConstructor(clazz, ResultSet.class);
        if(constructor == null){
            System.out.println("Constructor: \"" + clazz.getName()+"(ResultSet.class)\" cannot be null");
            return;
        }
        createTable();
    }

    public Class<T> getClazz() {
        return clazz;
    }

    private void createTable() {
        PrimaryKey primaryKey = null;
        Map<String, String> objectMap = new HashMap<>();
        for (Field declaredField : clazz.getDeclaredFields()) {
            declaredField.setAccessible(true);
            PrimaryKey key = declaredField.getDeclaredAnnotation(PrimaryKey.class);
            if(key != null) {
                primaryKey = key;
                objectMap.put(key.value().key(), key.value().type());
            } else {
                Value value = declaredField.getDeclaredAnnotation(Value.class);
                if (value != null) {
                    objectMap.put(value.key(), value.type());
                }
            }
        }
        Objects.requireNonNull(primaryKey, "Primary key cannot be null");
        connector.getGetter().createTable(dataObject.table(), "primary key(" + primaryKey.value().key() + ")", ListHelper.mapToList(objectMap, (s, s2) -> s + " " + s2));
    }

    @SneakyThrows
    public void update(T object){
        if(connector.getConnection().isClosed()) connector.reconnect();
        Objects.requireNonNull(dataObject.table(), "Primary key cannot be null");
        Map<String, Object> objectMap = new HashMap<>();

        for (Field declaredField : clazz.getDeclaredFields()) {
            declaredField.setAccessible(true);
            PrimaryKey primaryKey = declaredField.getDeclaredAnnotation(PrimaryKey.class);
            Value value = declaredField.getDeclaredAnnotation(Value.class);
            if(primaryKey != null) value = primaryKey.value();
            if (value == null) continue;
            Object obj = declaredField.get(object);
            if(obj == null) break;
            ItemSerializer<Object> serializer = (ItemSerializer<Object>) connector.getSerializerMap().get(obj.getClass());
            if(serializer == null) {
                if(obj instanceof List) {
                    List list = (List) obj;
                    ListSeparator separator = declaredField.getDeclaredAnnotation(ListSeparator.class);
                    if(separator != null) objectMap.put(value.key(), list.stream().map(Objects::toString).collect(Collectors.joining(separator.separator())));
                    else objectMap.put(value.key(), list.stream().map(this::serialize).collect(Collectors.toList()));
                } else objectMap.put(value.key(), obj);
            } else objectMap.put(value.key(), serializer.deserialize(obj));
        }
        connector.getGetter().createOrUpdate(dataObject.table(), objectMap);
    }

    @SneakyThrows
    public void remove(T object){
        Objects.requireNonNull(dataObject.table(), "Primary key cannot be null");

        for (Field declaredField : clazz.getDeclaredFields()) {
            declaredField.setAccessible(true);
            PrimaryKey primaryKey = declaredField.getDeclaredAnnotation(PrimaryKey.class);

            if(primaryKey == null) continue;
            Value value = primaryKey.value();
            Object obj = declaredField.get(object);
            if(obj == null) break;
            ItemSerializer<Object> serializer = (ItemSerializer<Object>) connector.getSerializerMap().get(obj.getClass());
            if(serializer == null) {
                if(obj instanceof List list) {
                    ListSeparator separator = declaredField.getDeclaredAnnotation(ListSeparator.class);
                    if(separator != null) connector.getConnection().prepareStatement("DELETE FROM `" + dataObject.table() + "` WHERE `" + value.key() + "`='" + list.stream().map(Objects::toString).collect(Collectors.joining(separator.separator())) + "'").execute();
                    else connector.getConnection().prepareStatement("DELETE FROM `" + dataObject.table() + "` WHERE `" + value.key() + "`='" + list.stream().map(this::serialize).collect(Collectors.toList()) + "'").execute();
                } else connector.getConnection().prepareStatement("DELETE FROM `" + dataObject.table() + "` WHERE `" + value.key() + "`='" + obj + "'").execute();
            } else connector.getConnection().prepareStatement("DELETE FROM `" + dataObject.table() + "` WHERE `" + value.key() + "`='" + serializer.deserialize(obj) + "'").execute();
            return;
        }
    }

    private <V> Object serialize(V value){
        ItemSerializer<V> itemSerializer = (ItemSerializer<V>) connector.getSerializerMap().get(value);
        return itemSerializer == null ? value : itemSerializer.deserialize(value);
    }

    @SneakyThrows
    public void load(DatabaseCache<T> databaseCache){
        ResultSet resultSet = connector.getConnection().prepareStatement("SELECT * FROM `" + dataObject.table() + "`").executeQuery();
        while (resultSet.next()){
            T t = (T) ReflectionHelper.newInstance(constructor, resultSet);
            databaseCache.add(t);
        }
        resultSet.close();
    }
}

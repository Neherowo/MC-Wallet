package pl.nehorowo.wallet.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import pl.nehorowo.wallet.database.api.DataObjectScanner;
import pl.nehorowo.wallet.database.api.ItemSerializer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DatabaseConnector {

    private HikariDataSource dataSource;
    private final DatabaseGetter getter;

    protected final HikariConfig configuration;

    public DatabaseConnector(DatabaseConfiguration dataBaseConfiguration) {
        this.configuration = this.getHikariConfig(dataBaseConfiguration);
        this.dataSource = new HikariDataSource(this.configuration);
        this.getter = new DatabaseGetter(this);
    }

    public DatabaseGetter getGetter() {
        return getter;
    }

    private final Map<Class<?>, DataObjectScanner<?>> scannerMap = new HashMap<>();
    private final Map<Class<?>, ItemSerializer<?>> serializerMap = new HashMap<>();

    public void registerScanner(DataObjectScanner<?> scanner){
        scannerMap.put(scanner.getClazz(), scanner);
    }

    public <T> void registerSerializer(ItemSerializer<T> itemSerializer){
        serializerMap.put(itemSerializer.supportedClass(), itemSerializer);
    }

    public <T> void registerDataObjectToScan(Class<T> clazz){
        DataObjectScanner<T> scanner = new DataObjectScanner<>(clazz, this);
        registerScanner(scanner);
    }

    public Map<Class<?>, ItemSerializer<?>> getSerializerMap() {
        return serializerMap;
    }

    public Map<Class<?>, DataObjectScanner<?>> getScannerMap() {
        return scannerMap;
    }

    public static Connection connection;

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = dataSource.getConnection();
        }
        return connection;
    }

    private HikariConfig getHikariConfig(DatabaseConfiguration dataBaseConfiguration) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=" + dataBaseConfiguration.ssl(),
                dataBaseConfiguration.host(),
                dataBaseConfiguration.port(),
                dataBaseConfiguration.table()));
        hikariConfig.setUsername(dataBaseConfiguration.username());
        hikariConfig.setPassword(dataBaseConfiguration.password());
        //hikariConfig.addDataSourceProperty("maxLifetime", "60000");
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
        hikariConfig.addDataSourceProperty("autoReconnect", "true");
        hikariConfig.addDataSourceProperty("failOverReadOnly", "false");
        return hikariConfig;
    }

    public <T> Optional<DataObjectScanner<T>> getScanner(Class<T> aClass) {
        return Optional.ofNullable((DataObjectScanner<T>) scannerMap.get(aClass));
    }

    public void reconnect() {
        this.dataSource = new HikariDataSource(this.configuration);
    }
}
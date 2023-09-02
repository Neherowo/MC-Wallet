package pl.nehorowo.wallet.database.api;

public interface ItemSerializer<T> {

    Class<T> supportedClass();

    T serialize(String s);

    String deserialize(T t);

}

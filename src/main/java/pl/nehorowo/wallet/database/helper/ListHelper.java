package pl.nehorowo.wallet.database.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ListHelper {

    public static <K, V> List<String> mapToList(Map<K, V> map, BiFunction<K, V, String> function){
        List<String> list = new ArrayList<>();
        map.forEach(((k, v) -> list.add(function.apply(k, v))));
        return list;
    }

}

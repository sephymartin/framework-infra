package top.sephy.infra.option;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.NonNull;

public interface ItemOptionProvider<K, V> {

    /**
     * Get all item types
     * 
     * @return
     */
    Set<String> getTypes();

    /**
     * Get all options
     * 
     * @return
     */
    List<ItemOption<K, V>> getAllOptions();

    default boolean fixedTypes() {
        return true;
    }

    /**
     * Get options by type
     * 
     * @param type
     * @return
     */
    default List<ItemOption<K, V>> getOptions(String type) {
        return getOptions(type, null, true);
    }

    default List<ItemOption<K, V>> getOptions(@NonNull String type, Set<K> filterKeys) {
        return getOptions(type, filterKeys, true);
    }

    /**
     * Get options by type and filter values
     * 
     * @param type
     * @param filterKeys
     * @return
     */
    default List<ItemOption<K, V>> getOptions(@NonNull String type, Set<K> filterKeys, boolean compareWithKeyString) {
        List<ItemOption<K, V>> list = new ArrayList<>();
        for (ItemOption<K, V> option : getAllOptions()) {
            if (Objects.equals(option.getType(), type)) {
                if (filterKeys != null) {
                    if (compareWithKeyString) {
                        Set<String> filterStringKeys =
                            filterKeys.stream().map(Object::toString).collect(Collectors.toSet());
                        if (filterStringKeys.contains(String.valueOf(option.getKey()))) {
                            list.add(option);
                        }
                    } else if (filterKeys.contains(option.getKey())) {
                        list.add(option);
                    }
                }

            }
        }
        return list;
    }

    default Map<String, List<ItemOption<K, V>>> optionsByGroup() {
        return getAllOptions().stream().collect(Collectors.groupingBy(ItemOption::getType));
    }

    default Map<K, V> getAllOptionMap() {
        return getAllOptions().stream().collect(Collectors.toMap(ItemOption::getKey, ItemOption::getLabel));
    }

    default Map<K, V> getOptionsMap(@NonNull String type) {
        return getOptions(type).stream().collect(Collectors.toMap(ItemOption::getKey, ItemOption::getLabel));
    }

    default Map<K, V> getOptionsMap(@NonNull String type, Set<K> filterValues, boolean compareWithKeyString) {
        return getOptions(type, filterValues, compareWithKeyString).stream()
            .collect(Collectors.toMap(ItemOption::getKey, ItemOption::getLabel));
    }

    default V getV(@NonNull String type, @NonNull K k) {
        return getV(type, k, true);
    }

    default V getV(@NonNull String type, @NonNull K k, boolean compareWithKeyString) {
        return getOptions(type, Set.of(k), compareWithKeyString).stream().map(ItemOption::getLabel).findFirst()
            .orElse(null);
    }
}

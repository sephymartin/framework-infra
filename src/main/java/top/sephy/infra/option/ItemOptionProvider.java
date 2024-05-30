package top.sephy.infra.option;

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
        return getOptions(type, null);
    }

    /**
     * Get options by type and filter values
     * 
     * @param type
     * @param filterValues
     * @return
     */
    default List<ItemOption<K, V>> getOptions(@NonNull String type, Set<K> filterValues) {
        return getAllOptions().stream().filter(kvItemOption -> Objects.equals(kvItemOption.getType(), type)
            && (filterValues == null || filterValues.contains(kvItemOption.getValue()))).toList();
    }

    default Map<String, List<ItemOption<K, V>>> optionsByGroup() {
        return getAllOptions().stream().collect(Collectors.groupingBy(ItemOption::getType));
    }

    default Map<K, V> getAllOptionMap() {
        return getAllOptions().stream().collect(Collectors.toMap(ItemOption::getValue, ItemOption::getLabel));
    }

    default Map<K, V> getOptionsMap(@NonNull String type) {
        return getOptions(type).stream().collect(Collectors.toMap(ItemOption::getValue, ItemOption::getLabel));
    }

    default Map<K, V> getOptionsMap(@NonNull String type, Set<K> filterValues) {
        return getOptions(type, filterValues).stream()
            .collect(Collectors.toMap(ItemOption::getValue, ItemOption::getLabel));
    }

    default V getV(@NonNull String type, @NonNull K k) {
        return getOptions(type, Set.of(k)).stream().map(ItemOption::getLabel).findFirst().orElse(null);
    }
}

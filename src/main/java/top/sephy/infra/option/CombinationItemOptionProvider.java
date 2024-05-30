package top.sephy.infra.option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;

import lombok.NonNull;

public class CombinationItemOptionProvider<K, V> implements ItemOptionProvider<K, V> {

    private final List<ItemOptionProvider<K, V>> fixedTypeProviders = new ArrayList<>();

    private final List<ItemOptionProvider<K, V>> notFixedTypeProviders = new ArrayList<>();

    private final ConcurrentMap<String, ItemOptionProvider<K, V>> typeProviderMappingCache = new ConcurrentHashMap<>();

    public CombinationItemOptionProvider(ObjectProvider<List<ItemOptionProvider<K, V>>> provider) {
        List<ItemOptionProvider<K, V>> ifAvailable = provider.getIfAvailable();
        if (ifAvailable != null) {
            for (ItemOptionProvider<K, V> itemOptionProvider : ifAvailable) {
                if (itemOptionProvider.fixedTypes()) {
                    fixedTypeProviders.add(itemOptionProvider);
                } else {
                    notFixedTypeProviders.add(itemOptionProvider);
                }
                for (String type : itemOptionProvider.getTypes()) {
                    typeProviderMappingCache.put(type, itemOptionProvider);
                }
            }
        }
    }

    @Override
    public Set<String> getTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.addAll(fixedTypeProviders.stream().flatMap(p -> p.getTypes().stream()).collect(Collectors.toSet()));
        types.addAll(notFixedTypeProviders.stream().flatMap(p -> p.getTypes().stream()).collect(Collectors.toSet()));
        return types;
    }

    @Override
    public List<ItemOption<K, V>> getAllOptions() {
        List<ItemOption<K, V>> list = new ArrayList<>();
        list.addAll(fixedTypeProviders.stream().flatMap(p -> p.getAllOptions().stream()).toList());
        list.addAll(notFixedTypeProviders.stream().flatMap(p -> p.getAllOptions().stream()).toList());
        return list;
    }

    @Override
    public boolean fixedTypes() {
        return notFixedTypeProviders.isEmpty();
    }

    @Override
    public List<ItemOption<K, V>> getOptions(@NonNull String type, Set<K> filterValues) {
        ItemOptionProvider<K, V> provider = typeProviderMappingCache.get(type);

        List<ItemOption<K, V>> options = Collections.emptyList();
        if (provider != null) {
            options = provider.getOptions(type, filterValues);
        } else {
            for (ItemOptionProvider<K, V> itemOptionProvider : notFixedTypeProviders) {
                if (itemOptionProvider.getTypes().contains(type)) {
                    options = itemOptionProvider.getOptions(type, filterValues);
                    typeProviderMappingCache.put(type, itemOptionProvider);
                    break;
                }
            }
        }

        return options;
    }

    @Override
    public Map<String, List<ItemOption<K, V>>> optionsByGroup() {
        Map<String, List<ItemOption<K, V>>> map = new LinkedHashMap<>();
        for (ItemOptionProvider<K, V> optionProvider : fixedTypeProviders) {
            for (ItemOption<K, V> option : optionProvider.getAllOptions()) {
                map.computeIfAbsent(option.getType(), k -> new ArrayList<>()).add(option);
            }
        }

        for (ItemOptionProvider<K, V> optionProvider : notFixedTypeProviders) {
            for (ItemOption<K, V> option : optionProvider.getAllOptions()) {
                map.computeIfAbsent(option.getType(), k -> new ArrayList<>()).add(option);
            }
        }

        return map;
    }
}

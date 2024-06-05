package top.sephy.infra.option;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CompositeCachedDictEntryProvider<K, V>
    implements DictEntryProvider<K, V>, ApplicationListener<ContextRefreshedEvent> {

    private final ObjectProvider<List<DictEntryProvider<K, V>>> objectProvider;

    private final Map<String, List<DictEntry<K, V>>> dictCache = new ConcurrentHashMap<>();

    public CompositeCachedDictEntryProvider(ObjectProvider<List<DictEntryProvider<K, V>>> objectProvider) {
        this.objectProvider = objectProvider;
    }

    @Override
    public Set<String> types() {
        return dictCache.keySet();
    }

    @Override
    public Map<String, List<DictEntry<K, V>>> optionsMap() {
        return ImmutableMap.copyOf(dictCache);
    }

    @Override
    public List<DictEntry<K, V>> options(String type) {
        return dictCache.getOrDefault(type, Collections.emptyList());
    }

    public void refresh() {
        List<DictEntryProvider<K, V>> ifAvailable = objectProvider.getIfAvailable();
        if (ifAvailable != null) {
            for (DictEntryProvider<K, V> itemOptionProvider : ifAvailable) {
                Map<String, List<DictEntry<K, V>>> tmp = itemOptionProvider.optionsMap();
                for (Map.Entry<String, List<DictEntry<K, V>>> entry : tmp.entrySet()) {
                    dictCache.put(entry.getKey(), ImmutableList.copyOf(entry.getValue()));
                }
            }
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        refresh();
    }
}

package top.sephy.infra.option;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictEntry<K, V> {

    private K key;

    private V label;

    private Boolean disabled;

    private String type;
}

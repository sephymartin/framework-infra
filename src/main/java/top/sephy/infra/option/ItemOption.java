package top.sephy.infra.option;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemOption<K, V> {

    @Schema(name = "对应 option 的 value")
    private K value;
    @Schema(name = "对应 option 的 label")
    private V label;
    @Schema(name = "是否失效")
    private Boolean disabled;
    @Schema(name = "类别")
    private String type;
}

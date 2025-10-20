package cloud.neko_lkc1009.nekoshare.model;

import cloud.neko_lkc1009.nekoshare.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Permission extends BaseEntity<Permission> {
    private String name;
    private Long parentId;
    private String path;
    private String icon;
    private Integer type;
    private Integer sort;

}

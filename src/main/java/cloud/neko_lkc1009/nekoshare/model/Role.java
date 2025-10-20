package cloud.neko_lkc1009.nekoshare.model;

import cloud.neko_lkc1009.nekoshare.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity<Role> {
    private String name;

}

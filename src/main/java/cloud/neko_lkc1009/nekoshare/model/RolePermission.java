package cloud.neko_lkc1009.nekoshare.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RolePermission {
    private Long roleId;
    private Long permissionId;

}

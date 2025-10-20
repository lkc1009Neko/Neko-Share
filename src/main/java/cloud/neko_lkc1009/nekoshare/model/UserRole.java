package cloud.neko_lkc1009.nekoshare.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserRole {
    private Long userId;
    private Long roleId;

}

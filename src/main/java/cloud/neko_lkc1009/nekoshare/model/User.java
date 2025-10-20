package cloud.neko_lkc1009.nekoshare.model;

import cloud.neko_lkc1009.nekoshare.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity<User> {
    private String username;
    private String password;
    private String email;
    private String nickname;
    private String avatar;
    private String salt;

}

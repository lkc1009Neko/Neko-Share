package cloud.neko_lkc1009.nekoshare.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserDto {
    private String username;
    private String password;
}

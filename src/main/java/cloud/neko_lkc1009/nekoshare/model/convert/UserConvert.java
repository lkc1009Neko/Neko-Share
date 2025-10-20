package cloud.neko_lkc1009.nekoshare.model.convert;

import cloud.neko_lkc1009.nekoshare.common.base.BaseConvert;
import cloud.neko_lkc1009.nekoshare.configuration.security.SaltKit;
import cloud.neko_lkc1009.nekoshare.model.User;
import cloud.neko_lkc1009.nekoshare.model.dto.UserDto;
import cn.dev33.satoken.secure.SaSecureUtil;
import org.springframework.stereotype.Component;

@Component
public class UserConvert implements BaseConvert<User, UserDto> {
    @Override
    public UserDto convert(User source) {
        return new UserDto();
    }

    @Override
    public User reverse(UserDto target) {
        String salt = SaltKit.initSalt();
        return new User()
                .setUsername(target.getUsername())
                .setPassword(SaSecureUtil.aesEncrypt(salt, target.getPassword()))
                .setSalt(salt);
    }
}

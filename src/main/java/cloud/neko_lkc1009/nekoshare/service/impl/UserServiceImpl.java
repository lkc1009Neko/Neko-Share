package cloud.neko_lkc1009.nekoshare.service.impl;

import cloud.neko_lkc1009.nekoshare.configuration.security.SaltKit;
import cloud.neko_lkc1009.nekoshare.mapper.UserMapper;
import cloud.neko_lkc1009.nekoshare.model.User;
import cloud.neko_lkc1009.nekoshare.model.convert.UserConvert;
import cloud.neko_lkc1009.nekoshare.model.dto.UserDto;
import cloud.neko_lkc1009.nekoshare.service.UserService;
import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserConvert userConvert;

    @Override
    public void login(UserDto userDto) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, userDto.getUsername()));

        if (Objects.isNull(user)) {
            throw new RuntimeException("");
        }

        if (!Objects.equals(SaSecureUtil.aesDecrypt(user.getSalt(), user.getPassword()), user.getPassword())) {
            throw new RuntimeException("");
        }

        StpUtil.login(user.getId());
    }

    @Override
    public void register(UserDto userDto) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, userDto.getUsername()));

        if (Objects.nonNull(user)) {
            throw new RuntimeException("");
        }

        User insertUser = userConvert.reverse(userDto);
        userMapper.insert(insertUser);

        if (Objects.nonNull(insertUser.getId())) {
            StpUtil.login(insertUser.getId());
        } else {
            throw new RuntimeException("");
        }
    }

}

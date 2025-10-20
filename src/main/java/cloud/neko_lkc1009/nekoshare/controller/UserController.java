package cloud.neko_lkc1009.nekoshare.controller;

import cloud.neko_lkc1009.nekoshare.common.base.BaseResult;
import cloud.neko_lkc1009.nekoshare.model.dto.UserDto;
import cloud.neko_lkc1009.nekoshare.service.UserService;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public BaseResult<SaTokenInfo> login(UserDto userDto) {
        userService.login(userDto);
        return BaseResult.success(StpUtil.getTokenInfo());
    }

    @PostMapping("/register")
    public BaseResult<SaTokenInfo> register(UserDto userDto) {
        userService.register(userDto);
        return BaseResult.success(StpUtil.getTokenInfo());
    }

}

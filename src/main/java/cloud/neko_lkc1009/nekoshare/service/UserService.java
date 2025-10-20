package cloud.neko_lkc1009.nekoshare.service;

import cloud.neko_lkc1009.nekoshare.model.dto.UserDto;

public interface UserService {
    void login(UserDto userDto);

    void register(UserDto userDto);

}

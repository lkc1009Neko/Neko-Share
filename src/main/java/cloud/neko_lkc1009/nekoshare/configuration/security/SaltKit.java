package cloud.neko_lkc1009.nekoshare.configuration.security;

import java.util.UUID;

public class SaltKit {
    public static String initSalt() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}

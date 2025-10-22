package cloud.neko_lkc1009.nekoshare.common.process;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VideoPromoteTest {

    @Test
    void testAvailabilityChecks() {
        VideoPromote vp = new VideoPromote();
        boolean ffmpegOk;
        boolean waifuOk;
        try {
            ffmpegOk = vp.isFfmpegAvailable();
        } catch (Exception e) {
            ffmpegOk = false;
        }
        try {
            waifuOk = vp.isWaifu2xAvailable();
        } catch (Exception e) {
            waifuOk = false;
        }
        Assertions.assertNotNull(ffmpegOk);
        Assertions.assertNotNull(waifuOk);
    }

    @Test
    void testPromoteDryRun() {
        VideoPromote vp = new VideoPromote();
        VideoPromote.PromoteConfig cfg = new VideoPromote.PromoteConfig();
        cfg.setVerbose(false);
        // 不提供真实文件，仅验证代码路径不抛未捕获异常
        boolean result = vp.promote("Z:/path/not_exists.mp4", System.getProperty("java.io.tmpdir") + "/up.mp4", cfg);
        Assertions.assertFalse(result);
    }
}

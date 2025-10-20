package cloud.neko_lkc1009.nekoshare;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@MapperScans({
        @MapperScan("cloud.neko_lkc1009.nekoshare.mapper")
})
@SpringBootApplication
public class NekoShareApplication {

    public static void main(String[] args) {
        SpringApplication.run(NekoShareApplication.class, args);
    }

}

package cloud.neko_lkc1009.nekoshare.configuration.security;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.SaJwtTemplate;
import cn.dev33.satoken.jwt.SaJwtUtil;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.jwt.JWT;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class SaTokenConfiguration implements WebMvcConfigurer {
    @Bean
    public StpLogic initStpLogic() {
        return new StpLogicJwtForSimple();
    }

    @Bean
    @Primary
    public SaTokenConfig initSaTokenConfiguration() {
        SaTokenConfig saTokenConfig = new SaTokenConfig();
        saTokenConfig.setTokenName("neko-share-token");
        saTokenConfig.setTimeout(60 * 60 * 24 * 30);
        saTokenConfig.setActiveTimeout(-1);
        saTokenConfig.setIsConcurrent(true);
        saTokenConfig.setIsShare(false);
        saTokenConfig.setTokenStyle("uuid");
        saTokenConfig.setTokenPrefix("Bearer");
        saTokenConfig.setJwtSecretKey("NekoShare");
        return saTokenConfig;
    }

    @PostConstruct
    private void initJwtConfiguration() {
        SaJwtUtil.setSaJwtTemplate(new SaJwtTemplate() {
            @Override
            public String generateToken(JWT jwt, String keyt) {
                return super.generateToken(jwt, keyt);
            }
        });
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
            SaRouter.match("/**")
                    .notMatch("/api/user/login")
                    .check(StpUtil::checkLogin);
        })).addPathPatterns("/**");
    }

}

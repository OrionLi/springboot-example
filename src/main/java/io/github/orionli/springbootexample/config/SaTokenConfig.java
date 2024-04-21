package io.github.orionli.springbootexample.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * sa-token配置
 *
 * @author OrionLi
 * @date 2024/02/17
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/logout")
                .excludePathPatterns("/user/register")
                // TODO (OrionLi, 2024/3/11 21:36) knife4j相关，生产环境要删
                .excludePathPatterns("/**/doc.*",
                        "/**/swagger-ui.*",
                        "/**/swagger-resources",
                        "/**/webjars/**",
                        "/**/v3/api-docs/**");
    }

}

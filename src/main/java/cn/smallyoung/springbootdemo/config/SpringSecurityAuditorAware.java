package cn.smallyoung.springbootdemo.config;

import cn.smallyoung.springbootdemo.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * 审计，获取当前登录用户ID
 * @author smallyoung
 */
@Slf4j
@Configuration
class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(UserUtil.getCurrentAuditor());
    }

}

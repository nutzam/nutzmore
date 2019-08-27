package org.nutz.plugin.spring.boot;

import org.nutz.plugin.spring.boot.config.NgrokConfigProperties;
import org.nutz.plugins.ngrok.client.NgrokClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kerbores(kerbores@gmail.com)
 */
@Configuration
@ConditionalOnClass(NgrokClient.class)
@EnableConfigurationProperties(NgrokConfigProperties.class)
public class NgrokAutoConfiguration {

    @Value("${server.port}")
    int port;

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(name = "ngrok.token")
    public NgrokClient ngrokClient(NgrokConfigProperties ngrokConfigProperties) {
        NgrokClient client = new NgrokClient();
        client.to_port = getPort(ngrokConfigProperties);
        client.auth_token = ngrokConfigProperties.getToken();
        return client;
    }

    /**
     * @param ngrokConfigProperties
     * @return
     */
    private int getPort(NgrokConfigProperties ngrokConfigProperties) {
        if (ngrokConfigProperties.getPort() != 0) {
            return ngrokConfigProperties.getPort();
        }
        return port == 0 ? 8080 : port;
    }

}
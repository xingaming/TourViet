package fpt.aptech.api.webconfig;


import jakarta.servlet.Filter;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class WebConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofBytes(500000000L));
        factory.setMaxRequestSize(DataSize.ofBytes(500000000L));
        return factory.createMultipartConfig();
    }


}

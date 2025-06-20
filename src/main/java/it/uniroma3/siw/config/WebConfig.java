package it.uniroma3.siw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mappa /images/** a uploads/images/
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:uploads/images/");
        
        // Mantieni anche il mapping per le risorse statiche classpath
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
} 
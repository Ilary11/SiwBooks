package it.uniroma3.siw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mappa /images/** a entrambi i possibili percorsi delle immagini
        
        registry.addResourceHandler("/images/**")
                .addResourceLocations(
                    "file:uploads/images/",           // Per esecuzione da Eclipse
                    "file:SiwBooks/uploads/images/"   // Per esecuzione da SiwBooksGit
                );
        
        // Mantieni anche il mapping per le risorse statiche classpath
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
} 
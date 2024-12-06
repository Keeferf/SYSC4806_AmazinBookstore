package com.bookstore.image;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ImageConfig implements WebMvcConfigurer {

    private static final String UPLOAD_DIRECTORY = "target/uploads/";
    private static final String URL_PATH = "/uploads/**";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(URL_PATH)
                .addResourceLocations("file:" + UPLOAD_DIRECTORY);
    }

    public static String getUploadsDirectory() {
        return UPLOAD_DIRECTORY;
    }
}

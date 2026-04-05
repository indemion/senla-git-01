package ru.indemion.carservice;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import org.jspecify.annotations.Nullable;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import ru.indemion.carservice.config.AppConfig;
import ru.indemion.carservice.config.WebConfig;


public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    private static final long MAX_UPLOAD_SIZE = 5 * 1024 * 1024;

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Class<?> @Nullable [] getRootConfigClasses() {
        return new Class[]{AppConfig.class};
    }

    @Override
    protected Class<?> @Nullable [] getServletConfigClasses() {
        return new Class[]{WebConfig.class};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setMultipartConfig(new MultipartConfigElement("",
                MAX_UPLOAD_SIZE, MAX_UPLOAD_SIZE * 2L, (int) MAX_UPLOAD_SIZE / 2));
    }
}

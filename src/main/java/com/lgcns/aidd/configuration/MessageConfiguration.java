package com.lgcns.aidd.configuration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;


import java.util.Arrays;
import java.util.Locale;

@Configuration
public class MessageConfiguration {

    @Bean
    public LocaleResolver getLocalResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(new Locale("en", "US"));
        resolver.setSupportedLocales(Arrays.asList(new Locale("en", "US"), new Locale("ko", "KR")));
        return resolver;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}


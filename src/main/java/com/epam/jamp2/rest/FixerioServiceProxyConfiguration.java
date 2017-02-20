package com.epam.jamp2.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Alexey on 05.12.2016.
 */
@Configuration
public class FixerioServiceProxyConfiguration {
    @Value("${fixerio-url}")
    private String url;

    @Bean
    FixerioServiceProxy getFixerioService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(FixerioServiceProxy.class);
    }
}

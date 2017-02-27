package com.epam.jamp2;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by Northzzn on 2017/2/27.
 */
@Configuration
@ComponentScan
@PropertySource("classpath:application.properties")
public class ApplicationTestConfig {

}

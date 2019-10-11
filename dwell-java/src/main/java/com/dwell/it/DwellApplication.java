package com.dwell.it;

import com.dwell.it.enums.AppPreloadConfigEnum;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DwellApplication {

    public static void main(String[] args) {
        preLoadApplicationConfigs();

        SpringApplication.run(DwellApplication.class, args);
    }


    private static void preLoadApplicationConfigs() {
        System.setProperty(AppPreloadConfigEnum.SELENIUM_WEBDRIVER_TAG_KEY.toString(),
                AppPreloadConfigEnum.SELENIUM_WEBDRIVER_TAG_VALUE.toString());

        System.setProperty(AppPreloadConfigEnum.TOMCAT_PROPERTY_TAG_KEY.toString(),
                AppPreloadConfigEnum.TOMCAT_PROPERTY_TAG_VALUE.toString());
    }

}

package com.dwell.it.enums;

public enum AppPreloadConfigEnum {

    TOMCAT_PROPERTY_TAG_KEY("tomcat.util.http.parser.HttpParser.requestTargetAllow"),
    TOMCAT_PROPERTY_TAG_VALUE("{}"),

    SELENIUM_WEBDRIVER_TAG_KEY("webdriver.chrome.driver"),
    SELENIUM_WEBDRIVER_TAG_VALUE("./web-driver/chromedriver"),
    ;


    private final String name;

    AppPreloadConfigEnum(final String text) {
        name = text;
    }

    @Override
    public String toString() {
        return this.name;
    }

}

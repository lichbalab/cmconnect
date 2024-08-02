package com.lichbalab.cmc.spring.sdk.test;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringContextUtil {
    private static ConfigurableApplicationContext context;

    public static void startContext(String ... args) {
        if (context != null) {
            context.close();
        }
        context = SpringApplication.run(TestApplication.class, args);
    }

    public static void stopContext() {
        if (context != null) {
            context.close();
            context = null;
        }
    }

    public static ConfigurableApplicationContext getContext() {
        return context;
    }
}

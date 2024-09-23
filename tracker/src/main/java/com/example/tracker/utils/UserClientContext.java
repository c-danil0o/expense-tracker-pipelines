package com.example.tracker.utils;

import java.util.Map;

public class UserClientContext {
    private static final ThreadLocal<Map<String, String>> clientContext = new ThreadLocal<>();
    public static Map<String, String> getCurrentContext(){
        return clientContext.get();
    }

    public static void setCurrentContext(Map<String ,String> context){
        clientContext.set(context);
    }

    public static void clear(){
        clientContext.remove();
    }
}

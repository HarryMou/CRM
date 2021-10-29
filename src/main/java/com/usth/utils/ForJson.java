package com.usth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ForJson {
    public static String takeJson(Object object){
        ObjectMapper om = new ObjectMapper();
        String json="";
        try {
            json = om.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }
}

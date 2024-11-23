package com.hodos.hermes.utils;

import io.micrometer.common.util.StringUtils;

public class GeneralUtils {

    public static boolean isBlankStrings(String ... strings){
        for(String str :strings){
            if(StringUtils.isBlank(str))
                return true;
        }
        return false;
    }
}

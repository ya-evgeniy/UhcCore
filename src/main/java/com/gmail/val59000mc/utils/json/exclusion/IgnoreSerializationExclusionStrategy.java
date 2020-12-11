package com.gmail.val59000mc.utils.json.exclusion;

import com.gmail.val59000mc.utils.json.annotation.IgnoreSerialization;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class IgnoreSerializationExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(IgnoreSerialization.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return clazz.getAnnotation(IgnoreSerialization.class) != null;
    }

}

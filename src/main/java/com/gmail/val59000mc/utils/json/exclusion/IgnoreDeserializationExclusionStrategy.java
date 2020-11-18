package com.gmail.val59000mc.utils.json.exclusion;

import com.gmail.val59000mc.utils.json.annotation.IgnoreDeserialization;
import com.gmail.val59000mc.utils.json.annotation.IgnoreSerialization;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class IgnoreDeserializationExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(IgnoreDeserialization.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return clazz.getAnnotation(IgnoreDeserialization.class) != null;
    }

}

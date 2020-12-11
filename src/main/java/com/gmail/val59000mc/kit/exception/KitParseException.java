package com.gmail.val59000mc.kit.exception;

import com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager;
import com.google.gson.JsonParseException;

public class KitParseException extends JsonParseException {

    public KitParseException(String msg, Object... args) {
        super(String.format(msg, args));
    }

}

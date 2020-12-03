package com.gmail.val59000mc.kit.deserializer.stacktrace;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Deque;
import java.util.LinkedList;

public class KitDeserializeStacktrace {

    private final Path path;
    private final String identifier;

    private final Deque<String> stacktrace;

    public KitDeserializeStacktrace(@NotNull Path path, @NotNull String identifier) {
        this.path = path;
        this.identifier = identifier;

        this.stacktrace = new LinkedList<>();
    }

    private KitDeserializeStacktrace(@NotNull Path path, @NotNull String identifier, @NotNull Deque<String> stacktrace) {
        this.path = path;
        this.identifier = identifier;

        this.stacktrace = new LinkedList<>(stacktrace);
    }

    public void push(String key) {
        this.stacktrace.addLast(key);
    }

    public @NotNull String createStacktraceMessage(@NotNull String message) {
        String stacktracePath = String.join(".", this.stacktrace);
        return String.format("%s -> %s: %s", identifier, stacktracePath, message);
    }

    public KitDeserializeStacktrace copy() {
        return new KitDeserializeStacktrace(path, identifier, stacktrace);
    }

}

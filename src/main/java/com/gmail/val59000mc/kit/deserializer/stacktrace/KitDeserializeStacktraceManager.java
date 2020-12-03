package com.gmail.val59000mc.kit.deserializer.stacktrace;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Deque;
import java.util.LinkedList;

public class KitDeserializeStacktraceManager {

    private static Deque<KitDeserializeStacktrace> stacktraces = new LinkedList<>();

    public static void initialize(@NotNull Path path, @NotNull String identifier) {
        stacktraces.clear();
        stacktraces.add(new KitDeserializeStacktrace(path, identifier));
    }

    public static void begin(@NotNull String identifier) {
        if (stacktraces.isEmpty()) return;

        KitDeserializeStacktrace copy = stacktraces.getLast().copy();
        copy.push(identifier);
        stacktraces.addLast(copy);
    }

    public static void end() {
        if (!stacktraces.isEmpty()) stacktraces.removeLast();
    }

    public static @Nullable String createStacktraceMessage(@NotNull String message) {
        if (!stacktraces.isEmpty()) return stacktraces.getLast().createStacktraceMessage(message);
        return null;
    }

}

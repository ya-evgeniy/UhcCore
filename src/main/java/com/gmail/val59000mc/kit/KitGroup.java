package com.gmail.val59000mc.kit;

import org.jetbrains.annotations.NotNull;

public class KitGroup {

    private final @NotNull String id;
    private final int lines;

    public KitGroup(@NotNull String id, int lines) {
        this.id = id;
        this.lines = lines;
    }

    public @NotNull String getId() {
        return id;
    }

    public int getLines() {
        return lines;
    }

}

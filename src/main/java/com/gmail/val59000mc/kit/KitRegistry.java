package com.gmail.val59000mc.kit;

import com.gmail.val59000mc.kit.table.KitTableRegistry;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class KitRegistry {

    private final Set<Path> loadedKits = new HashSet<>();
    private final Set<Path> failureKits = new HashSet<>();

    private final Set<Path> setReferences = new HashSet<>();
    private final Set<Path> loadedSets = new HashSet<>();
    private final Set<Path> failureSets = new HashSet<>();

    private final KitTableRegistry tableRegistry = new KitTableRegistry();

    public Set<Path> getLoadedKits() {
        return loadedKits;
    }

    public Set<Path> getFailureKits() {
        return failureKits;
    }

    public Set<Path> getSetReferences() {
        return setReferences;
    }

    public Set<Path> getLoadedSets() {
        return loadedSets;
    }

    public Set<Path> getFailureSets() {
        return failureSets;
    }

    public KitTableRegistry getTableRegistry() {
        return tableRegistry;
    }

}

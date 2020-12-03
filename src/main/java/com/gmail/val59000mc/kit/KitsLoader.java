package com.gmail.val59000mc.kit;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.kit.deserializer.KitDeserializeState;
import com.gmail.val59000mc.kit.deserializer.KitsDeserializer;
import com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager;
import com.gmail.val59000mc.kit.table.set.KitTableSet;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class KitsLoader {

    public static final String JSON_EXTENSION = ".json";

    public static final String KIT_EXTENSION = ".kit" + JSON_EXTENSION;
    public static final int KIT_EXTENSION_LENGTH = KIT_EXTENSION.length();

    public static final String SET_EXTENSION = ".set" + JSON_EXTENSION;
    public static final int SET_EXTENSION_LENGTH = SET_EXTENSION.length();

    private final @NotNull KitsManager manager;

    public KitsLoader(@NotNull KitsManager manager) {
        this.manager = manager;
    }

    public void load(@NotNull Path pluginDirectoryPath) {
        Path kitsDirectoryPath = pluginDirectoryPath.resolve("kits");

        KitsDeserializer kitsDeserializer = new KitsDeserializer(this.manager);

        if (Files.exists(kitsDirectoryPath) && Files.isDirectory(kitsDirectoryPath)) {
            List<Path> toLoad = getDeepDirectoryFiles(kitsDirectoryPath);

            List<Path> kitFiles = new ArrayList<>();
            List<Path> setFiles = new ArrayList<>();

            for (Path path : toLoad) {
                String filePath = path.toAbsolutePath().toString();
                if (filePath.endsWith(KIT_EXTENSION)) {
                    kitFiles.add(path);
                }
                else if (filePath.endsWith(SET_EXTENSION)) {
                    setFiles.add(path);
                }
            }

            Logger logger = UhcCore.getPlugin().getLogger();

            KitRegistry registry = manager.getRegistry();

            List<Kit> kits = new ArrayList<>();
            List<KitTableSet> sets = new ArrayList<>();
            deserialize(kitFiles, registry.getLoadedKits(), registry.getFailureKits(), kits, kitsDirectoryPath, kitsDeserializer, Kit.class, KIT_EXTENSION_LENGTH);
            deserialize(setFiles, registry.getLoadedSets(), registry.getFailureSets(), sets, kitsDirectoryPath, kitsDeserializer, KitTableSet.class, SET_EXTENSION_LENGTH);

            if (!registry.getFailureKits().isEmpty()) logger.severe("kits has a errors: " + registry.getFailureKits());
            if (!registry.getFailureSets().isEmpty()) logger.severe("sets has a errors: " + registry.getFailureSets());

            Set<Path> setReferences = new HashSet<>(registry.getSetReferences());
            setReferences.removeAll(registry.getLoadedSets());

            if (!setReferences.isEmpty()) logger.severe("Unknown set references: " + setReferences);

            logger.info("Loaded kits: " + registry.getLoadedKits());
            logger.info("Loaded sets: " + registry.getLoadedSets());

            for (Kit kit : kits) manager.registerKit(kit);
            for (KitTableSet set : sets) manager.registerSet(set);
        }

    }

    private <T> void deserialize(List<Path> files, Set<Path> success, Set<Path> failure, List<T> result,
                             Path kitsDirectoryPath, KitsDeserializer kitsDeserializer,
                             Class<T> type, int length) {
        for (Path filePath : files) {
            Path relativePath = kitsDirectoryPath.relativize(filePath);
            String fullFileName = relativePath.toString();
            String fullName = fullFileName.substring(0, fullFileName.length() - length);

            String fileName = relativePath.getFileName().toString();
            String name = fileName.substring(0, fileName.length() - length);

            KitDeserializeState.initialize(
                    kitsDirectoryPath,
                    filePath, filePath.getParent(),
                    fullFileName, fullName,
                    fileName, name
            );

            try(BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
                result.add(kitsDeserializer.getGson().fromJson(reader, type));
                success.add(Paths.get(fullName));
            }
            catch (JsonParseException e) {
                UhcCore.getPlugin().getLogger().severe(KitDeserializeStacktraceManager.createStacktraceMessage(e.getMessage()));
                failure.add(Paths.get(fullFileName));
            }
            catch (IOException e) {
                e.printStackTrace();
                failure.add(Paths.get(fullFileName));
            }
        }
    }

    private List<Path> getDeepDirectoryFiles(@NotNull Path kitsDirectoryPath) {
        Deque<Path> queue = new LinkedList<>();
        queue.add(kitsDirectoryPath);

        List<Path> toLoad = new ArrayList<>();

        while (!queue.isEmpty()) {
            Path path = queue.removeFirst();
            if (Files.isDirectory(path)) {
                try (DirectoryStream<Path> filePaths = Files.newDirectoryStream(path)) {
                    for (Path filePath : filePaths) queue.addLast(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                toLoad.add(path);
            }

        }

        return toLoad;
    }

    public static Path resolve(Path kitsDirectoryPath, Path currentDirectory, Path path) {
        Path usedDirectory = currentDirectory;

        String pathStr = path.toString();
        if (pathStr.startsWith(File.separator)) {
            usedDirectory = kitsDirectoryPath;
            path = Paths.get(pathStr.substring(1));
        }

        return usedDirectory.resolve(path).normalize();
    }

    public static boolean checkPathAccess(Path kitDirectoryPath, Path resolvedPath) {
        return resolvedPath.startsWith(kitDirectoryPath);
    }

}

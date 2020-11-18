package com.gmail.val59000mc.kit;

import com.gmail.val59000mc.kit.deserializer.KitsDeserializer;
import com.gmail.val59000mc.kit.table.set.KitTableSet;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Deque;
import java.util.LinkedList;

public class KitsLoader {

    private final @NotNull KitsManager manager;

    public KitsLoader(@NotNull KitsManager manager) {
        this.manager = manager;
    }

    public void load(@NotNull Path pluginDirectoryPath) {
        Path kitsDirectoryPath = pluginDirectoryPath.resolve("kits");
        Path setsDirectoryPath = kitsDirectoryPath.resolve("sets");

        KitsDeserializer kitsDeserializer = new KitsDeserializer(this.manager);

        if (Files.exists(kitsDirectoryPath) && Files.isDirectory(kitsDirectoryPath)) {
            loadKits(kitsDeserializer, kitsDirectoryPath);
        }

        if (Files.exists(setsDirectoryPath) && Files.isDirectory(setsDirectoryPath)) {
            loadSets(kitsDeserializer, setsDirectoryPath);
        }

    }

    private void loadKits(@NotNull KitsDeserializer kitsDeserializer, @NotNull Path directoryPath) {
        try (DirectoryStream<Path> filePaths = Files.newDirectoryStream(directoryPath)) {
            for (Path filePath : filePaths) {
                if (Files.isDirectory(filePath)) continue;
                loadKit(kitsDeserializer, filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadKit(@NotNull KitsDeserializer kitsDeserializer, @NotNull Path kitPath) {
        Gson gson = kitsDeserializer.getGson();

        try (BufferedReader reader = Files.newBufferedReader(kitPath, StandardCharsets.UTF_8)) {
            Kit kit = gson.fromJson(reader, Kit.class);
            this.manager.registerKit(kit);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
    }

    private void loadSets(@NotNull KitsDeserializer kitsDeserializer, @NotNull Path directoryPath) {
        Deque<Path> queue = new LinkedList<>();
        queue.add(directoryPath);

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
                loadSet(kitsDeserializer, path);
            }

        }
    }

    private void loadSet(@NotNull KitsDeserializer kitsDeserializer, @NotNull Path setPath) {
        Gson gson = kitsDeserializer.getGson();

        try (BufferedReader reader = Files.newBufferedReader(setPath, StandardCharsets.UTF_8)) {
            KitTableSet set = gson.fromJson(reader, KitTableSet.class);
            manager.registerSet(set);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
    }

}

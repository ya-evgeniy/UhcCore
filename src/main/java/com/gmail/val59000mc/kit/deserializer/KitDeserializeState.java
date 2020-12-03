package com.gmail.val59000mc.kit.deserializer;

import com.gmail.val59000mc.kit.deserializer.stacktrace.KitDeserializeStacktraceManager;

import java.nio.file.Path;

public class KitDeserializeState {

    private static Path workingDirectory;

    private static Path filePath;
    private static Path parentDirectoryPath;

    private static String fullFileName;
    private static String fullName;

    private static String fileName;
    private static String name;

    public static void initialize(Path workingDirectory,
                                  Path filePath, Path parentDirectoryPath,
                                  String fullFileName, String fullName,
                                  String fileName, String name) {
        KitDeserializeStacktraceManager.initialize(filePath, fullFileName);
        KitDeserializeState.workingDirectory = workingDirectory;
        KitDeserializeState.filePath = filePath;
        KitDeserializeState.parentDirectoryPath = parentDirectoryPath;
        KitDeserializeState.fullFileName = fullFileName;
        KitDeserializeState.fullName = fullName;
        KitDeserializeState.fileName = fileName;
        KitDeserializeState.name = name;
    }

    public static Path getWorkingDirectory() {
        return workingDirectory;
    }

    public static Path getFilePath() {
        return filePath;
    }

    public static Path getParentDirectoryPath() {
        return parentDirectoryPath;
    }

    public static String getFullFileName() {
        return fullFileName;
    }

    public static String getFullName() {
        return fullName;
    }

    public static String getFileName() {
        return fileName;
    }

    public static String getName() {
        return name;
    }

}

package com.plantssoil.common.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class TempDirectoryUtility {
    private String tempDir;
    private Map<String, String> subDirs = new HashMap<>();

    public TempDirectoryUtility() {
        String location = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        if (location.charAt(2) == ':') {
            location = location.substring(1);
        }
        this.tempDir = String.format("%stemp%d", location, ThreadLocalRandom.current().nextInt(10000));
        new File(tempDir).mkdirs();
    }

    public void createSubDirectory(String subdirectoryName) {
        String subdir = String.format("%s/%s", tempDir, subdirectoryName);
        new File(subdir).mkdirs();
        subDirs.put(subdirectoryName, subdir);
    }

    public String getTempDir() {
        return this.tempDir;
    }

    public String getSubDirectory(String subdirectoryName) {
        return subDirs.get(subdirectoryName);
    }

    public void removeTempDirectory() {
        File f = new File(tempDir);
        removeFile(f);
    }

    private void removeFile(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                removeFile(file);
            }
        }
        dir.delete();
    }
}

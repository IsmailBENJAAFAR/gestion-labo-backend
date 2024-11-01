package com.example.gestion_laboratoire.test_utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class IBC {

    public static byte[] extractBytes(String ImageName) throws IOException {
        // open image
        File imgPath = new File(ImageName);
        byte[] fileContent = Files.readAllBytes(imgPath.toPath());

        return fileContent;
    }

    // TODO: Should add file size to control it even from the backend
}

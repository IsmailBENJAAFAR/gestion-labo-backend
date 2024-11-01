package com.example.gestion_laboratoire.test_utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class IBC {

    public static byte[] extractBytes(String imageName) throws IOException {
        // open image
        File imgPath = new File(imageName);
        byte[] fileContent = Files.readAllBytes(imgPath.toPath());

        return fileContent;
    }
}

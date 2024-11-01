package com.example.gestion_laboratoire.test_utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImageToBytesConverter {

    public static byte[] extractBytes(String imageName) throws IOException {
        File imgPath = new File(imageName);
        byte[] fileContent = Files.readAllBytes(imgPath.toPath());

        return fileContent;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(ImageToBytesConverter.extractBytes("cloudinary_test_images/car.jpg").length);
    }
}

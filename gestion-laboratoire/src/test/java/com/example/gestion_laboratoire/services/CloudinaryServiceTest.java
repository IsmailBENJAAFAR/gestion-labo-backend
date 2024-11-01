package com.example.gestion_laboratoire.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.gestion_laboratoire.test_utils.IBC;

@SpringBootTest(classes = CloudinaryService.class)
public class CloudinaryServiceTest {

    @Autowired
    private CloudinaryService cloudinaryService;

    private String image1Path = "/home/amidrissi/Pictures/AMI.jpeg";

    @Test
    void testCloudinaryUploadAndDeleteService() throws IOException {

        // Testing image upload
        Map<String, Object> imageInfo = cloudinaryService
                .uploadImage(IBC.extractBytes(image1Path));
        assertNotNull(imageInfo.get("url"), "the image actually saved");

        // Testing image deletion
        String message = cloudinaryService.deleteImage(String.valueOf(imageInfo.get("display_name")));
        assertEquals("Image deleted successfully", message);
    }

    @Test
    void testImageIsTooBigConstraintInCloudinaryService() throws IOException {

        // Testing when the image is bigger than 750kb
        String tooBigImage = "/home/amidrissi/Documents/wall.jpg";
        Map<String, Object> imageInfo = cloudinaryService
                .uploadImage(IBC.extractBytes(tooBigImage));
        assertEquals("Failed to upload image : Image should be less than 750kb", imageInfo.get("error"));

    }

    @Test
    void testImageNotFoundWhenDeleting() throws IOException {
        // Testing when id the given image name is not found when deleting
        String message = cloudinaryService.deleteImage(String.valueOf("someRandomName"));
        assertEquals("Unable to delete image : Image not found", message);
    }
}

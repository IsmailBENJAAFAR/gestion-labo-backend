package com.api.gestion_laboratoire.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.api.gestion_laboratoire.test_utils.ImageToBytesConverter;

@SpringBootTest(classes = CloudinaryService.class)
public class CloudinaryServiceTest {

    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setup() {
        this.cloudinaryService = new CloudinaryService();
    }

    private String image1Path = "src/test/java/com/api/gestion_laboratoire/services/cloudinary_test_images/AMI.png";

    @Test
    void testCloudinaryUploadAndDeleteService() throws IOException {

        // Testing image upload
        Map<String, Object> imageInfo = cloudinaryService
                .uploadImage(ImageToBytesConverter.extractBytes(image1Path));
        assertNotNull(imageInfo.get("url"));

        // Testing image deletion
        String message = cloudinaryService.deleteImage(String.valueOf(imageInfo.get("display_name")));
        assertEquals("Image deleted successfully", message);
    }

    @Test
    void testImageIsTooBigConstraintInCloudinaryService() throws IOException {

        // Testing when the image is bigger than 750kb
        String tooBigImage = "src/test/java/com/api/gestion_laboratoire/services/cloudinary_test_images/car.jpg";
        Map<String, Object> imageInfo = cloudinaryService
                .uploadImage(ImageToBytesConverter.extractBytes(tooBigImage));
        assertEquals("Failed to upload image : Image should be less than 750kb",
                imageInfo.get("error"));

    }

    @Test
    void testImageNotFoundWhenDeleting() throws IOException {
        // Testing when id the given image name is not found when deleting
        String message = cloudinaryService.deleteImage(String.valueOf("someRandomName"));
        assertEquals("Unable to delete image : Image not found", message);
    }
}

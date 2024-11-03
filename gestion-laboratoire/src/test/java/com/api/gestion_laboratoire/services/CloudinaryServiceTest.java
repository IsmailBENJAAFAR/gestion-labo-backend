package com.api.gestion_laboratoire.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;

@SpringBootTest(classes = CloudinaryService.class)
public class CloudinaryServiceTest {

    private CloudinaryService cloudinaryService;
    @Mock
    private Cloudinary cloudinary;
    @Mock
    private Uploader uploader;

    @BeforeEach
    void setup() {
        this.cloudinaryService = new CloudinaryService();
    }

    @Test
    void cloudinaryUploadImageNormal() throws Exception {
        // Test update action with an invalid id
        byte[] b = { 1 };

        BDDMockito.when(cloudinary.uploader()
                .upload(b, new HashMap<>())).thenReturn(new HashMap<String, Object>() {
                    {
                        put("url", "some_url");
                        put("display_name", "idk");
                    }
                });
        Map<String, Object> map = cloudinaryService.uploadImage(b);

        assertEquals("some_url", map.get("url"));
        assertEquals("idk", map.get("display_name"));
    }

    @Test
    void cloudinaryUploadImageInvalidMapEntry() throws Exception {
        // Test update action with an invalid id
        byte[] b = { 1 };

        when(cloudinary.uploader()
                .upload(b, new HashMap<>())).thenThrow(new IOException());
        Map<String, Object> map = cloudinaryService.uploadImage(b);

        assertEquals("Failed to upload image", map.get("error"));
    }

    // @Test
    // void testCloudinaryUploadAndDeleteService() throws IOException {

    // // Testing image upload
    // Map<String, Object> imageInfo = cloudinaryService
    // .uploadImage(ImageToBytesConverter.extractBytes(image1Path));
    // assertNotNull(imageInfo.get("url"), "the image actually saved");

    // // Testing image deletion
    // String message =
    // cloudinaryService.deleteImage(String.valueOf(imageInfo.get("display_name")));
    // assertEquals("Image deleted successfully", message);
    // }

    // @Test
    // void testImageIsTooBigConstraintInCloudinaryService() throws IOException {

    // // Testing when the image is bigger than 750kb
    // String tooBigImage =
    // "src/test/java/com/api/gestion_laboratoire/services/cloudinary_test_images/car.jpg";
    // Map<String, Object> imageInfo = cloudinaryService
    // .uploadImage(ImageToBytesConverter.extractBytes(tooBigImage));
    // assertEquals("Failed to upload image : Image should be less than 750kb",
    // imageInfo.get("error"));

    // }

    // @Test
    // void testImageNotFoundWhenDeleting() throws IOException {
    // // Testing when id the given image name is not found when deleting
    // String message =
    // cloudinaryService.deleteImage(String.valueOf("someRandomName"));
    // assertEquals("Unable to delete image : Image not found", message);
    // }
}

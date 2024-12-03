package com.api.gestion_laboratoire.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.cloudinary.Api;
import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;

@ExtendWith(MockitoExtension.class)
class StockageServiceTest {

    private StorageService storageService;
    @Mock
    private Cloudinary mockCloudinary;
    @Mock
    private Uploader mockUploader;
    @Mock
    private Api mockCloudinaryApi;
    @Mock
    private ApiResponse mockCloudinaryApiResp;
    private final long maxImageSize = 750000;
    private final String folder = "logos";

    @BeforeEach
    void setup() {
        this.storageService = new StorageService(mockCloudinary);
    }

    @Test
    void testUploadImageSuccess() throws IOException {
        when(mockCloudinary.uploader()).thenReturn(mockUploader);
        byte[] imageEx = new byte[2000];
        Map<String, Object> imageInfo = new HashMap<>();
        imageInfo.put("display_name", "moooooo");
        imageInfo.put("url", "https://baaaaa3");
        when(mockUploader.upload(imageEx, ObjectUtils.asMap("unique_filename", true, "folder", folder, "overwrite",
                true, "use_filename", false, "resource_type", "image"))).thenReturn(imageInfo);

        Map<String, Object> realResp = storageService.uploadImage(imageEx);
        assertEquals(imageInfo.get("display_name"), realResp.get("display_name"));
        assertEquals(imageInfo.get("url"), realResp.get("url"));
    }

    @Test
    void testUploadImageTooBig() {
        byte[] imageEx = new byte[800000];
        Map<String, Object> response = storageService.uploadImage(imageEx);

        assertEquals(
                "Failed to upload image : Image should be less than " + ((maxImageSize) / 1000) + "kb",
                response.get("error"));
    }

    @Test
    void testUploadImageException() throws IOException {
        when(mockCloudinary.uploader()).thenReturn(mockUploader);
        byte[] imageEx = new byte[2000];

        when(mockUploader.upload(imageEx, ObjectUtils.asMap("unique_filename", true,
                "folder", folder,
                "overwrite", true,
                "use_filename", false,
                "resource_type", "image"))).thenThrow(new IOException());

        Map<String, Object> response = storageService.uploadImage(imageEx);
        assertEquals(
                "Failed to upload image",
                response.get("error"));
    }

    @Test
    void testUpdateImageSuccess() throws IOException {
        when(mockCloudinary.uploader()).thenReturn(mockUploader);

        byte[] imageEx = new byte[2000];
        String imageExName = "tag";
        Map<String, Object> imageInfo = new HashMap<>();
        imageInfo.put("url", "https://baaaaa3");

        when(mockUploader.upload(imageEx, ObjectUtils.asMap("unique_filename", false,
                "folder", folder,
                "overwrite", true,
                "use_filename", true,
                "filename_override", imageExName,
                "resource_type", "image"))).thenReturn(imageInfo);

        String realImageName = storageService.uploadImage(imageExName, imageEx);
        assertEquals(imageInfo.get("url"), realImageName);
    }

    @Test
    void testUpdateImageTooBig() {
        byte[] imageEx = new byte[800000];
        String imageExName = "tag";
        String response = storageService.uploadImage(imageExName, imageEx);

        assertEquals(
                "Failed to upload image : Image should be less than " + ((maxImageSize) / 1000) + "kb",
                response);
    }

    @Test
    void testUpdateImageException() throws IOException {
        when(mockCloudinary.uploader()).thenReturn(mockUploader);
        byte[] imageEx = new byte[2000];
        String imageExName = "tag";
        when(mockUploader.upload(imageEx, ObjectUtils.asMap(
                "folder", folder,
                "use_filename", true,
                "unique_filename", false,
                "resource_type", "image",
                "filename_override", imageExName,
                "overwrite", true))).thenThrow(new IOException());

        String response = storageService.uploadImage(imageExName, imageEx);
        assertEquals(
                "Failed to upload image",
                response);
    }

    @Test
    void testDeleteImageSuccess() throws Exception {
        String imageExName = "tag";
        Map<String, Object> resp = new HashMap<>();
        resp.put(folder + "/" + imageExName, "deleted");

        when(mockCloudinary.api()).thenReturn(mockCloudinaryApi);
        when(mockCloudinaryApi.deleteResources(List.of(folder + "/" + imageExName),
                ObjectUtils.asMap("type", "upload", "resource_type", "image")))
                .thenReturn(mockCloudinaryApiResp);
        when(mockCloudinaryApiResp.get("deleted")).thenReturn(resp);

        String response = storageService.deleteImage(imageExName);
        assertEquals("Image deleted successfully", response);
    }

    @Test
    void testDeleteImageErrors() throws Exception {
        String imageExName = "tag";
        Map<String, Object> resp = new HashMap<>();
        resp.put(folder + "/" + imageExName, "not_found");

        when(mockCloudinary.api()).thenReturn(mockCloudinaryApi);
        when(mockCloudinaryApi.deleteResources(List.of(folder + "/" + imageExName),
                ObjectUtils.asMap("type", "upload", "resource_type", "image")))
                .thenReturn(mockCloudinaryApiResp);
        when(mockCloudinaryApiResp.get("deleted")).thenReturn(resp);

        String response = storageService.deleteImage(imageExName);
        assertEquals("Unable to delete image : Image not found", response);

        Map<String, Object> resp2 = new HashMap<>();
        resp2.put(folder + "/" + imageExName, "(눈_눈)");

        when(mockCloudinaryApiResp.get("deleted")).thenReturn(resp2);

        String response2 = storageService.deleteImage(imageExName);
        assertEquals("Failed to delete image", response2);
    }

    @Test
    void testDeleteImageException() throws Exception {
        when(mockCloudinary.api()).thenReturn(mockCloudinaryApi);
        String imageExName = "tag";
        Exception ex = new Exception("kaboom");
        when(mockCloudinaryApi.deleteResources(List.of(folder + "/" + imageExName),
                ObjectUtils.asMap("type", "upload", "resource_type", "image")))
                .thenThrow(ex);
        String response = storageService.deleteImage(imageExName);
        assertEquals("Failed to delete image : " + ex.getMessage(), response);
    }

}

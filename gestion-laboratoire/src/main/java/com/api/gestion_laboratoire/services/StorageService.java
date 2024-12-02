package com.api.gestion_laboratoire.services;

import com.cloudinary.*;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;
import java.util.HashMap;
import java.io.IOException;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@Service
@ComponentScan(basePackages = { "com.api.gestion_laboratoire.config" })
public class StorageService {

    private Cloudinary cloudinary;
    private String folder = "logos";
    private static final String resourceType = "image";
    private final long tolerance = 20000;
    private final long maxImageSize = 750000 + tolerance; // equivilant to 770kb, just to allow some flexibility
    private Logger logger = Logger.getLogger(getClass().getName());

    public StorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    private boolean isTooBig(byte[] imageBytes) {
        return imageBytes.length > maxImageSize;
    }

    public Map<String, Object> uploadImage(byte[] imageBytes) {
        Map<String, Object> imageInfo = new HashMap<>();

        if (isTooBig(imageBytes)) {
            imageInfo.put("error",
                    "Failed to upload image : Image should be less than " + ((maxImageSize - tolerance) / 1000) + "kb");
            return imageInfo;
        }

        try {
            @SuppressWarnings("rawtypes")
            Map response = cloudinary.uploader().upload(imageBytes,
                    ObjectUtils.asMap(
                            "folder", folder,
                            "use_filename", false,
                            "unique_filename", true,
                            "resourceType", resourceType,
                            "overwrite", true));

            imageInfo.put("display_name", response.get("display_name"));
            imageInfo.put("url", response.get("url"));
            return imageInfo;
        } catch (IOException e) {
            logger.severe(e.getMessage());
            imageInfo.put("error", "Failed to upload image");
            return imageInfo;
        }
    }

    public String uploadImage(String imageName, byte[] imageBytes) {

        if (isTooBig(imageBytes)) {
            return "Failed to upload image : Image should be less than " + ((maxImageSize - tolerance) / 1000) + "kb";
        }

        try {
            @SuppressWarnings("rawtypes")
            Map response = cloudinary.uploader().upload(imageBytes,
                    ObjectUtils.asMap(
                            "folder", folder,
                            "use_filename", true,
                            "unique_filename", false,
                            "resourceType", resourceType,
                            "filename_override", imageName,
                            "overwrite", true));

            return String.valueOf(response.get("url"));
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return "Failed to upload image";
        }
    }

    public String deleteImage(String imageName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ApiResponse apiResponse = cloudinary.api().deleteResources(Arrays.asList(folder + "/" + imageName),
                    ObjectUtils.asMap("type", "upload", "resourceType", resourceType));
            String deleteState = String.valueOf(mapper
                    .convertValue(apiResponse.get("deleted"), Map.class).get(folder + "/" + imageName));
            if (deleteState.equals("deleted"))
                return "Image deleted successfully";
            else if (deleteState.equals("not_found"))
                return "Unable to delete image : Image not found";
            else
                return "Failed to delete image";

        } catch (Exception exception) {
            logger.severe(exception.getMessage());
            return "Failed to delete image : " + exception.getMessage();
        }
    }

}

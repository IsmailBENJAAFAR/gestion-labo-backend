package com.example.gestion_laboratoire.services;

import com.cloudinary.*;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.Arrays;
import java.util.Map;
import java.util.Base64;
import java.util.HashMap;
import java.io.IOException;

import org.springframework.stereotype.Service;

@Service
public class CloudinaryService {

    private Dotenv dotenv = Dotenv.load();
    private final Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
    private String folder = "logos";
    private final long maxImageSize = 770000; // equivilant to 750kb

    private boolean isTooBig(byte[] imageBytes) {
        return imageBytes.length > maxImageSize;
    }

    @SuppressWarnings("rawtypes")
    public Map<String, Object> uploadImage(byte[] imageBytes) {
        Map<String, Object> imageInfo = new HashMap<>();

        if (isTooBig(imageBytes)) {
            imageInfo.put("error", "Failed to upload image : Image should be less than 750kb");
            return imageInfo;
        }

        Map params1 = ObjectUtils.asMap(
                "folder", folder,
                "use_filename", false,
                "unique_filename", true,
                "resource_type", "image",
                "overwrite", true);

        try {
            Map response = cloudinary.uploader().upload(imageBytes,
                    params1);
            imageInfo.put("display_name", response.get("display_name"));
            imageInfo.put("url", response.get("url"));
            return imageInfo;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            imageInfo.put("error", "Failed to upload image");
            return imageInfo;
        }
    }

    @SuppressWarnings("rawtypes")
    public String uploadImage(String imageName, byte[] imageBytes) {

        if (isTooBig(imageBytes)) {
            return "Failed to upload image : Image should be less than 750kb";
        }

        Map params1 = ObjectUtils.asMap(
                "folder", folder,
                "use_filename", true,
                "unique_filename", false,
                "resource_type", "image",
                "filename_override", imageName,
                "overwrite", true);

        try {
            Map response = cloudinary.uploader().upload(imageBytes,
                    params1);
            return String.valueOf(response.get("url"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return "Failed to upload image";
        }
    }

    @SuppressWarnings("unchecked")
    public String deleteImage(String imageName) {
        try {
            ApiResponse apiResponse = cloudinary.api().deleteResources(Arrays.asList(folder + "/" + imageName),
                    ObjectUtils.asMap("type", "upload", "resource_type", "image"));

            String delete_state = ((Map<String, String>) apiResponse.get("deleted")).get(folder + "/" + imageName);
            if (delete_state.equals("deleted"))
                return "Image deleted successfully";
            else if (delete_state.equals("not_found"))
                return "Unable to delete image : Image not found";
            else
                return "Failed to delete image";

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return "Failed to delete image : " + exception.getMessage();
        }
    }

}

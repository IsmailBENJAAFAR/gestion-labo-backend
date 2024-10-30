package com.example.gestion_laboratoire.services;

import com.cloudinary.*;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class CloudinaryService {

    private Dotenv dotenv = Dotenv.load();
    private final Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
    private String folder = "logos";

    @SuppressWarnings("rawtypes")
    public String uploadImage(String imageName, byte[] imageBytes) {
        imageName = Base64.getEncoder().withoutPadding().encodeToString(imageName.getBytes());
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
            return (String) response.get("url");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Failed to upload image : " + e.getMessage();
        }
    }

    @SuppressWarnings("unchecked")
    public String deleteImage(String imageName) {
        imageName = Base64.getEncoder().withoutPadding().encodeToString(imageName.getBytes());
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

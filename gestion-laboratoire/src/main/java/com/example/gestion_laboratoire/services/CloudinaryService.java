package com.example.gestion_laboratoire.services;

import com.cloudinary.*;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class CloudinaryService {

    private Dotenv dotenv = Dotenv.load();
    private Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
    private String folder = "logos";

    public String uploadImage(String imageName) {
        Map params1 = ObjectUtils.asMap(
                "folder", folder,
                "use_filename", true,
                "unique_filename", false,
                "resource_type", "image",
                "filename_override", imageName,
                "overwrite", true);

        try {
            Map response = cloudinary.uploader().upload(
                    IBC.extractBytes("/home/amidrissi/Pictures/AMI.jpeg"),
                    params1);
            // optimizeImage(imageName, "logos");
            return (String) response.get("url");
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload image";
        }
    }

    // TODO: Make use of the optimization in the backend maybe ?
    // @SuppressWarnings("rawtypes")
    // private static void optimizeImage(String imageName, String folder) {
    // System.out.println(
    // cloudinary.url().transformation(new Transformation().quality("auto"))
    // .imageTag(folder + "/" + imageName).toString());
    // }

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
            return "Failed to delete image";
        }
    }

    public static void main(String[] args) throws IOException {

        CloudinaryService c = new CloudinaryService();

        // System.out.println(c.uploadImage("kubo_1"));
        System.out.println(c.deleteImage("kubo_1"));
    }
}

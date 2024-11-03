package com.api.gestion_laboratoire.repositories;

import java.util.Map;

public interface GenericStorage {

    // generic upload image
    public Map<String, Object> uploadImage(byte[] imageBytes);

    // generic Image overwritting
    public String uploadImage(String imageName, byte[] imageBytes);

    // generic delete image by name
    public String deleteImage(String imageName);
}
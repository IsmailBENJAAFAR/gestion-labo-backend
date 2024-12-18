package com.api.gestion_analyse.services;

import java.util.HashMap;
import java.util.Map;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONObject;

public class ExternalCommunicationService {
    public JSONObject getLaboWithId(Long id) {
        try {
            HttpResponse<JsonNode> resp = Unirest.get("http://gestionlabo:8080/api/v1/laboratoires/" + id).asJson();
            return resp.isSuccess() ? resp.getBody().getObject() : new JSONObject();
        } catch (UnirestException e) {
            return null;
        }
    }

    public Map<Long, JSONObject> getAllLabos() {
        Map<Long, JSONObject> map = new HashMap<>();
        try {
            HttpResponse<JsonNode> resp = Unirest.get("http://gestionlabo:8080/api/v1/laboratoires").asJson();
            for (Object labo : resp.getBody().getArray()) {
                JSONObject parsedLabo = (JSONObject) labo;
                map.put(parsedLabo.getLong("id"), parsedLabo);
            }
            return map;
        } catch (UnirestException e) {
            return null;
        }
    }
}

package com.api.gestion_laboratoire.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.api.gestion_laboratoire.models.Laboratoire;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONObject;

@Service
public class LaboratoireExternalService {

    // public List<HashMap<String, Object>> LaboratoireUsers(Laboratoire laboratoire) {
    //     HttpResponse<JsonNode> contactLaboratoires = Unirest.get("http://gestionlabo:8080/api/v1/contacts")
    //             .asJson();
    //     return filterByLaboId(contactLaboratoires, laboratoire.getId());
    // }

    public List<HashMap<String, Object>> LaboratoireAnalyses(Laboratoire laboratoire) {
        HttpResponse<JsonNode> analyses = Unirest.get("http://gestionanalyse:8080/api/v1/analyses").asJson();
        return filterByLaboId(analyses, laboratoire.getId());
    }

    // public List<HashMap<String, Object>> LaboratoireContacts(Laboratoire laboratoire) {
    //     HttpResponse<JsonNode> utilisateurs = Unirest.get("http://gestionlabo:8080/api/v1/utilisateurs").asJson();
    //     return filterByLaboId(utilisateurs, laboratoire.getId());
    // }

    public Map<String, String> checkDependencies(Laboratoire laboratoire) {
        Map<String, String> map = new HashMap<>();
        try {

            List<HashMap<String, Object>> analyses = LaboratoireAnalyses(laboratoire);
            // List<HashMap<String, Object>> utilisateurs = LaboratoireUsers(laboratoire);
            // List<HashMap<String, Object>> contacts = LaboratoireContacts(laboratoire);

            if (!analyses.isEmpty())
                map.put("analyse", "you have a dependency on one or more analyses on laboratoire `" 
                + laboratoire.getNom() + "` ,id => " + laboratoire.getId());
            // if (!utilisateurs.isEmpty())
            //     map.put("utilisateur", "you have a dependency on one or more utilisateurs");
            // if (!contacts.isEmpty())
            //     map.put("contact", "you have a dependency on one or more contacts");
            System.out.println(map);
            return map;
        } catch (UnirestException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List<HashMap<String, Object>> filterByLaboId(HttpResponse<JsonNode> resp, Long laboId) {
        return (List<HashMap<String, Object>>) resp.getBody()
                .getArray().toList().stream()
                .filter(analyse -> Long
                        .valueOf((Integer) ((JSONObject) analyse).get("fkIdLaboratoire")) == laboId)
                .collect(Collectors.toList());
    }
}
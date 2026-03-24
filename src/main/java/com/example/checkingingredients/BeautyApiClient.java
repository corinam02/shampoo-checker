package com.example.checkingingredients;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class BeautyApiClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public String fetchRawIngredients(String barcode) {

        String url = "https://world.openbeautyfacts.org/api/v0/product/" + barcode + ".json";

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("product")) {
                Map<String, Object> product = (Map<String, Object>) response.get("product");
                Object ingredients = product.get("ingredients_text");
                return ingredients != null ? ingredients.toString(): "No ingredients found.";
            }
            return "No ingredients found for this barcode.";
        } catch (Exception e) {
            return "Error calling Beauty API: " + e.getMessage();
        }
    }

    public List<String> fetchIngredientList(String barcode) {
        String raw = fetchRawIngredients(barcode);
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s-> !s.isEmpty())
                .toList();
    }
}
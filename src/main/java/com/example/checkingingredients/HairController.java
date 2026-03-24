package com.example.checkingingredients;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/hair")
public class HairController {

    private final BeautyApiClient beautyApiClient;
    private final IngredientRepository ingredientRepository;

    // Constructor injection (best practice over @Autowired on fields)
    public HairController(BeautyApiClient beautyApiClient, IngredientRepository ingredientRepository) {
        this.beautyApiClient = beautyApiClient;
        this.ingredientRepository = ingredientRepository;
    }

    // Analyze a single ingredient by name
    @GetMapping("/analyze")
    public AnalysisReport analyze(@RequestParam String ingredient) {
        List<String> flags = new ArrayList<>();
        int dangerScore = 0;
        String advice;

        String lower = ingredient.toLowerCase();

        if (lower.contains("sulfate")) {
            flags.add("HARSH - Sulfate detected");
            dangerScore += 3;
        }
        if (lower.contains("silicone") || lower.contains("dimethicone")) {
            flags.add("WATCH OUT - Silicone detected");
            dangerScore += 2;
        }
        if (lower.contains("paraben")) {
            flags.add("WARNING - Paraben detected");
            dangerScore += 4;
        }
        if (lower.contains("alcohol denat")) {
            flags.add("DRYING - Denatured alcohol detected");
            dangerScore += 2;
        }

        // Also check your local database for known ingredients
        ingredientRepository.findByNameIgnoreCase(ingredient).ifPresent(dbIngredient -> {
            flags.add("DB Match: [" + dbIngredient.getDangerLevel() + "] " + dbIngredient.getDescription());
        });

        if (dangerScore == 0) {
            advice = "No common harsh chemicals detected. Looks safe!";
        } else if (dangerScore <= 3) {
            advice = "Mild concern. Use with caution if you have sensitive skin.";
        } else {
            advice = "High concern. Consider avoiding this product.";
        }

        return new AnalysisReport(ingredient, flags, dangerScore, advice);
    }

    // Analyze a full product by barcode (uses Open Beauty Facts API)
    @GetMapping("/analyze/barcode")
    public AnalysisReport analyzeByBarcode(@RequestParam String barcode) {
        List<String> allIngredients = beautyApiClient.fetchIngredientList(barcode);
        List<String> flags = new ArrayList<>();
        int totalDangerScore = 0;

        for (String ingredient : allIngredients) {
            String lower = ingredient.toLowerCase();

            if (lower.contains("sulfate"))         { flags.add("HARSH: " + ingredient); totalDangerScore += 3; }
            if (lower.contains("silicone") || lower.contains("dimethicone"))
            { flags.add("BUILDUP RISK: " + ingredient); totalDangerScore += 2; }
            if (lower.contains("paraben"))         { flags.add("WARNING: " + ingredient); totalDangerScore += 4; }
            if (lower.contains("alcohol denat"))   { flags.add("DRYING: " + ingredient); totalDangerScore += 2; }
        }

        String advice = totalDangerScore == 0 ? "Product looks clean!"
                : totalDangerScore <= 5  ? "Some mild concerns found."
                : "Multiple harsh ingredients detected. Use carefully.";

        return new AnalysisReport(barcode, flags, totalDangerScore, advice);
    }
}

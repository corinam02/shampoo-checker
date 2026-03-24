package com.example.checkingingredients;
import java.util.List;

public class AnalysisReport {
    private String productBarcode;
    private List<String> flags;
    private int dangerScore;
    private String advice;

    public AnalysisReport(String productBarcode, List<String> flags, int dangerScore, String advice) {
        this.productBarcode = productBarcode;
        this.flags = flags;
        this.dangerScore = dangerScore;
        this.advice = advice;
    }

    public String getProductBarcode() { return productBarcode; }
    public List<String> getFlags() { return flags; }
    public int getDangerScore() { return dangerScore; }
    public String getAdvice() { return advice; }
}
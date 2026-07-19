package com.savekart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProductNormalizationService {

    @Autowired
    private DataQualityValidationService dataQualityValidationService;

    private static final List<String> KNOWN_BRANDS = List.of(
            "amul", "visakha", "heritage", "jersey", "freedom",
            "apple", "samsung", "oneplus", "google", "sony",
            "lg", "xiaomi", "realme", "vivo", "oppo"
    );

    /**
     * Calculates combined matching confidence score [0.0 to 1.0] including Barcode/EAN/UPC checks
     */
    public double calculateMatchingConfidence(String title1, String title2, String barcode1, String barcode2) {
        // Direct EAN/UPC/Barcode match
        if (barcode1 != null && barcode2 != null && !barcode1.trim().isEmpty() && barcode1.equalsIgnoreCase(barcode2)) {
            return 1.0;
        }

        double similarity = calculateSimilarityScore(title1, title2);
        double cosine = calculateCosineSimilarity(title1, title2);

        return (similarity * 0.6) + (cosine * 0.4);
    }

    public String getMatchClassification(String title1, String title2, String barcode1, String barcode2) {
        double confidence = calculateMatchingConfidence(title1, title2, barcode1, barcode2);
        return dataQualityValidationService.evaluateMatchQuality(confidence);
    }

    /**
     * Calculates fuzzy matching score between two raw product titles (0.0 to 1.0)
     */
    public double calculateSimilarityScore(String title1, String title2) {
        if (title1 == null || title2 == null) return 0.0;

        String norm1 = normalizeString(title1);
        String norm2 = normalizeString(title2);

        if (norm1.equalsIgnoreCase(norm2)) return 1.0;

        // Jaccard similarity on tokens
        Set<String> set1 = new HashSet<>(Arrays.asList(norm1.split("\\s+")));
        Set<String> set2 = new HashSet<>(Arrays.asList(norm2.split("\\s+")));

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        double jaccardScore = union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();

        // Levenshtein distance similarity
        int distance = computeLevenshteinDistance(norm1, norm2);
        int maxLen = Math.max(norm1.length(), norm2.length());
        double levScore = maxLen == 0 ? 1.0 : 1.0 - ((double) distance / maxLen);

        return (jaccardScore * 0.6) + (levScore * 0.4);
    }

    /**
     * Calculates Cosine Similarity between word frequency vectors
     */
    public double calculateCosineSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null) return 0.0;

        Map<String, Integer> freq1 = getTermFrequencies(normalizeString(text1));
        Map<String, Integer> freq2 = getTermFrequencies(normalizeString(text2));

        Set<String> terms = new HashSet<>(freq1.keySet());
        terms.addAll(freq2.keySet());

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String term : terms) {
            int v1 = freq1.getOrDefault(term, 0);
            int v2 = freq2.getOrDefault(term, 0);
            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }

        if (norm1 == 0.0 || norm2 == 0.0) return 0.0;
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    public String extractBrand(String title) {
        if (title == null) return "Generic";
        String lower = title.toLowerCase();
        for (String brand : KNOWN_BRANDS) {
            if (lower.contains(brand)) {
                return brand.substring(0, 1).toUpperCase() + brand.substring(1);
            }
        }
        return "Generic";
    }

    public String normalizeWeight(String text) {
        if (text == null) return "";
        String lower = text.toLowerCase();

        // Check for liters / ml
        Matcher mlMatcher = Pattern.compile("(\\d+)\\s*ml").matcher(lower);
        if (mlMatcher.find()) {
            double ml = Double.parseDouble(mlMatcher.group(1));
            return (ml / 1000.0) + "L";
        }

        Matcher ltrMatcher = Pattern.compile("(\\d+(\\.\\d+)?)\\s*(l|ltr|liter|litre)").matcher(lower);
        if (ltrMatcher.find()) {
            return ltrMatcher.group(1) + "L";
        }

        // Check for grams / kg
        Matcher kgMatcher = Pattern.compile("(\\d+(\\.\\d+)?)\\s*kg").matcher(lower);
        if (kgMatcher.find()) {
            double kg = Double.parseDouble(kgMatcher.group(1));
            return ((long) (kg * 1000)) + "g";
        }

        Matcher gMatcher = Pattern.compile("(\\d+)\\s*g").matcher(lower);
        if (gMatcher.find()) {
            return gMatcher.group(1) + "g";
        }

        return text.trim();
    }

    public String normalizeString(String text) {
        if (text == null) return "";
        return text.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private Map<String, Integer> getTermFrequencies(String text) {
        Map<String, Integer> map = new HashMap<>();
        for (String word : text.split("\\s+")) {
            if (!word.isEmpty()) {
                map.put(word, map.getOrDefault(word, 0) + 1);
            }
        }
        return map;
    }

    private int computeLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + cost
                    );
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }
}

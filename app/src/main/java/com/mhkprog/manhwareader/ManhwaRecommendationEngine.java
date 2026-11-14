package com.mhkprog.manhwareader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/** Small dependency-free recommendation helper. */
final class ManhwaRecommendationEngine {
    private static final int DEFAULT_WEIGHT = 10;
    private static final double EXPLORATION_RATE = 0.15;

    static final class Manhwa {
        final long id;
        final String title;
        final List<String> genres;
        final boolean favorite;
        final boolean disliked;

        Manhwa(long id, String title, List<String> genres, boolean favorite, boolean disliked) {
            this.id = id;
            this.title = title == null ? "" : title;
            this.genres = genres == null ? new ArrayList<>() : new ArrayList<>(genres);
            this.favorite = favorite;
            this.disliked = disliked;
        }
    }

    static final class UserPreferences {
        final Map<String, Integer> genreWeights = new HashMap<>();

        int weightFor(String genre) {
            String key = normalize(genre);
            Integer weight = genreWeights.get(key);
            if (weight == null) {
                genreWeights.put(key, DEFAULT_WEIGHT);
                return DEFAULT_WEIGHT;
            }
            int safeWeight = Math.max(1, weight);
            genreWeights.put(key, safeWeight);
            return safeWeight;
        }
    }

    private final UserPreferences preferences;
    private final Random random = new Random();

    ManhwaRecommendationEngine(UserPreferences preferences) {
        this.preferences = preferences;
    }

    Manhwa recommendManhwa(List<Manhwa> manhwas, Set<Long> seenManhwaIds) {
        if (manhwas == null || manhwas.isEmpty()) return null;
        List<Manhwa> candidates = new ArrayList<>();
        Set<Long> ids = new HashSet<>();
        for (Manhwa manhwa : manhwas) {
            if (manhwa == null || !ids.add(manhwa.id)) continue;
            if (seenManhwaIds == null || !seenManhwaIds.contains(manhwa.id)) candidates.add(manhwa);
        }
        // Start a fresh recommendation cycle after every eligible manhwa has appeared once.
        if (candidates.isEmpty()) {
            for (Manhwa manhwa : manhwas) if (manhwa != null) candidates.add(manhwa);
            if (candidates.isEmpty()) return null;
        }
        if (candidates.size() == 1 || random.nextDouble() < EXPLORATION_RATE) {
            return candidates.get(random.nextInt(candidates.size()));
        }

        double total = 0;
        for (Manhwa manhwa : candidates) total += manhwaWeight(manhwa);
        if (total <= 0) return candidates.get(random.nextInt(candidates.size()));
        double selection = random.nextDouble() * total;
        for (Manhwa manhwa : candidates) {
            selection -= manhwaWeight(manhwa);
            if (selection < 0) return manhwa;
        }
        return candidates.get(candidates.size() - 1);
    }

    private double manhwaWeight(Manhwa manhwa) {
        Set<String> uniqueGenres = new HashSet<>();
        double totalGenreWeight = 0;
        for (String genre : manhwa.genres) {
            String key = normalize(genre);
            if (key.isEmpty() || "other".equals(key) || !uniqueGenres.add(key)) continue;
            totalGenreWeight += preferences.weightFor(key);
        }
        // Averaging prevents manhwas with many tags from receiving extra lottery tickets.
        double genreWeight = uniqueGenres.isEmpty() ? DEFAULT_WEIGHT
                : totalGenreWeight / uniqueGenres.size();
        double personalWeight = manhwa.disliked ? 0.25 : manhwa.favorite ? 1.5 : 1.0;
        return Math.max(0.05, genreWeight * personalWeight);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}

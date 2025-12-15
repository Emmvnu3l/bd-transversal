package com.noRelacional.baseDeDatos.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product_review_stats")
public class ProductReviewStats {
    
    @Id
    private Long productId;        // Mismo ID que en Oracle
    
    private Integer totalReviews = 0;
    private Double averageRating = 0.0;
    
    // Distribución de ratings: {5: 80, 4: 40, 3: 20, 2: 7, 1: 3}
    private Map<Integer, Integer> ratingDistribution = new HashMap<>();
    
    // Últimas 5 reseñas embebidas (cache para mostrar rápido)
    private List<RecentReview> recentReviews = new ArrayList<>();
}

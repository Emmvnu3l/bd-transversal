package com.noRelacional.baseDeDatos.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * Clase embebida dentro de ProductReviewStats
 * Representa un resumen ligero de las últimas reseñas para caché
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentReview {
    private String reviewId;
    private String userName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}

package com.noRelacional.baseDeDatos.service;

import com.noRelacional.baseDeDatos.dto.CreateReviewRequest;
import com.noRelacional.baseDeDatos.model.ProductReviewStats;
import com.noRelacional.baseDeDatos.model.RecentReview;
import com.noRelacional.baseDeDatos.model.Review;
import com.noRelacional.baseDeDatos.repository.ProductReviewStatsRepository;
import com.noRelacional.baseDeDatos.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ProductReviewStatsRepository statsRepository;
    
    /**
     * Crea una nueva reseña y actualiza las estadísticas del producto
     */
    @Transactional
    public Review createReview(CreateReviewRequest request) {
        log.info("Creando reseña para producto {} del usuario {}", request.getProductId(), request.getUserId());
        
        // Validar que no exista reseña previa para esta orden
        if (reviewRepository.existsByUserIdAndProductIdAndOrderId(
                request.getUserId(), request.getProductId(), request.getOrderId())) {
            throw new IllegalStateException("Ya existe una reseña para esta orden");
        }
        
        // Crear reseña
        Review review = new Review();
        review.setProductId(request.getProductId());
        review.setUserId(request.getUserId());
        review.setOrderId(request.getOrderId());
        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setComment(request.getComment());
        review.setImages(request.getImages());
        review.setProductName(request.getProductName());
        review.setUserName(request.getUserName());
        review.setHelpfulCount(0);
        review.setVerified(true);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        
        Review savedReview = reviewRepository.save(review);
        log.info("Reseña creada con ID: {}", savedReview.getId());
        
        // Actualizar estadísticas del producto
        updateProductStats(request.getProductId());
        
        return savedReview;
    }
    
    /**
     * Actualiza las estadísticas del producto (promedio, distribución, últimas 5)
     */
    private void updateProductStats(Long productId) {
        log.info("Actualizando estadísticas para producto {}", productId);
        
        // Obtener todas las reseñas del producto
        List<Review> reviews = reviewRepository.findByProductId(productId);
        
        if (reviews.isEmpty()) {
            log.warn("No hay reseñas para el producto {}", productId);
            // Si no hay reseñas, eliminar las estadísticas para evitar valores obsoletos
            statsRepository.deleteById(productId);
            log.info("Estadísticas eliminadas para producto {} por ausencia de reseñas", productId);
            return;
        }
        
        // Calcular total de reseñas
        int total = reviews.size();
        
        // Calcular promedio
        double average = reviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);
        
        // Calcular distribución de ratings (1-5)
        Map<Integer, Integer> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            final int ratingValue = i;
            int count = (int) reviews.stream()
                .filter(r -> r.getRating() == ratingValue)
                .count();
            distribution.put(i, count);
        }
        
        // Obtener últimas 5 reseñas (embebidas para caché)
        List<RecentReview> recent = reviews.stream()
            .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
            .limit(5)
            .map(r -> {
                RecentReview rr = new RecentReview();
                rr.setReviewId(r.getId());
                rr.setUserName(r.getUserName());
                rr.setRating(r.getRating());
                rr.setComment(r.getComment() != null && r.getComment().length() > 100 
                    ? r.getComment().substring(0, 100) + "..." 
                    : r.getComment());
                rr.setCreatedAt(r.getCreatedAt());
                return rr;
            })
            .collect(Collectors.toList());
        
        // Guardar o actualizar stats
        ProductReviewStats stats = statsRepository.findById(productId)
            .orElse(new ProductReviewStats());
        
        stats.setProductId(productId);
        stats.setTotalReviews(total);
        stats.setAverageRating(Math.round(average * 10.0) / 10.0); // Redondear a 1 decimal
        stats.setRatingDistribution(distribution);
        stats.setRecentReviews(recent);
        
        statsRepository.save(stats);
        log.info("Estadísticas actualizadas: {} reseñas, promedio {}", total, stats.getAverageRating());
    }
    
    /**
     * Obtiene todas las reseñas de un producto (paginadas)
     */
    public Page<Review> getProductReviews(Long productId, Pageable pageable) {
        log.info("Obteniendo reseñas del producto {} - Página {}", productId, pageable.getPageNumber());
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
    }
    
    /**
     * Obtiene las estadísticas de un producto (promedio, total, distribución, últimas 5)
     */
    public ProductReviewStats getProductStats(Long productId) {
        log.info("Obteniendo estadísticas del producto {}", productId);
        return statsRepository.findById(productId)
            .orElse(createEmptyStats(productId));
    }
    
    /**
     * Obtiene todas las reseñas de un usuario
     */
    public Page<Review> getUserReviews(Long userId, Pageable pageable) {
        log.info("Obteniendo reseñas del usuario {}", userId);
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    /**
     * Obtiene reseñas filtradas por rating
     */
    public Page<Review> getProductReviewsByRating(Long productId, Integer rating, Pageable pageable) {
        log.info("Obteniendo reseñas del producto {} con rating {}", productId, rating);
        return reviewRepository.findByProductIdAndRatingOrderByCreatedAtDesc(productId, rating, pageable);
    }
    
    /**
     * Incrementa el contador de "útil" en una reseña
     */
    @Transactional
    public Review markReviewAsHelpful(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));
        
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        review.setUpdatedAt(LocalDateTime.now());
        
        return reviewRepository.save(review);
    }
    
    /**
     * Elimina una reseña y actualiza las estadísticas
     */
    @Transactional
    public void deleteReview(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));
        
        Long productId = review.getProductId();
        reviewRepository.deleteById(reviewId);
        log.info("Reseña {} eliminada", reviewId);
        
        // Actualizar estadísticas
        updateProductStats(productId);
    }
    
    /**
     * Crea estadísticas vacías para un producto sin reseñas
     */
    private ProductReviewStats createEmptyStats(Long productId) {
        ProductReviewStats stats = new ProductReviewStats();
        stats.setProductId(productId);
        stats.setTotalReviews(0);
        stats.setAverageRating(0.0);
        
        Map<Integer, Integer> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0);
        }
        stats.setRatingDistribution(distribution);
        
        return stats;
    }
}

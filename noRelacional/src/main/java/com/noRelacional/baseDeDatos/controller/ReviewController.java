package com.noRelacional.baseDeDatos.controller;

import com.noRelacional.baseDeDatos.dto.CreateReviewRequest;
import com.noRelacional.baseDeDatos.model.ProductReviewStats;
import com.noRelacional.baseDeDatos.model.Review;
import com.noRelacional.baseDeDatos.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "API para gestión de reseñas de productos")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @PostMapping
    @Operation(summary = "Crear nueva reseña", 
               description = "Crea una reseña para un producto después de completar una orden")
    public ResponseEntity<Review> createReview(@Valid @RequestBody CreateReviewRequest request) {
        try {
            Review review = reviewService.createReview(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    @GetMapping("/product/{productId}")
    @Operation(summary = "Obtener reseñas de un producto", 
               description = "Obtiene todas las reseñas de un producto con paginación")
    public ResponseEntity<Page<Review>> getProductReviews(
            @Parameter(description = "ID del producto") @PathVariable Long productId,
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewService.getProductReviews(productId, pageable);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/product/{productId}/stats")
    @Operation(summary = "Obtener estadísticas de reseñas", 
               description = "Obtiene el promedio, total y distribución de ratings de un producto")
    public ResponseEntity<ProductReviewStats> getProductStats(
            @Parameter(description = "ID del producto") @PathVariable Long productId) {
        
        ProductReviewStats stats = reviewService.getProductStats(productId);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/product/{productId}/rating/{rating}")
    @Operation(summary = "Filtrar reseñas por rating", 
               description = "Obtiene reseñas de un producto filtradas por calificación específica")
    public ResponseEntity<Page<Review>> getProductReviewsByRating(
            @Parameter(description = "ID del producto") @PathVariable Long productId,
            @Parameter(description = "Rating (1-5)") @PathVariable Integer rating,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {
        
        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().build();
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewService.getProductReviewsByRating(productId, rating, pageable);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener reseñas de un usuario", 
               description = "Obtiene todas las reseñas realizadas por un usuario")
    public ResponseEntity<Page<Review>> getUserReviews(
            @Parameter(description = "ID del usuario") @PathVariable Long userId,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewService.getUserReviews(userId, pageable);
        return ResponseEntity.ok(reviews);
    }
    
    @PatchMapping("/{reviewId}/helpful")
    @Operation(summary = "Marcar reseña como útil", 
               description = "Incrementa el contador de usuarios que encontraron útil la reseña")
    public ResponseEntity<Review> markAsHelpful(
            @Parameter(description = "ID de la reseña") @PathVariable String reviewId) {
        
        try {
            Review review = reviewService.markReviewAsHelpful(reviewId);
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Eliminar reseña", 
               description = "Elimina una reseña y actualiza las estadísticas del producto")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "ID de la reseña") @PathVariable String reviewId) {
        
        try {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

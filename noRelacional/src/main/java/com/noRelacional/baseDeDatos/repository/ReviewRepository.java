package com.noRelacional.baseDeDatos.repository;

import com.noRelacional.baseDeDatos.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    
    /**
     * Verifica si ya existe una reseña para un usuario, producto y orden específicos
     */
    boolean existsByUserIdAndProductIdAndOrderId(Long userId, Long productId, Long orderId);
    
    /**
     * Obtiene todas las reseñas de un producto
     */
    List<Review> findByProductId(Long productId);
    
    /**
     * Obtiene todas las reseñas de un producto ordenadas por fecha (paginadas)
     */
    Page<Review> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);
    
    /**
     * Obtiene todas las reseñas de un usuario
     */
    List<Review> findByUserId(Long userId);
    
    /**
     * Obtiene todas las reseñas de un usuario (paginadas)
     */
    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * Obtiene una reseña específica por usuario y producto
     */
    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * Obtiene reseñas por calificación
     */
    Page<Review> findByProductIdAndRatingOrderByCreatedAtDesc(Long productId, Integer rating, Pageable pageable);
}

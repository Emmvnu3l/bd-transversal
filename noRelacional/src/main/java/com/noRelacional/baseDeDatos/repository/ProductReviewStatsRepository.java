package com.noRelacional.baseDeDatos.repository;

import com.noRelacional.baseDeDatos.model.ProductReviewStats;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewStatsRepository extends MongoRepository<ProductReviewStats, Long> {
    
    /**
     * Obtiene estadísticas de múltiples productos
     */
    List<ProductReviewStats> findByProductIdIn(List<Long> productIds);
}

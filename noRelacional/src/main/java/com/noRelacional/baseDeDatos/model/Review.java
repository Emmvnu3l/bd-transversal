package com.noRelacional.baseDeDatos.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
@CompoundIndex(name = "unique_user_product_order", def = "{'userId': 1, 'productId': 1, 'orderId': 1}", unique = true)
public class Review {
    
    @Id
    private String id;
    
    @Indexed
    private Long productId;        // ID del producto en Oracle
    
    @Indexed
    private Long userId;           // ID del usuario en Oracle
    
    @Indexed
    private Long orderId;          // ID de la orden en Oracle (para verificar compra)
    
    private Integer rating;        // 1-5 estrellas
    private String title;
    private String comment;
    private List<String> images = new ArrayList<>();   // URLs de imágenes subidas
    
    private Integer helpfulCount = 0;
    private Boolean verified = true;  // Siempre true si viene de orden completada
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Información desnormalizada para consultas rápidas (evitar llamadas al backend relacional)
    private String productName;
    private String userName;
}

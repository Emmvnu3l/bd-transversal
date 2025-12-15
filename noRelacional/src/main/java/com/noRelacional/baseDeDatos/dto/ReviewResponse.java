package com.noRelacional.baseDeDatos.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private String id;
    private Long productId;
    private Long userId;
    private Long orderId;
    private Integer rating;
    private String title;
    private String comment;
    private List<String> images;
    private Integer helpfulCount;
    private Boolean verified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String productName;
    private String userName;
}

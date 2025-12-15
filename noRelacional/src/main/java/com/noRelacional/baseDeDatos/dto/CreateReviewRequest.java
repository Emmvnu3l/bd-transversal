package com.noRelacional.baseDeDatos.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {
    
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;
    
    @NotNull(message = "El ID de la orden es obligatorio")
    private Long orderId;
    
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer rating;
    
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100, message = "El título no puede exceder 100 caracteres")
    private String title;
    
    @Size(max = 1000, message = "El comentario no puede exceder 1000 caracteres")
    private String comment;
    
    private List<String> images = new ArrayList<>();
    
    // Datos desnormalizados (vienen del backend relacional)
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String productName;
    
    @NotBlank(message = "El nombre del usuario es obligatorio")
    private String userName;
}

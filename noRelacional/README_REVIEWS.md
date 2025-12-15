# Sistema de Reseñas - Modelo Híbrido MongoDB

## 📚 Arquitectura Implementada

Este microservicio implementa un **modelo híbrido** de MongoDB para gestionar reseñas de productos en un ecommerce.

### 🎯 Estrategia: Referenced + Embedded Stats

#### Colecciones MongoDB:

1. **`reviews`** - Colección principal (REFERENCED)
   - Almacena todas las reseñas individuales
   - Sin límite de tamaño
   - Fácil de consultar y filtrar

2. **`product_review_stats`** - Colección de estadísticas (EMBEDDED)
   - Pre-calcula promedios y distribución
   - Cachea las últimas 5 reseñas
   - Optimiza performance de consultas frecuentes

---

## 📦 Entidades Creadas

### Review (Referenced)
```java
{
  id: "mongo-generated-id",
  productId: 123,           // ID del producto en Oracle
  userId: 456,              // ID del usuario en Oracle
  orderId: 789,             // ID de la orden en Oracle
  rating: 5,                // 1-5 estrellas
  title: "Excelente producto",
  comment: "Me encantó...",
  images: ["url1.jpg"],
  helpfulCount: 15,
  verified: true,
  createdAt: "2025-12-15T10:00:00",
  productName: "Laptop Gaming",  // Desnormalizado
  userName: "Juan Pérez"         // Desnormalizado
}
```

### ProductReviewStats (Embedded Stats)
```java
{
  productId: 123,
  totalReviews: 1523,
  averageRating: 4.3,
  ratingDistribution: {
    5: 800,
    4: 400,
    3: 200,
    2: 80,
    1: 43
  },
  recentReviews: [           // EMBEBIDO (últimas 5)
    {
      reviewId: "abc123",
      userName: "Juan Pérez",
      rating: 5,
      comment: "Excelente...",
      createdAt: "2025-12-15"
    }
  ]
}
```

---

## 🚀 Endpoints REST

### Crear Reseña
```http
POST /api/reviews
Content-Type: application/json

{
  "productId": 123,
  "userId": 456,
  "orderId": 789,
  "rating": 5,
  "title": "Excelente producto",
  "comment": "Me encantó la calidad...",
  "images": ["https://example.com/img1.jpg"],
  "productName": "Laptop Gaming",
  "userName": "Juan Pérez"
}
```

### Obtener Estadísticas (RÁPIDO - Datos Embebidos)
```http
GET /api/reviews/product/123/stats

Response:
{
  "productId": 123,
  "totalReviews": 1523,
  "averageRating": 4.3,
  "ratingDistribution": {5: 800, 4: 400, ...},
  "recentReviews": [...]  // Últimas 5 embebidas
}
```

### Obtener Todas las Reseñas (Paginadas)
```http
GET /api/reviews/product/123?page=0&size=10

Response: Page<Review>
```

### Filtrar por Rating
```http
GET /api/reviews/product/123/rating/5?page=0&size=10
```

### Reseñas de un Usuario
```http
GET /api/reviews/user/456?page=0&size=10
```

### Marcar como Útil
```http
PATCH /api/reviews/{reviewId}/helpful
```

### Eliminar Reseña
```http
DELETE /api/reviews/{reviewId}
```

---

## 🔧 Configuración

### application.properties
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/ecommerce_norel
spring.data.mongodb.auto-index-creation=true
server.port=8081

springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true
```

### Índices MongoDB (Auto-creados)
- `userId + productId + orderId` (UNIQUE) - Evita reseñas duplicadas
- `productId` - Consultas por producto
- `userId` - Consultas por usuario

---

## 💡 Ventajas del Modelo Híbrido

### ✅ REFERENCED (reviews)
- **Sin límite**: Miles de reseñas por producto
- **Consultas flexibles**: Filtrar por usuario, rating, fecha
- **No duplicación**: Datos del usuario en su propia colección

### ✅ EMBEDDED (stats)
- **Ultra rápido**: 1 query para mostrar página de producto
- **Pre-calculado**: Promedio y distribución listos
- **Caché**: Últimas 5 reseñas embebidas

---

## 📊 Flujo de Uso

```
1. Usuario completa orden → Backend Relacional (Oracle)
2. Orden status = "COMPLETADA"
3. Frontend llama POST /api/reviews (Backend NoRelacional)
4. Se crea Review en MongoDB
5. Se actualiza automáticamente ProductReviewStats
6. Frontend muestra stats (1 query rápido)
```

---

## 🧪 Testing con Swagger

1. Iniciar MongoDB: `mongod`
2. Iniciar aplicación: Run BaseDeDatosApplication
3. Abrir: http://localhost:8081/swagger-ui.html
4. Probar endpoints en orden:
   - POST /api/reviews (crear reseña)
   - GET /api/reviews/product/{id}/stats (ver estadísticas)
   - GET /api/reviews/product/{id} (ver todas)

---

## 📝 Validaciones Implementadas

- ✅ Usuario solo puede dejar 1 reseña por orden/producto
- ✅ Rating debe estar entre 1-5
- ✅ Título obligatorio (max 100 chars)
- ✅ Comentario opcional (max 1000 chars)
- ✅ Reseña marcada como "verificada" automáticamente
- ✅ Al eliminar reseña, se actualizan stats automáticamente

---

## 🎓 ¿Por qué este Modelo?

**Alternativa 1 (TODO EMBEDDED):**
```
❌ Producto con 10,000 reseñas = Excede 16MB
❌ Difícil consultar "todas las reseñas de Juan"
```

**Alternativa 2 (TODO REFERENCED):**
```
❌ Mostrar página de producto = 2+ queries (lento)
❌ Calcular promedio en cada request (ineficiente)
```

**NUESTRO MODELO (HÍBRIDO):**
```
✅ Reseñas ilimitadas en colección separada
✅ Stats pre-calculadas para consultas rápidas
✅ Últimas 5 embebidas como caché
✅ Lo mejor de ambos mundos
```

---

## 🔗 Integración con Backend Relacional

El backend relacional (Oracle) debe enviar estos datos al crear reseña:
- `productId`, `userId`, `orderId` (IDs de Oracle)
- `productName`, `userName` (desnormalizados para evitar joins)

```java
// Ejemplo en backend relacional:
RestTemplate restTemplate = new RestTemplate();
CreateReviewRequest request = new CreateReviewRequest();
request.setProductId(orden.getProductId());
request.setUserId(orden.getUserId());
request.setOrderId(orden.getId());
request.setProductName(orden.getProducto().getNombre());
request.setUserName(orden.getUsuario().getNombre());
// ... usuario completa rating, title, comment

restTemplate.postForEntity(
    "http://localhost:8081/api/reviews", 
    request, 
    Review.class
);
```

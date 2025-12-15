# Ejemplos de Uso - API de Reseñas

## 📝 Ejemplo 1: Crear Primera Reseña

```json
POST http://localhost:8081/api/reviews
Content-Type: application/json

{
  "productId": 1,
  "userId": 100,
  "orderId": 500,
  "rating": 5,
  "title": "¡Increíble laptop!",
  "comment": "Compré esta laptop para gaming y trabajo, y superó todas mis expectativas. El rendimiento es excelente.",
  "images": [
    "https://ejemplo.com/laptop1.jpg",
    "https://ejemplo.com/laptop2.jpg"
  ],
  "productName": "Laptop Gaming MSI",
  "userName": "Juan Pérez"
}
```

**Resultado:** Se crea la reseña Y se crean las estadísticas con:
- totalReviews: 1
- averageRating: 5.0
- ratingDistribution: {5: 1, 4: 0, 3: 0, 2: 0, 1: 0}

---

## 📝 Ejemplo 2: Agregar Más Reseñas

```json
POST http://localhost:8081/api/reviews

{
  "productId": 1,
  "userId": 101,
  "orderId": 501,
  "rating": 4,
  "title": "Muy buena, pero cara",
  "comment": "Excelente rendimiento, pero el precio es un poco alto.",
  "productName": "Laptop Gaming MSI",
  "userName": "María López"
}
```

```json
POST http://localhost:8081/api/reviews

{
  "productId": 1,
  "userId": 102,
  "orderId": 502,
  "rating": 5,
  "title": "Perfecta para programar",
  "comment": "La uso para desarrollo y corre todo super fluido.",
  "productName": "Laptop Gaming MSI",
  "userName": "Carlos Ruiz"
}
```

---

## 📊 Ejemplo 3: Ver Estadísticas (1 Query - RÁPIDO)

```http
GET http://localhost:8081/api/reviews/product/1/stats
```

**Response:**
```json
{
  "productId": 1,
  "totalReviews": 3,
  "averageRating": 4.7,
  "ratingDistribution": {
    "5": 2,
    "4": 1,
    "3": 0,
    "2": 0,
    "1": 0
  },
  "recentReviews": [
    {
      "reviewId": "67890...",
      "userName": "Carlos Ruiz",
      "rating": 5,
      "comment": "La uso para desarrollo y corre todo super fluido.",
      "createdAt": "2025-12-15T15:30:00"
    },
    {
      "reviewId": "67889...",
      "userName": "María López",
      "rating": 4,
      "comment": "Excelente rendimiento, pero el precio es un poco alto.",
      "createdAt": "2025-12-15T14:20:00"
    },
    {
      "reviewId": "67888...",
      "userName": "Juan Pérez",
      "rating": 5,
      "comment": "Compré esta laptop para gaming y trabajo, y superó todas mis expectativas...",
      "createdAt": "2025-12-15T10:00:00"
    }
  ]
}
```

---

## 📋 Ejemplo 4: Ver Todas las Reseñas (Paginadas)

```http
GET http://localhost:8081/api/reviews/product/1?page=0&size=10
```

**Response:**
```json
{
  "content": [
    {
      "id": "67890abc...",
      "productId": 1,
      "userId": 102,
      "orderId": 502,
      "rating": 5,
      "title": "Perfecta para programar",
      "comment": "La uso para desarrollo y corre todo super fluido.",
      "images": [],
      "helpfulCount": 0,
      "verified": true,
      "createdAt": "2025-12-15T15:30:00",
      "updatedAt": "2025-12-15T15:30:00",
      "productName": "Laptop Gaming MSI",
      "userName": "Carlos Ruiz"
    }
    // ... más reseñas
  ],
  "pageable": {...},
  "totalPages": 1,
  "totalElements": 3,
  "size": 10,
  "number": 0
}
```

---

## ⭐ Ejemplo 5: Filtrar por Rating

```http
GET http://localhost:8081/api/reviews/product/1/rating/5?page=0&size=10
```

**Response:** Solo reseñas de 5 estrellas

---

## 👤 Ejemplo 6: Ver Reseñas de un Usuario

```http
GET http://localhost:8081/api/reviews/user/100?page=0&size=10
```

**Response:** Todas las reseñas del usuario 100

---

## 👍 Ejemplo 7: Marcar Reseña como Útil

```http
PATCH http://localhost:8081/api/reviews/67890abc.../helpful
```

**Response:**
```json
{
  "id": "67890abc...",
  "helpfulCount": 1,  // Incrementado
  // ... resto de campos
}
```

---

## 🗑️ Ejemplo 8: Eliminar Reseña

```http
DELETE http://localhost:8081/api/reviews/67890abc...
```

**Resultado:** 
- Reseña eliminada
- Stats actualizadas automáticamente:
  - totalReviews: 2
  - averageRating: 4.5
  - ratingDistribution actualizada

---

## ❌ Ejemplo 9: Intento de Duplicado (Error)

```json
POST http://localhost:8081/api/reviews

{
  "productId": 1,
  "userId": 100,
  "orderId": 500,  // Misma orden que Ejemplo 1
  "rating": 3,
  "title": "Cambié de opinión",
  "comment": "...",
  "productName": "Laptop Gaming MSI",
  "userName": "Juan Pérez"
}
```

**Response:** 409 CONFLICT
```
"Ya existe una reseña para esta orden"
```

---

## 🎯 Caso de Uso Completo: Frontend

### Página de Producto

1. **Cargar stats (RÁPIDO):**
```javascript
const stats = await fetch('/api/reviews/product/1/stats');
// Mostrar: ⭐ 4.7 (3 reseñas)
// Mostrar últimas 5 reseñas embebidas
```

2. **Usuario hace clic "Ver todas":**
```javascript
const allReviews = await fetch('/api/reviews/product/1?page=0&size=10');
// Cargar todas con paginación
```

3. **Usuario filtra por 5 estrellas:**
```javascript
const fiveStars = await fetch('/api/reviews/product/1/rating/5');
```

### Perfil de Usuario

```javascript
const myReviews = await fetch('/api/reviews/user/100');
// Mostrar todas las reseñas del usuario logueado
```

### Después de Completar Orden

```javascript
// Backend relacional notifica que orden está completada
// Frontend muestra modal para dejar reseña

const newReview = await fetch('/api/reviews', {
  method: 'POST',
  body: JSON.stringify({
    productId: orden.productId,
    userId: orden.userId,
    orderId: orden.id,
    rating: 5,
    title: "...",
    comment: "...",
    productName: orden.producto.nombre,
    userName: usuario.nombre
  })
});
```

---

## 📊 Performance

### Consulta de Stats (Modelo Híbrido)
```
GET /product/1/stats
→ 1 query a product_review_stats
→ ~5ms
✅ RÁPIDO
```

### Si fuera TODO Referenced
```
GET /product/1/stats
→ 1 query a reviews
→ Calcular promedio de 10,000 reseñas en tiempo real
→ ~500ms
❌ LENTO
```

### Si fuera TODO Embedded
```
GET /product/1
→ Cargar documento de 20MB con 10,000 reseñas
→ Timeout / Error
❌ NO FUNCIONA
```

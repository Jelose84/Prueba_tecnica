# backendDev — Similar Products API (Spring Boot)

Prueba técnica para la empresa Captiole realizada por Jesús López Segura que expone un endpoint para obtener una lista de **productos similares** a un producto dado (`productId`).  
La lógica está montada con **arquitectura hexagonal**, consumiendo una **API externa** y mapeando su DTO al **modelo de dominio** con **MapStruct**.

---

## ¿Qué se ha realizado?

### 1) Arquitectura Hexagonal (Clean Architecture)
Se ha organizado el código separando responsabilidades:

- **Domain**: modelos como `ProductDetail`.
- **Application**: casos de uso y puertos que definen lo que necesita el negocio.
- **Adapters**
  - **in**: controladores REST.
  - **out**: cliente HTTP hacia una API externa.

Esto permite que:
- La lógica de negocio no dependa de frameworks.
- Cambiar la API externa o el cliente (RestTemplate/WebClient) sea más fácil.
- Los tests sean más simples y rápidos.

---

### 2) Caso de uso principal: obtener productos similares
El `GetSimilarProductsUseCase` hace el flujo:

1. Pide a `SimilarIdsPort` los IDs similares del producto base.
2. Para cada ID, pide a `ProductDetailPort` el detalle de producto.
3. Si el detalle de un ID falla, se omite ese producto y se sigue con los demás.
4. Devuelve una lista final de `ProductDetail`.

Resultado: el endpoint devuelve los productos posibles aunque alguno falle.

---

### 3) Cliente HTTP hacia la API externa
El adapter de salida `ExistingProductApiClient` se encarga de llamar a la API externa:

- `GET /product/{id}/similarids` → devuelve lista de IDs, por ejemplo: `[2,3,4]`
- `GET /product/{id}` → devuelve un JSON con el detalle del producto:
  ```json
  {"id":2,"name":"Dress","price":19.99,"availability":true}

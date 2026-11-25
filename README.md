# TechLab E-Commerce API — Proyecto Final Java Back-End

API RESTful desarrollada con **Java 17**, **Spring Boot 3**, **Spring Data JPA** y **MySQL** para la gestión de un sistema de comercio electrónico (Productos, Categorías, Usuarios y Pedidos con control automático de stock).

---

## Arquitectura del Proyecto

El código está organizado siguiendo una arquitectura modular por capas:

```text
src/main/java/com/techlab/ecommerce/
├── config/          # Configuración de CORS y carga inicial de datos (DataSeeder)
├── controller/      # Controladores REST (endpoints HTTP y códigos de estado)
├── dto/             # Objetos de transferencia de datos y validaciones de entrada
├── exception/       # Excepciones personalizadas y manejo centralizado (@RestControllerAdvice)
├── model/           # Entidades JPA (Producto, Categoria, Usuario, Pedido, LineaPedido)
├── repository/      # Interfaces Spring Data JPA para acceso a MySQL
└── service/         # Lógica de negocio, cálculos y validaciones transaccionales
```

---

## Requisitos Previos

* **Java JDK 17** o superior
* **Maven 3.8+**
* **MySQL Server 8.0+** ejecutándose en `localhost:3306`

---

## Configuración y Ejecución

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/tu-usuario/techlab-ecommerce-backend.git
   cd techlab-ecommerce-backend
   ```

2. **Configurar la base de datos en `src/main/resources/application.properties`:**
   La base de datos `techlab_ecommerce` se crea automáticamente si no existe (`createDatabaseIfNotExist=true`). Verificá que el usuario y contraseña coincidan con tu instancia local de MySQL:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/techlab_ecommerce?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=America/Argentina/Buenos_Aires&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=root
   ```

3. **Compilar y ejecutar la aplicación:**
   ```bash
   mvn spring-boot:run
   ```
   El servidor iniciará en `http://localhost:8080` y precargará categorías, productos y usuarios de prueba automáticamente si las tablas están vacías.

---

## Lista de Endpoints REST

### Productos (`/api/productos`)
| Método | Endpoint | Descripción | Código HTTP |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/productos` | Lista todos los productos. Soporta filtros `?nombre=...` y `?categoriaId=...` | `200 OK` |
| `GET` | `/api/productos/{id}` | Obtiene el detalle de un producto por ID | `200 OK` / `404 Not Found` |
| `GET` | `/api/productos/stock-bajo?limite=5` | Lista productos con stock menor o igual al límite | `200 OK` |
| `POST` | `/api/productos` | Crea un nuevo producto en el catálogo | `201 Created` / `400 Bad Request` |
| `PUT` | `/api/productos/{id}` | Actualiza los datos completos de un producto existente | `200 OK` / `400` / `404` |
| `PATCH` | `/api/productos/{id}/stock` | Actualiza únicamente el stock (`{"stock": 25}`) | `200 OK` / `400` / `404` |
| `DELETE` | `/api/productos/{id}` | Elimina un producto por ID | `204 No Content` / `404` |

**Ejemplo JSON para `POST /api/productos`:**
```json
{
  "nombre": "Café Premium",
  "descripcion": "Café colombiano tostado",
  "precio": 12500.50,
  "categoriaId": 1,
  "imagenUrl": "https://ejemplo.com/cafe.jpg",
  "stock": 100
}
```

### Categorías (`/api/categorias`)
| Método | Endpoint | Descripción | Código HTTP |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/categorias` | Lista todas las categorías | `200 OK` |
| `GET` | `/api/categorias/{id}` | Obtiene una categoría por ID | `200 OK` / `404 Not Found` |
| `POST` | `/api/categorias` | Crea una nueva categoría | `201 Created` / `400 Bad Request` |
| `PUT` | `/api/categorias/{id}` | Actualiza una categoría existente | `200 OK` / `400` / `404` |
| `DELETE` | `/api/categorias/{id}` | Elimina una categoría | `204 No Content` / `404` |

### Pedidos (`/api/pedidos`)
| Método | Endpoint | Descripción | Código HTTP |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/pedidos` | Lista todos los pedidos registrados | `200 OK` |
| `GET` | `/api/pedidos/{id}` | Obtiene el detalle de un pedido y sus líneas | `200 OK` / `404 Not Found` |
| `POST` | `/api/pedidos` | Crea un pedido, valida stock, calcula total y descuenta stock | `201 Created` / `400 Bad Request` |
| `PATCH` | `/api/pedidos/{id}/estado` | Cambia el estado (`PENDIENTE`, `CONFIRMADO`, `ENVIADO`, `ENTREGADO`, `CANCELADO`). Si se cancela, restaura el stock | `200 OK` / `400` / `404` |

**Ejemplo JSON para `POST /api/pedidos`:**
```json
{
  "usuarioId": 1,
  "itemsPedido": [
    { "productoId": 1, "cantidad": 2 },
    { "productoId": 3, "cantidad": 1 }
  ]
}
```

### Usuarios (`/api/usuarios`)
| Método | Endpoint | Descripción | Código HTTP |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/usuarios` | Lista todos los usuarios | `200 OK` |
| `GET` | `/api/usuarios/{id}` | Obtiene un usuario por ID | `200 OK` / `404 Not Found` |
| `POST` | `/api/usuarios` | Registra un nuevo usuario | `201 Created` / `400 Bad Request` |
| `GET` | `/api/usuarios/{id}/pedidos` | Obtiene el historial de pedidos de un usuario | `200 OK` / `404 Not Found` |

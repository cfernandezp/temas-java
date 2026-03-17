# MongoDB y NoSQL

---

## 1. Como estructura MongoDB la base de datos?

| SQL (Relacional) | MongoDB (NoSQL) |
|-----------------|-----------------|
| Base de datos | Base de datos |
| Tabla | **Coleccion** |
| Fila | **Documento** (JSON/BSON) |
| Columna | **Campo** |
| JOIN | Documentos embebidos o `$lookup` |
| Schema fijo | **Schema flexible** (cada documento puede tener campos distintos) |

```javascript
// Estructura MongoDB:
// Base de datos: "tienda"
//   └── Coleccion: "usuarios"
//         └── Documento:
{
    "_id": ObjectId("507f1f77bcf86cd799439011"),
    "nombre": "Carlos",
    "email": "carlos@mail.com",
    "edad": 25,
    "direccion": {              // documento embebido
        "calle": "Av. Principal 123",
        "ciudad": "Lima"
    },
    "telefonos": ["999111222", "998333444"]  // array
}
```

---

## 2. Operaciones CRUD en MongoDB

```javascript
// INSERTAR
db.usuarios.insertOne({ nombre: "Ana", email: "ana@mail.com", edad: 30 });
db.usuarios.insertMany([
    { nombre: "Luis", edad: 25 },
    { nombre: "Marta", edad: 28 }
]);

// BUSCAR
db.usuarios.find();                           // todos
db.usuarios.find({ nombre: "Ana" });          // filtro exacto
db.usuarios.findOne({ email: "ana@mail.com" }); // uno solo

// ACTUALIZAR
db.usuarios.updateOne(
    { email: "ana@mail.com" },                // filtro
    { $set: { edad: 31 } }                   // actualizacion
);

// ELIMINAR
db.usuarios.deleteOne({ email: "ana@mail.com" });
```

---

## 3. Busquedas avanzadas

```javascript
// Busqueda por RANGO de un campo
db.productos.find({ precio: { $gte: 100, $lte: 500 } });
// $gt: mayor que, $gte: mayor o igual
// $lt: menor que, $lte: menor o igual
// $ne: distinto de

// Busqueda con AND
db.usuarios.find({ edad: { $gte: 18 }, estado: "activo" });

// Busqueda con OR
db.usuarios.find({ $or: [{ ciudad: "Lima" }, { ciudad: "Bogota" }] });

// Busqueda en TEXTO (contiene)
db.usuarios.find({ nombre: { $regex: "Car", $options: "i" } }); // case insensitive

// Busqueda en ARRAYS
db.usuarios.find({ telefonos: "999111222" });  // contiene este telefono

// Ordenar y limitar
db.usuarios.find().sort({ edad: -1 }).limit(10);  // top 10 mas viejos

// Contar
db.usuarios.countDocuments({ estado: "activo" });
```

---

## 4. Schema flexible: que pasa si envio un campo extra?

```javascript
// Documento original en la coleccion:
{ "dni": "12345678", "nombre": "Carlos" }

// Si inserto un documento con campo EXTRA:
db.usuarios.insertOne({
    "dni": "87654321",
    "nombre": "Ana",
    "telefono": "999111222"   // campo extra, NO definido antes
});

// MongoDB lo ACEPTA sin error
// Schema flexible = cada documento puede tener campos distintos
// La coleccion ahora tiene:
// { "dni": "12345678", "nombre": "Carlos" }                       ← sin telefono
// { "dni": "87654321", "nombre": "Ana", "telefono": "999111222" } ← con telefono
```

> **Tip entrevista:** "MongoDB tiene schema flexible: acepta campos extras sin error. Esto es una ventaja para datos semi-estructurados, pero hay que tener cuidado porque puede generar inconsistencias. Para evitarlo, se puede usar **Schema Validation** en MongoDB o validar en la capa de aplicacion."

---

## 5. MongoDB con Spring Boot

```java
// Dependencia
// spring-boot-starter-data-mongodb

// Documento (equivalente a @Entity en JPA)
@Document(collection = "usuarios")
public class Usuario {
    @Id
    private String id;  // MongoDB genera un ObjectId automaticamente
    private String nombre;
    private String email;
    private int edad;
    private Direccion direccion; // documento embebido
}

// Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    List<Usuario> findByEdadBetween(int min, int max);
    Optional<Usuario> findByEmail(String email);

    @Query("{ 'direccion.ciudad': ?0 }")
    List<Usuario> findByCiudad(String ciudad);
}

// application.yml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/tienda
```

---

## 6. Indices en MongoDB

```javascript
// Crear indice (acelera busquedas)
db.usuarios.createIndex({ email: 1 });          // indice ascendente
db.usuarios.createIndex({ email: 1 }, { unique: true }); // indice unico
db.usuarios.createIndex({ nombre: 1, edad: -1 }); // indice compuesto

// Ver indices
db.usuarios.getIndexes();
```

---

## 7. Aggregation Pipeline

```javascript
// Agrupar ventas por producto y calcular total
db.ventas.aggregate([
    { $match: { estado: "completada" } },        // filtrar
    { $group: {                                    // agrupar
        _id: "$producto",
        totalVentas: { $sum: "$monto" },
        cantidad: { $sum: 1 }
    }},
    { $sort: { totalVentas: -1 } },               // ordenar
    { $limit: 5 }                                   // top 5
]);
```

---

## RESPUESTAS RAPIDAS - MONGODB

| Pregunta | Respuesta |
|----------|-----------|
| Como estructura MongoDB? | Base de datos → Colecciones → Documentos (JSON) |
| Equivalente a tabla? | Coleccion. Equivalente a fila: Documento |
| Schema flexible? | Cada documento puede tener campos distintos. Campo extra se acepta sin error |
| Buscar un registro? | `db.coleccion.findOne({ campo: "valor" })` |
| Busqueda por rango? | `{ campo: { $gte: 100, $lte: 500 } }` |
| MongoDB con Spring Boot? | `@Document` + `MongoRepository` (similar a JPA) |
| Cuando usar MongoDB? | Datos semi-estructurados, schema flexible, alta escala, documentos anidados |

# TESTING - JUnit 5 y Mockito

---

## 1. Anotaciones JUnit 5

| Anotacion | Uso |
|-----------|-----|
| `@Test` | Marca metodo como prueba |
| `@BeforeEach` | Antes de **cada** prueba (setup) |
| `@AfterEach` | Despues de **cada** prueba (cleanup) |
| `@BeforeAll` | Una vez antes de **todas** (static) |
| `@AfterAll` | Una vez despues de **todas** (static) |
| `@DisplayName` | Nombre descriptivo para la prueba |
| `@Disabled` | Desactiva temporalmente |
| `@Nested` | Agrupa pruebas en clases internas |
| `@ParameterizedTest` | Misma prueba con diferentes datos |
| `@ValueSource` | Provee valores para test parametrizado |

---

## 2. Assertions principales

```java
assertEquals(esperado, actual);
assertNotEquals(a, b);
assertTrue(condicion);
assertFalse(condicion);
assertNull(objeto);
assertNotNull(objeto);
assertThrows(Exception.class, () -> metodoQueFalla());
assertAll(
    () -> assertEquals("Ana", nombre),
    () -> assertEquals(25, edad)
);
```

---

## 3. Ejemplo completo con Mockito

```java
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService service;

    @Test
    @DisplayName("Debe retornar usuario cuando existe")
    void buscarPorId_existente() {
        // GIVEN (Arrange)
        Usuario usuario = new Usuario(1L, "Ana", "ana@mail.com");
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));

        // WHEN (Act)
        UsuarioDTO result = service.buscarPorId(1L);

        // THEN (Assert)
        assertNotNull(result);
        assertEquals("Ana", result.getNombre());
        verify(repository).findById(1L);          // verifica que se llamo
        verify(repository, never()).deleteById(any()); // verifica que NO se llamo
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando no existe")
    void buscarPorId_noExistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class,
            () -> service.buscarPorId(99L));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Debe rechazar nombres vacios")
    void crear_nombreVacio_falla(String nombre) {
        assertThrows(IllegalArgumentException.class,
            () -> service.crear(new UsuarioDTO(nombre, 25)));
    }
}
```

---

## 4. Mockito - Cheat Sheet

```java
// CREAR MOCKS
@Mock UsuarioRepository repo;           // mock completo
@Spy UsuarioService service;            // spy: metodos reales + puedes sobreescribir algunos
@InjectMocks UsuarioService service;    // inyecta los mocks automaticamente

// CONFIGURAR COMPORTAMIENTO
when(repo.findById(1L)).thenReturn(Optional.of(usuario));
when(repo.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
doThrow(new RuntimeException()).when(repo).deleteById(1L);

// VERIFICAR LLAMADAS
verify(repo).findById(1L);             // se llamo exactamente 1 vez
verify(repo, times(2)).save(any());    // se llamo 2 veces
verify(repo, never()).deleteById(any()); // nunca se llamo
verifyNoMoreInteractions(repo);         // no hubo mas llamadas
```

---

## 5. Test de integracion (Spring Boot)

```java
@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getUsuario_retorna200() throws Exception {
        mockMvc.perform(get("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    void crearUsuario_retorna201() throws Exception {
        String json = """
            {"nombre": "Carlos", "edad": 25}
            """;
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated());
    }
}
```

---

## 6. Mock vs Stub vs Spy

| Concepto | Que es | Ejemplo |
|----------|--------|---------|
| **Mock** | Objeto falso que simula comportamiento y **verifica interacciones** | `@Mock` de Mockito |
| **Stub** | Objeto falso que solo retorna valores predefinidos, **sin verificar** | `when(...).thenReturn(...)` |
| **Spy** | Objeto **real** que permite sobreescribir algunos metodos | `@Spy` de Mockito |

```java
// MOCK: todo es falso, defines que retornar
@Mock
private UsuarioRepository mockRepo;

@Test
void conMock() {
    // Configuras comportamiento (esto tecnicamente es un STUB dentro del mock)
    when(mockRepo.findById(1L)).thenReturn(Optional.of(usuario));

    service.buscarPorId(1L);

    // Verificas interaccion (esto es lo que hace al mock un MOCK)
    verify(mockRepo).findById(1L);
}

// STUB: solo retorna valores, no verificas llamadas
// En Mockito no hay @Stub explicito - un mock sin verify() funciona como stub
@Test
void conStub() {
    when(mockRepo.findById(1L)).thenReturn(Optional.of(usuario));
    UsuarioDTO result = service.buscarPorId(1L);
    assertEquals("Ana", result.getNombre());
    // No hay verify() → es un stub
}

// SPY: objeto REAL, puedes sobreescribir solo algunos metodos
@Spy
private UsuarioService spyService;

@Test
void conSpy() {
    // El metodo real se ejecuta EXCEPTO los que sobreescribas
    doReturn(usuarioDTO).when(spyService).buscarPorId(1L);  // sobreescrito
    spyService.otroMetodo();  // ejecuta el metodo REAL

    verify(spyService).otroMetodo();
}
```

> **Tip entrevista:** "La diferencia clave es: Mock simula todo y verifica interacciones. Stub solo provee datos de prueba sin verificar. Spy es un objeto real donde sobreescribo solo lo necesario. En Mockito, la diferencia entre mock y stub es si usas verify() o no."

---

## 7. Cobertura de tests y buenas practicas

```java
// Que testear:
// - Logica de negocio en @Service (unitarios con mocks)
// - Endpoints en @Controller (integracion con MockMvc)
// - Queries custom en @Repository (integracion con @DataJpaTest)

// Que NO testear:
// - Getters/Setters
// - Metodos generados por frameworks (JPA, Lombok)
// - Configuracion de Spring

// Naming convention para tests
@Test
void buscarPorId_conIdExistente_retornaUsuario() { }
// metodo_escenario_resultadoEsperado

// @DataJpaTest: solo carga JPA, usa H2 en memoria
@DataJpaTest
class UsuarioRepositoryTest {
    @Autowired
    private UsuarioRepository repository;

    @Test
    void findByEmail_conEmailExistente_retornaUsuario() {
        repository.save(new Usuario("Ana", "ana@mail.com"));
        Optional<Usuario> result = repository.findByEmail("ana@mail.com");
        assertTrue(result.isPresent());
    }
}
```

---

## RESPUESTAS RAPIDAS - TESTING

| Pregunta | Respuesta |
|----------|-----------|
| Que patron sigues en tests? | AAA (Arrange-Act-Assert) o Given-When-Then |
| Que es Mockito? | Framework para crear mocks (objetos simulados) de dependencias |
| @Mock vs @Spy? | Mock: todo falso. Spy: metodos reales, puedes sobreescribir algunos |
| Mock vs Stub? | Mock verifica interacciones (verify). Stub solo retorna datos sin verificar |
| Que es @InjectMocks? | Crea la instancia e inyecta automaticamente los @Mock declarados |
| Test unitario vs integracion? | Unitario: 1 clase aislada con mocks. Integracion: contexto Spring real |
| Como testeas controllers? | MockMvc para tests de integracion HTTP |
| assertThrows para que? | Verificar que un metodo lanza la excepcion esperada |

# Sistema de Gestión de Red de Bibliotecas
JAVA 17, 
Maven, 
Swing

Sistema completo para gestionar una red de bibliotecas interconectadas con estructuras de datos avanzadas para optimizar búsquedas, préstamos y envíos entre bibliotecas.

## Características Principales
### Gestión de Bibliotecas
Crear, configurar y conectar múltiples bibliotecas
Carga masiva de datos via archivos CSV
Interfaz gráfica intuitiva para gestión manual

### Sistema de Préstamos Avanzado
Préstamos con sistema de pila para deshacer operaciones
Historial completo de préstamos con tracking
Estados de libros: Disponible, En Prestamo, Recibido En Prestamo, En tránsito

### Red de Bibliotecas con Grafos
Modelado de conexiones entre bibliotecas
Algoritmos de ruta óptima (Dijkstra)
Visualización de la red completa

### Búsquedas Multi-Estructura
Árbol AVL: Búsqueda eficiente por títulos
Árbol B: Indexado por fechas de publicación
Árbol B+: Organización por géneros literarios
Tabla Hash: Acceso rápido por ISBN
Búsqueda unificada across todas las estructuras

### Sistema de Envíos Inteligente
Coordinador de envíos entre bibliotecas
Múltiples prioridades (tiempo vs costo)
Panel de tráfico en tiempo real
Tracking completo del estado de envíos

## Estructura del Proyecto

```
src/main/java/org/example/
├── Main.java                          # Punto de entrada
├── Modelos/                           # Modelos de dominio
│   ├── Biblioteca.java               # Entidad biblioteca
│   ├── Libro.java                    # Entidad libro
│   ├── ListaLibros.java              # Lista especializada
│   ├── RegistroPrestamo.java         # Registro de préstamos
│   ├── CoordinadorEnvios.java        # Coordinador de envíos
│   └── EnvioListener.java            # Patrón observer
├── GUI/                              # Interfaz gráfica
│   ├── MainWindow.java               # Ventana principal
│   ├── MainWindow2.java              # Ventana principal mejorada
│   ├── VistasGenerales/              # Vistas globales
│   │   ├── BusquedaGlobal.java       # Búsqueda en toda la red
│   │   ├── PanelInfoRed.java         # Información de la red
│   │   ├── PanelTraficoLibros2.java  # Monitor de tráfico
│   │   └── Diálogos de carga CSV...
│   ├── VistasIndividuales/           # Vistas por biblioteca
│   │   ├── BibliotecaWindow.java     # Ventana individual
│   │   ├── HistorialPrestamos.java   # Gestión de préstamos
│   │   ├── PanelEnvioLibros.java     # Panel de envíos
│   │   ├── PanelOrdenamientos.java   # Ordenamientos
│   │   └── Vistas/                   # Componentes visuales
│   │       ├── AVLViewer.java        # Visualizador AVL
│   │       ├── BViewer.java          # Visualizador Árbol B
│   │       ├── BPlusViewer.java      # Visualizador Árbol B+
│   │       ├── ListadoAlfabetico.java# Listado ordenado
│   │       └── BusquedaUnificada.java# Búsqueda integrada
│   └── VistasManuales/               # Gestión manual
│       ├── AgregarBibliotecaManual.java
│       ├── AgregarLibroManual.java
│       └── ConexionManual.java
├── Estructuras/                      # Estructuras de datos
│   ├── AVL/                          # Árbol AVL balanceado
│   │   ├── ArbolAVL.java            # Implementación AVL
│   │   ├── NodoAVL.java             # Nodo del árbol
│   │   └── Operaciones (rotaciones, búsqueda, inserción, eliminación)
│   ├── B/                            # Árbol B
│   │   ├── ArbolB.java              # Implementación B
│   │   ├── NodoB.java               # Nodo del árbol B
│   │   └── Operaciones completas
│   ├── BPlus/                        # Árbol B+
│   │   ├── ArbolBPlus.java          # Implementación B+
│   │   ├── NodoHoja.java            # Nodos hoja
│   │   ├── NodoInterno.java         # Nodos internos
│   │   └── Operaciones completas
│   ├── Grafo/                        # Sistema de grafos
│   │   ├── GrafoBibliotecas.java    # Grafo principal
│   │   ├── ListaAdyacencia.java     # Lista de adyacencia
│   │   ├── Arista.java              # Conexiones
│   │   ├── Vertice.java             # Vértices
│   │   └── RutaDijkstra.java        # Algoritmo de rutas
│   ├── TablaHash/                    # Tabla hash
│   │   ├── TablaHash.java           # Implementación
│   │   └── Iterador.java            # Iterador especializado
│   ├── Catalogo/                     # Catálogo
│   │   └── Catalogo.java            # Gestión de catálogo
│   ├── Cola.java                     # Estructura cola
│   └── Pila.java                     # Estructura pila
├── CSV/                              # Procesamiento CSV
│   ├── LectorCSVBiblioteca.java     # Carga de bibliotecas
│   ├── LectorCSVConexiones.java     # Carga de conexiones
│   └── LectorCSVLibros.java         # Carga de libros
├── AVL_Auxiliar/                     # Utilidades AVL
│   ├── IndiceISBN.java              # Índice por ISBN
│   └── NodoIndiceISBN.java          # Nodo del índice
└── include/                          # Utilidades varias
├── ExportadorDOT.java           # Exportación Graphviz
├── ExportadorDotB.java          # Exportación Árbol B
├── ExportadorDotBPlus.java      # Exportación Árbol B+
├── Ordenamientos.java           # Algoritmos ordenamiento
└── Nodo.java                    # Nodo genérico
```

## Tecnologías Utilizadas
Java 17+ - Lenguaje de programación
Maven - Gestión de dependencias y build
Swing - Interfaz gráfica de usuario
Estructuras de Datos Personalizadas - AVL, B, B+, Tablas Hash
Grafos - Modelado de redes y algoritmos de ruta


## Cómo Ejecutar
Prerrequisitos
Java JDK 17 o superior
Maven 3.6+
IDE recomendado: IntelliJ IDEA
Compilación y Ejecución


### Compilar con Maven
mvn compile
Ejecutar la aplicación

### empaquetar como JAR
mvn package
java -jar target/Biblioteca_Magica_2-1.0.jar


## Desde IntelliJ IDEA
Abrir el proyecto como proyecto Maven
Ejecutar org.example.Main como aplicación Java
Asegurarse de usar JDK 17 en las configuraciones

##  Funcionalidades Detalladas
Gestión de Datos
Carga CSV: Bibliotecas, conexiones y libros desde archivos
Gestión Manual: Agregar elementos manualmente via interfaz
Exportación: Visualización de árboles con Graphviz

## Operaciones de Biblioteca
Préstamos: Sistema completo con historial y deshacer
Búsquedas: Multi-criterio en todas las estructuras
Envíos: Entre bibliotecas con diferentes prioridades
Estadísticas: Rendimiento y métricas del sistema

## Visualizaciones
Árbol AVL: Visualización de títulos ordenados
Árbol B: Estructura por fechas de publicación
Árbol B+: Organización por géneros literarios
Red de Bibliotecas: Visualización gráfica de conexiones

## Casos de Uso
Administrador de Red: Configurar bibliotecas y conexiones
Bibliotecario: Gestionar préstamos y stock local
Usuario: Buscar libros across toda la red
Coordinador: Monitorear envíos entre bibliotecas

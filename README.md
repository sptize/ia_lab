# ia_lab

Proyecto Java para experimentar con algoritmos de búsqueda informada y búsqueda local. Incluye resolución de rutas en cuadrículas y grafos ponderados, así como una implementación de Hill Climbing para el problema de las 8 reinas.

## ¿Qué incluye?

- Búsqueda informada en cuadrículas con costos variables:
  - A*
  - Greedy Best-First
  - comparación directa entre ambos algoritmos
- Búsqueda informada en grafos ponderados
- Búsqueda local para las 8 reinas con reinicios aleatorios
- Menú interactivo en consola para probar cada caso

## Menú principal

El programa presenta estas opciones:

| Opción | Algoritmo / problema | Descripción |
|---|---|---|
| 1 | A* en laberinto | Busca un camino mínimo de costo en una cuadrícula con costes por celda. |
| 2 | Greedy Best-First en laberinto | Prioriza la heurística y puede producir rutas no óptimas. |
| 3 | Hill Climbing - 8 reinas | Resuelve el problema de las 8 reinas con reinicios aleatorios. |
| 4 | Comparar A* vs Greedy en laberinto | Ejecuta ambos sobre el mismo mapa y compara resultados. |
| 5 | A* en grafos | Calcula un camino de menor costo en un grafo ponderado. |
| 6 | Greedy Best-First en grafos | Busca un camino guiado por la heurística en grafos. |
| 7 | Comparar A* vs Greedy en un grafo | Evalúa ambos algoritmos sobre el mismo grafo. |
| 0 | Salir | Cierra el programa. |

> Minimax no está implementado y no aparece en el menú.

## Requisitos

- Java 25
- Maven

## Ejecutar el proyecto

Desde la raíz del repositorio, entra al directorio del proyecto:

```bash
cd ia_lab
```

Compila y ejecuta:

```bash
mvn package
java -cp target/classes com.busquedainformada.ia_lab.IaLab
```

También puedes compilar directamente con `javac` si lo prefieres:

```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out com.busquedainformada.ia_lab.IaLab
```

En PowerShell (Windows):

```powershell
javac -d out (Get-ChildItem -Recurse src\main\java -Filter *.java).FullName
java -cp out com.busquedainformada.ia_lab.IaLab
```

## Formato de entrada para mapas

Para las opciones de cuadrículas, puedes introducir tu propio mapa escribiendo una fila por línea. La entrada termina con una línea vacía.

Reglas:

- `S`: inicio
- `G`: meta
- `#`: pared
- `.`: terreno transitable con costo 1
- `1`-`9`: terreno transitable con el costo indicado

El mapa debe ser rectangular y contener exactamente un inicio y una meta.

Ejemplo:

```text
S999999999G
.#########.
..3........
```

La heurística utilizada es la distancia Manhattan. El costo de cada movimiento es el costo de entrar a la celda destino.

## Formato de entrada para grafos

En las opciones de grafos, el programa lee un grafo textual con secciones de nodos y aristas.

Formato de ejemplo:

```text
# ejemplo de grafo
inicio: S
meta: G
nodos:
S 6
A 2
B 4
C 3
G 0
aristas:
S A 4
A G 20
S B 2
B C 2
C G 3
```

- Las directivas `inicio:`, `meta:` y `nodos:` / `aristas:` definen la estructura del grafo.
- Cada nodo tiene un nombre y una heurística asociada.
- Cada arista se expresa como `origen destino costo`.
- La búsqueda admite grafos ponderados y la heurística depende del problema concreto.

## Estructura del proyecto

```text
ia_lab/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── ia_lab/
│                   ├── IaLab.java
│                   ├── consola/
│                   │   ├── Comando.java
│                   │   ├── ComandoBusquedaCaminos.java
│                   │   ├── ComandoBusquedaCaminosGrafos.java
│                   │   ├── ComandoOchoReinas.java
│                   │   ├── Consola.java
│                   │   └── Menu.java
│                   ├── busquedainformadadecaminos/
│                   │   ├── core/
│                   │   │   ├── BusquedaAEstrella.java
│                   │   │   ├── BusquedaPrimeroMejor.java
│                   │   │   ├── BusquedaPrimeroMejorVoraz.java
│                   │   │   ├── IAlgoritmoBusqueda.java
│                   │   │   ├── IProblemaBusqueda.java
│                   │   │   ├── Nodo.java
│                   │   │   ├── Paso.java
│                   │   │   └── ResultadoBusqueda.java
│                   │   ├── grafos/
│                   │   │   ├── Grafo.java
│                   │   │   ├── GrafoProblema.java
│                   │   │   └── ValidadorHeuristica.java
│                   │   └── laberintos/
│                   │       ├── Cuadricula.java
│                   │       ├── CuadriculaProblema.java
│                   │       ├── Direccion.java
│                   │       └── Posicion.java
│                   ├── busquedainformadalocal/
│                   │   ├── core/
│                   │   │   ├── HillClimbing.java
│                   │   │   ├── IProblemaBusquedaLocal.java
│                   │   │   └── ResultadoBusquedaLocal.java
│                   │   └── ochoreinas/
│                   │       ├── ProblemaOchoReinas.java
│                   │       └── TableroOchoReinas.java
│                   └── busquedaadversarial/
│                       └── (directorio preparado para extensiones futuras)
```

## Patrones de diseño

- Strategy: A* y Greedy Best-First implementan la misma interfaz y se intercambian fácilmente.
- Template Method: `BusquedaPrimeroMejor` encapsula el flujo general de la exploración y delega la prioridad en subclases.
- Adapter: `CuadriculaProblema` y `GrafoProblema` conectan dominios concretos con el contrato de búsqueda.
- Command: cada opción del menú está representada por un comando concreto.

## Notas académicas

Los estados deben ser inmutables durante la búsqueda y durante la vida útil del resultado. Esto es importante porque se usan como claves en estructuras tipo `HashMap` para reconstruir rutas y recuperar soluciones.

Los problemas implementados deben:

- producir transiciones con costos finitos y no negativos,
- definir una heurística finita y no negativa para los estados evaluados,
- tener un espacio de estados alcanzables finito,
- limitar el número de sucesores por estado para garantizar terminación.

A* encuentra una solución de costo mínimo cuando la heurística es admisible, es decir, cuando no sobreestima el costo mínimo restante. La admisibilidad depende del dominio concreto y no puede validarse genéricamente en tiempo de ejecución.

## Estado del proyecto

Este laboratorio está orientado a la práctica y la visualización de algoritmos de búsqueda con un enfoque didáctico. El objetivo principal es comprender cómo cambian el comportamiento y la calidad de la solución según el algoritmo, la heurística y el dominio del problema.

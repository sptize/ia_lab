# ia_lab

Proyecto de búsqueda informada y búsqueda local, implementado en Java 25. El proyecto Maven está dentro del directorio `ia_lab/`; ejecuta los comandos de compilación desde ese directorio.

## Menú interactivo

| Opción | Algoritmo | Qué hace |
|---|---|---|
| 1 | **A\*** | Busca un camino de costo mínimo en una cuadrícula con terrenos de distinto costo. |
| 2 | **Greedy Best-First** | Guía la búsqueda por la heurística; no garantiza un camino óptimo. |
| 3 | **Hill Climbing** | Resuelve el problema de las 8 reinas y permite configurar hasta 1000 reinicios aleatorios. |
| 4 | **Comparar A\* vs Greedy** | Ejecuta ambos algoritmos sobre el mismo mapa y muestra sus resultados. |

El menú permite escoger el mapa de ejemplo o introducir uno propio para las opciones de búsqueda de caminos. **Minimax aún no está implementado ni aparece en el menú.**

## Compilar y ejecutar

Se requiere Java 25. Desde la raíz del repositorio, entra primero al directorio del proyecto:

```text
cd ia_lab
```

Con Maven:

```bash
mvn package
java -cp target/classes com.busquedainformada.ia_lab.IaLab
```

También se puede compilar directamente desde el directorio `ia_lab/`:

```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out com.busquedainformada.ia_lab.IaLab
```

En Windows (PowerShell):

```powershell
javac -d out (Get-ChildItem -Recurse src\main\java -Filter *.java).FullName
java -cp out com.busquedainformada.ia_lab.IaLab
```

## Formato de entrada de mapas

Elige insertar un mapa y escribe una fila por línea. Una línea vacía termina la entrada. El mapa debe ser rectangular y contener exactamente un inicio y una meta.

- `S`: inicio
- `G`: meta
- `#`: pared
- `.`: terreno transitable de costo 1
- `1`-`9`: terreno transitable con el costo indicado

Ejemplo:

```text
S999999999G
.#########.
..3........
```

El costo de cada movimiento es el costo de entrada a la celda destino. La búsqueda permite movimientos en las cuatro direcciones; la heurística es la distancia Manhattan.

## Contrato y optimalidad de búsqueda

Los problemas que implementan `IProblemaBusqueda` deben producir transiciones
con costos finitos y no negativos y una heurística finita y no negativa para cada estado
evaluado. La búsqueda requiere un espacio de estados alcanzables finito y un
número finito de sucesores por estado para garantizar que termine. A* encuentra
una solución de costo mínimo cuando, además, la heurística es admisible (no
sobreestima el costo mínimo restante); la admisibilidad depende del problema y
no puede validarse genéricamente en tiempo de ejecución. La heurística no tiene
que ser consistente porque A* vuelve a abrir estados al encontrar un camino
mejor.

## Estructura y patrones

```text
com.busquedainformada.ia_lab
├── IaLab                         composición de comandos y punto de entrada
├── consola/
│   ├── Comando                    contrato de las opciones del menú
│   ├── ComandoBusquedaCaminos     ejecuta uno o varios algoritmos de caminos
│   ├── ComandoOchoReinas          configura y ejecuta Hill Climbing
│   ├── Consola                    fachada de entrada y salida
│   └── Menu                       presenta y ejecuta las opciones
├── algoritmos/
│   ├── IProblemaBusqueda          contrato genérico del problema
│   ├── IAlgoritmoBusqueda         contrato de los algoritmos
│   ├── BusquedaPrimeroMejor       bucle compartido (Template Method)
│   ├── BusquedaAEstrella          estrategia A*
│   └── BusquedaPrimeroMejorVoraz  estrategia Greedy Best-First
├── busquedadecaminos/
│   ├── Cuadricula                 representación y validación del mapa
│   └── CuadriculaProblema         adapta la cuadrícula al contrato de búsqueda
└── local/
    ├── HillClimbing               búsqueda local con reinicios opcionales
    └── ProblemaOchoReinas         dominio de las 8 reinas
```

- **Command:** cada opción del menú encapsula una acción.
- **Strategy:** A* y Greedy son intercambiables mediante `IAlgoritmoBusqueda`.
- **Template Method:** `BusquedaPrimeroMejor` implementa el ciclo común; sus subclases definen la prioridad y cuándo encolar un estado.
- **Adaptación del dominio:** `CuadriculaProblema` y `ProblemaOchoReinas` conectan sus dominios con los contratos de búsqueda correspondientes.

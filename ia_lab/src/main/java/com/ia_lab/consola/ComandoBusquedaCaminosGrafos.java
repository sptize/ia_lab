package com.ia_lab.consola;

import java.util.List;
import java.util.Optional;

import com.ia_lab.busquedainformadadecaminos.core.IAlgoritmoBusqueda;
import com.ia_lab.busquedainformadadecaminos.core.IProblemaBusqueda;
import com.ia_lab.busquedainformadadecaminos.core.ResultadoBusqueda;
import com.ia_lab.busquedainformadadecaminos.grafos.Grafo;
import com.ia_lab.busquedainformadadecaminos.grafos.GrafoProblema;


/**
 * Solicita un grafo, lo resuelve con los algoritmos configurados y muestra
 * las rutas encontradas.
 */
public class ComandoBusquedaCaminosGrafos implements Comando {
    private static final List<String> GRAFO_EJEMPLO = List.of(
            "# ejemplo de grafo",
            "inicio: S",
            "meta: G",
            "nodos:",
            "S 6",
            "A 2",
            "B 4",
            "C 3",
            "G 0",
            "aristas:",
            "S A 4",
            "A G 20",
            "S B 2",
            "B C 2",
            "C G 3");
    private final String titulo;
    private final List<IAlgoritmoBusqueda> algoritmos;

    /**
     * Crea un comando de búsqueda con el título y los algoritmos indicados.
     *
     * @param titulo título que se muestra en el menú
     * @param algoritmos algoritmos que se ejecutarán sobre el grafo
     */
    public ComandoBusquedaCaminosGrafos(String titulo, List<IAlgoritmoBusqueda> algoritmos) {
        this.titulo = titulo;
        this.algoritmos = List.copyOf(algoritmos);
    }

    /** {@inheritDoc} */
    @Override
    public String titulo() {
        return titulo;
    }

    /**
     * Solicita o selecciona un grafo y muestra el resultado de cada algoritmo.
     *
     * @param consola consola utilizada para recibir el grafo y mostrar los resultados
     */
    @Override
    public void execute(Consola consola) {
       Optional<Grafo> grafo = escogerGrafo(consola);
       if (grafo.isEmpty()) {
           return;
       }
       IProblemaBusqueda<String> problema = new GrafoProblema(grafo.get());
        for (IAlgoritmoBusqueda algoritmo : algoritmos) {
            consola.println();
            consola.println("--- " + algoritmo.nombre() + " ---");
            
            algoritmo.solucionar(problema).ifPresentOrElse(
                    result -> imprimirSolucion(consola, grafo.get(), result),
                    () -> consola.println(String.format(
                            "No existe camino entre %s y %s.",
                            grafo.get().getInicio(),
                            grafo.get().getMeta())));
        }
    }

    /**
     * Lee las líneas del grafo desde la consola; si no se proporciona ninguna,
     * usa el grafo de ejemplo.
     *
     * @param consola consola desde la que se leen las líneas
     * @return grafo analizado, o vacío si la entrada no es válida
     */
    private Optional<Grafo> escogerGrafo(Consola consola) {
        consola.println();
        consola.println("Ingrese el grafo a resolver (una línea por cada línea del grafo, o vacío para usar el ejemplo):");
        boolean costoOpcional = algoritmos.stream().noneMatch(IAlgoritmoBusqueda::requiereCostos);
        if (costoOpcional) {
            consola.println("Para este algoritmo el costo de las aristas es opcional: puede escribir 'A B' o 'A B 6'.");
        }
        List<String> lineas = consola.pedirLineas("Adicione detalles del grafo -inicio, meta, nodos, aristas- (línea vacía para terminar):");
        if (lineas.isEmpty()) {
            lineas = GRAFO_EJEMPLO;
        }
        try {
            return Optional.of(Grafo.parsear(lineas, costoOpcional));
        } catch (IllegalArgumentException e) {
            consola.println("Error al parsear el grafo: " + e.getMessage());
            return Optional.empty();
        }}

    /**
     * Muestra el costo, la cantidad de pasos, los nodos expandidos y las acciones
     * de una solución.
     *
     * @param consola consola en la que se imprime el resultado
     * @param grafo grafo resuelto
     * @param resultado solución encontrada por un algoritmo
     */
    private void imprimirSolucion(Consola consola, Grafo grafo, ResultadoBusqueda<String> resultado) {
        String costo = grafo.tieneCostosOmitidos()
                ? "no disponible (hay aristas sin costo)"
                : String.format("%.0f", resultado.costoTotal());
        consola.println(
            String.format("Costo total: %s | Pasos: %d | Nodos expandidos: %d",
                costo, resultado.acciones().size(), resultado.nodosExpandidos()));
        consola.println("Camino: " + String.join(" -> ", resultado.estados()));
    }
}

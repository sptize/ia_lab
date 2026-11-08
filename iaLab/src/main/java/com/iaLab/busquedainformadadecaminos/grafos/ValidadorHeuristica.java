package com.iaLab.busquedainformadadecaminos.grafos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

/**
 * Herramienta opcional para comprobar que las heurísticas de un grafo sean
 * admisibles respecto a su meta. No se ejecuta automáticamente al analizar un
 * grafo.
 */
public final class ValidadorHeuristica {

    private static final double TOLERANCIA = 1e-9;

    /** Evita instancias de esta clase de utilidades. */
    private ValidadorHeuristica() {
    }

    /**
     * Rechaza todas las heurísticas que sobreestimen el costo real mínimo hasta la meta.
     * Los nodos desde los que no se puede llegar a la meta no se consideran inadmisibles.
     *
     * @param grafo grafo cuyas heurísticas se validan
     * @throws IllegalArgumentException si una o más heurísticas son inadmisibles
     * @throws NullPointerException si {@code grafo} es {@code null}
     */
    public static void validar(Grafo grafo) {
        Objects.requireNonNull(grafo, "grafo");
        Map<String, Double> costosReales = costosRealesHastaMeta(grafo);
        List<String> errores = new ArrayList<>();

        for (String nodo : grafo.nodos()) {
            Double costoReal = costosReales.get(nodo);
            if (costoReal == null) {
                continue;
            }
            double heuristica = grafo.getHeuristica(nodo);
            if (heuristica > costoReal + TOLERANCIA) {
                errores.add("h(" + nodo + ") = " + heuristica
                        + " supera el costo real a la meta (" + costoReal + ")");
            }
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException(String.join(System.lineSeparator(), errores));
        }
    }

    /**
     * Calcula el costo mínimo desde cada nodo hasta la meta mediante Dijkstra
     * sobre las aristas invertidas.
     *
     * @param grafo grafo cuyas distancias se calculan
     * @return costos mínimos para los nodos desde los que se alcanza la meta
     */
    private static Map<String, Double> costosRealesHastaMeta(Grafo grafo) {
        Map<String, List<Grafo.Arista>> inversas = invertir(grafo);
        Map<String, Double> mejor = new HashMap<>();
        PriorityQueue<Entrada> frontera = new PriorityQueue<>(Comparator.comparingDouble(Entrada::costo));

        mejor.put(grafo.getMeta(), 0.0);
        frontera.add(new Entrada(grafo.getMeta(), 0.0));

        while (!frontera.isEmpty()) {
            Entrada actual = frontera.poll();
            if (actual.costo() > mejor.get(actual.nodo())) {
                continue;
            }

            for (Grafo.Arista arista : inversas.get(actual.nodo())) {
                double candidato = actual.costo() + arista.costo();
                if (!Double.isFinite(candidato)) {
                    continue;
                }
                Double costoConocido = mejor.get(arista.destino());
                if (costoConocido == null || candidato < costoConocido) {
                    mejor.put(arista.destino(), candidato);
                    frontera.add(new Entrada(arista.destino(), candidato));
                }
            }
        }

        return mejor;
    }

    /**
     * Invierte cada arista para que una búsqueda desde la meta recorra los
     * posibles caminos de llegada a ella.
     *
     * @param grafo grafo cuyas aristas se invierten
     * @return adyacencias invertidas, con una lista para cada nodo
     */
    private static Map<String, List<Grafo.Arista>> invertir(Grafo grafo) {
        Map<String, List<Grafo.Arista>> inversas = new LinkedHashMap<>();
        for (String nodo : grafo.nodos()) {
            inversas.put(nodo, new ArrayList<>());
        }
        for (String origen : grafo.nodos()) {
            for (Grafo.Arista arista : grafo.aristasDesde(origen)) {
                inversas.get(arista.destino()).add(new Grafo.Arista(origen, arista.costo()));
            }
        }
        return inversas;
    }

    /**
     * Entrada de la frontera priorizada de Dijkstra.
     *
     * @param nodo nodo asociado al costo acumulado
     * @param costo costo acumulado desde la meta en el grafo invertido
     */
    private record Entrada(String nodo, double costo) {
    }
}

package com.iaLab.busquedainformadadecaminos.core;

import java.util.Optional;

/** Patrón Strategy: cada algoritmo de búsqueda es intercambiable. */
public interface IAlgoritmoBusqueda {

    /**
     * Devuelve el nombre legible del algoritmo.
     *
     * @return nombre del algoritmo
     */
    String nombre();

    /**
     * Indica si el algoritmo necesita costos reales en las transiciones. A* los necesita para
     * ordenar y para garantizar el camino óptimo; Greedy Best-First solo usa la heurística.
     *
     * @return {@code true} si el algoritmo requiere costos reales (por defecto)
     */
    default boolean requiereCostos() {
        return true;
    }

    /**
     * Busca una solución para el problema indicado.
     *
     * @param <S> tipo de estado del problema
     * @param problema problema que se va a resolver
     * @return el camino encontrado, o vacío si la meta es inalcanzable
     */
    <S> Optional<ResultadoBusqueda<S>> solucionar(IProblemaBusqueda<S> problema);
}

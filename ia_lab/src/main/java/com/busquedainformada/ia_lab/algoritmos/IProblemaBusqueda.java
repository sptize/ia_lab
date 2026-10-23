package com.busquedainformada.ia_lab.algoritmos;

import java.util.List;

/**
 * Contrato de un problema de búsqueda en grafo con heurística.
 * Los algoritmos dependen solo de esta interfaz, no de un dominio concreto.
 *
 * @param <S> tipo del estado (debe implementar equals/hashCode)
 */
public interface IProblemaBusqueda<S> {

    /**
     * Devuelve el estado inicial del problema.
     *
     * @return estado inicial
     */
    S estadoInicial();

    /**
     * Indica si el estado indicado satisface la meta.
     *
     * @param estado estado que se evalúa
     * @return {@code true} si el estado es meta
     */
    boolean esMeta(S estado);

    /**
     * Devuelve los estados alcanzables desde el estado indicado, junto con la acción y el costo de cada paso.
     *
     * @param estado estado cuyos sucesores se solicitan
     * @return transiciones disponibles desde el estado
     */
    List<Paso<S>> sucesores(S estado);

    /**
     * Estima el costo restante desde el estado hasta la meta.
     *
     * @param estado estado cuya distancia estimada se calcula
     * @return estimación heurística del costo restante
     */
    double heuristica(S estado);
}

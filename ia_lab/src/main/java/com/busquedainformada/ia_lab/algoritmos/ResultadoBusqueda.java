package com.busquedainformada.ia_lab.algoritmos;

import java.util.ArrayList;
import java.util.List;

/**
 * Solución de una búsqueda.
 *
 * @param <S> tipo de estado del problema
 * @param estados        estados del camino, desde el inicial hasta la meta
 * @param acciones       acciones aplicadas (siempre {@code estados.size() - 1})
 * @param costoTotal     costo acumulado g(meta)
 * @param nodosExpandidos cantidad de nodos expandidos
 */
public record ResultadoBusqueda<S>(List<S> estados, List<String> acciones, double costoTotal, int nodosExpandidos) {

    public ResultadoBusqueda {
        estados = List.copyOf(estados);
        acciones = List.copyOf(acciones);
    }

    /**
     * Construye el resultado a partir del nodo objetivo y reconstruye su camino.
     *
     * @param <S> tipo de estado del problema
     * @param objetivo nodo que representa el estado meta
     * @param nodosExpandidos cantidad de nodos expandidos durante la búsqueda
     * @return resultado con los estados, acciones y costo del camino encontrado
     */
    static <S> ResultadoBusqueda<S> deObjetivo(Nodo<S> objetivo, int nodosExpandidos) {
        List<S> estados = new ArrayList<>();
        List<String> acciones = new ArrayList<>();
        for (Nodo<S> nodo : objetivo.caminoDesdeLaRaiz()) {
            estados.add(nodo.estado());
            if (nodo.accion() != null) {
                acciones.add(nodo.accion());
            }
        }
        return new ResultadoBusqueda<>(estados, acciones, objetivo.costoCamino(), nodosExpandidos);
    }
}

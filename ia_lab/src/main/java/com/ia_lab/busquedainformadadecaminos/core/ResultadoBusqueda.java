package com.ia_lab.busquedainformadadecaminos.core;

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
 *
 * <p>Las listas se copian y son inmutables, pero sus estados no se clonan:
 * el resultado conserva las mismas referencias recibidas. Los estados deben
 * ser inmutables para que el camino no cambie después de la búsqueda.</p>
 *
 * <p>El camino debe contener al menos un estado, debe haber exactamente una
 * acción por transición y el costo total y el contador de nodos expandidos
 * deben ser válidos (costo finito no negativo y contador no negativo).</p>
 */
public record ResultadoBusqueda<S>(List<S> estados, List<String> acciones, double costoTotal, int nodosExpandidos) {

    public ResultadoBusqueda {
        estados = List.copyOf(estados);
        acciones = List.copyOf(acciones);
        if (estados.isEmpty()) {
            throw new IllegalArgumentException("el camino debe incluir al menos el estado inicial");
        }
        if (acciones.size() != estados.size() - 1) {
            throw new IllegalArgumentException("debe haber una acción entre cada par de estados");
        }
        if (!Double.isFinite(costoTotal) || costoTotal < 0) {
            throw new IllegalArgumentException("el costo total debe ser finito y no negativo");
        }
        if (nodosExpandidos < 0) {
            throw new IllegalArgumentException("los nodos expandidos no pueden ser negativos");
        }
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

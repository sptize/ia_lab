package com.ia_lab.busquedainformadadecaminos.core;

/**
 * Transición hacia un estado sucesor.
 *
 * @param <S> tipo del estado
 * @param accion acción que produce la transición
 * @param estado estado sucesor
 * @param costo costo finito y no negativo de la transición
 */
public record Paso<S>(String accion, S estado, double costo) {

    public Paso {
        if (!Double.isFinite(costo) || costo < 0) {
            throw new IllegalArgumentException("el costo de un paso debe ser finito y no negativo");
        }
    }
}

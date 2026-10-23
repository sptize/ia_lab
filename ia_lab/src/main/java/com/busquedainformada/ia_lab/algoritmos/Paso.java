package com.busquedainformada.ia_lab.algoritmos;

/**
 * Transición hacia un estado sucesor.
 *
 * @param <S> tipo del estado
 * @param accion acción que produce la transición
 * @param estado estado sucesor
 * @param costo costo de la transición
 */
public record Paso<S>(String accion, S estado, double costo) {
}

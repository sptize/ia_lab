package com.busquedainformada.ia_lab.local;

/**
 * @param <S> tipo de estado del problema
 * @param estado solución, o mejor estado alcanzado si no se resolvió
 * @param costo costo de {@code estado}
 * @param solucion indica si se alcanzó un estado con costo cero
 * @param pasos movimientos totales realizados, sumando todos los intentos
 * @param reinicios reinicios aleatorios realizados
 */
public record ResultadoBusquedaLocal<S>(S estado, double costo, boolean solucion, int pasos, int reinicios) {
}

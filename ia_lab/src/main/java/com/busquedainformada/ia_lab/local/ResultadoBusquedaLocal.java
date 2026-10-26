package com.busquedainformada.ia_lab.local;

import java.util.Objects;

/**
 * @param <S> tipo de estado del problema
 * @param estado solución, o mejor estado alcanzado si no se resolvió
 * @param costo costo finito de {@code estado}
 * @param solucion indica si se alcanzó una solución; debe tener costo cero
 * @param pasos movimientos totales realizados, sumando todos los intentos
 * @param reinicios reinicios aleatorios realizados
 *
 * <p>Los contadores no pueden ser negativos. El costo de un resultado fallido
 * no se restringe a valores no negativos porque este tipo es genérico y solo
 * exige que sea finito.</p>
 */
public record ResultadoBusquedaLocal<S>(S estado, double costo, boolean solucion, int pasos, int reinicios) {

    public ResultadoBusquedaLocal {
        Objects.requireNonNull(estado, "estado");
        if (!Double.isFinite(costo)) {
            throw new IllegalArgumentException("el costo debe ser finito");
        }
        if (solucion && costo != 0) {
            throw new IllegalArgumentException("una solución debe tener costo cero");
        }
        if (pasos < 0 || reinicios < 0) {
            throw new IllegalArgumentException("los pasos y reinicios no pueden ser negativos");
        }
    }
}

package com.busquedainformada.ia_lab.local;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Hill Climbing de máxima pendiente (steepest-ascent) con reinicios aleatorios opcionales.
 * En cada paso se mueve al mejor vecino; si ninguno mejora el estado actual, está en un
 * óptimo local y (si quedan reinicios) vuelve a empezar desde un estado aleatorio.
 */
public final class HillClimbing<S> {

    private final Random aleatorio;

    /**
     * Crea el algoritmo con el generador aleatorio indicado.
     *
     * @param aleatorio generador utilizado para seleccionar estados iniciales y desempatar vecinos
     */
    public HillClimbing(Random aleatorio) {
        this.aleatorio = Objects.requireNonNull(aleatorio, "aleatorio");
    }

    /**
     * Busca una solución mediante hill climbing y reinicios aleatorios opcionales.
     *
     * @param problema problema de búsqueda local que se va a resolver
     * @param reiniciosMaximos reinicios permitidos tras quedar atascado (0 equivale a un solo intento)
     * @return resultado con la solución o el mejor estado encontrado
     * @throws IllegalArgumentException si {@code reiniciosMaximos} es negativo
     */
    public ResultadoBusquedaLocal<S> solucionar(IProblemaBusquedaLocal<S> problema, int reiniciosMaximos) {
        if (reiniciosMaximos < 0) {
            throw new IllegalArgumentException("los reinicios máximos no puede ser negativos");
        }
        int pasosTotales = 0;
        S mejor = null;
        double mejorCosto = Double.POSITIVE_INFINITY;

        for (int reinicio = 0; reinicio <= reiniciosMaximos; reinicio++) {
            S actual = problema.estadoAleatorio(aleatorio);
            double costoActual = problema.costo(actual);

            while (!problema.esSolucion(actual)) {
                S proximo = mejorVecino(problema, actual, costoActual);
                if (proximo == null) {
                    break; // óptimo local
                }
                actual = proximo;
                costoActual = problema.costo(actual);
                pasosTotales++;
            }

            if (costoActual < mejorCosto) {
                mejor = actual;
                mejorCosto = costoActual;
            }
            if (problema.esSolucion(actual)) {
                return new ResultadoBusquedaLocal<>(actual, costoActual, true, pasosTotales, reinicio);
            }
        }
        return new ResultadoBusquedaLocal<>(mejor, mejorCosto, false, pasosTotales, reiniciosMaximos);
    }

    /**
     * Busca un vecino con costo estrictamente menor al actual y elige al azar entre los mejores empatados.
     *
     * @param problema problema que define los vecinos y sus costos
     * @param actual estado actual
     * @param costoActual costo del estado actual
     * @return mejor vecino, o {@code null} si ninguno mejora el estado actual
     */
    private S mejorVecino(IProblemaBusquedaLocal<S> problema, S actual, double costoActual) {
        double mejorCosto = costoActual;
        List<S> candidatos = new ArrayList<>();
        for (S vecino : problema.vecinos(actual)) {
            double costo = problema.costo(vecino);
            if (costo < mejorCosto) {
                mejorCosto = costo;
                candidatos.clear();
                candidatos.add(vecino);
            } else if (costo == mejorCosto && !candidatos.isEmpty()) {
                candidatos.add(vecino);
            }
        }
        return candidatos.isEmpty() ? null : candidatos.get(aleatorio.nextInt(candidatos.size()));
    }
}

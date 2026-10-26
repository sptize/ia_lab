package com.busquedainformada.ia_lab.algoritmos;

/**
 * Greedy Best-First: f(n) = h(n). Rápido, pero ignora el costo ya pagado,
 * por lo que no garantiza el camino óptimo. Cada estado se visita una sola vez.
 */
public final class BusquedaPrimeroMejorVoraz extends BusquedaPrimeroMejor {

    /**
     * {@inheritDoc}
     */
    @Override
    public String nombre() {
        return "Greedy Best-First";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    double prioridad(double costoCamino, double heuristica) {
        return heuristica;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean encolar(Double costoConocido, double costoCandidato) {
        return costoConocido == null;
    }
}

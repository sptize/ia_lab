package com.busquedainformada.ia_lab.algoritmos;
/**
 * A*: f(n) = g(n) + h(n). Con una heurística admisible devuelve el camino de menor costo.
 * Reabre estados si encuentra un camino más barato hacia ellos.
 */
public final class BusquedaAEstrella extends BusquedaPrimeroMejor {

    /**
     * {@inheritDoc}
     */
    @Override
    public String nombre() {
        return "A*";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double prioridad(double costoCamino, double heuristica) {
        return costoCamino + heuristica;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean encolar(Double costoConocido, double costoCandidato) {
        return costoConocido == null || costoCandidato < costoConocido;
    }
}

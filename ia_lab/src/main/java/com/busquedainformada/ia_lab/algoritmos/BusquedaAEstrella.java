package com.busquedainformada.ia_lab.algoritmos;
/**
 * A*: f(n) = g(n) + h(n). Con costos finitos no negativos, una heurística
 * admisible y un espacio de estados alcanzables finito, devuelve el camino de
 * menor costo. Reabre estados si encuentra un camino más barato hacia ellos,
 * por lo que no requiere que la heurística sea consistente.
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
    double prioridad(double costoCamino, double heuristica) {
        return costoCamino + heuristica;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean encolar(Double costoConocido, double costoCandidato) {
        return costoConocido == null || costoCandidato < costoConocido;
    }
}

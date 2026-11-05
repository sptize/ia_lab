package com.ia_lab.busquedainformadadecaminos.core;
/**
 * A*: f(n) = g(n) + h(n). Con costos finitos no negativos, una heurística
 * admisible y un espacio de estados alcanzables finito, devuelve el camino de
 * menor costo si la identidad de los estados se representa correctamente y se
 * mantienen sus mejores costos. Este algoritmo reabre estados al encontrar un
 * camino más barato, por lo que no requiere que la heurística sea consistente.
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

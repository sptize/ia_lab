package com.iaLab.busquedainformadadecaminos.grafos;

import java.util.List;

import com.iaLab.busquedainformadadecaminos.core.IProblemaBusqueda;
import com.iaLab.busquedainformadadecaminos.core.Paso;


/**
 * Adapta un {@link Grafo} al contrato {@link IProblemaBusqueda} para ejecutar
 * algoritmos de búsqueda de caminos.
 */
public final class GrafoProblema implements IProblemaBusqueda<String> {

    private final Grafo grafo;

    /**
     * Crea el problema de búsqueda correspondiente al grafo indicado.
     *
     * @param grafo grafo que define el estado inicial, la meta, la heurística y las transiciones
     */
    public GrafoProblema(Grafo grafo) {
        this.grafo = grafo;
    }

    /** {@inheritDoc} */
    @Override
    public String estadoInicial() {
        return grafo.getInicio();
    }

    /** {@inheritDoc} */
    @Override
    public boolean esMeta(String estado) {
        return estado.equals(grafo.getMeta());
    }

    /** {@inheritDoc} */
    @Override
    public double heuristica(String estado) {
        return grafo.getHeuristica(estado);
    }

    /** {@inheritDoc} */
    @Override
    public List<Paso<String>> sucesores(String estado) {
        return grafo.aristasDesde(estado).stream()
                .map(arista -> new Paso<>("%s->%s".formatted(estado, arista.destino()), arista.destino(), arista.costo()))
                .toList();
    }
}

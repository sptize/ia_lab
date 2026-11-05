package com.ia_lab.busquedainformadalocal.ochoreinas;

import java.util.List;
import java.util.Random;

import com.ia_lab.busquedainformadalocal.core.IProblemaBusquedaLocal;

/** *  Adapta {@link TableroOchoReinas} al contrato {@link IProblemaBusquedaLocal}. */
public final class ProblemaOchoReinas implements IProblemaBusquedaLocal<TableroOchoReinas> {

    /**
     * {@inheritDoc}
     */
    @Override
    public TableroOchoReinas estadoAleatorio(Random aleatorio) {
        return TableroOchoReinas.aleatorio(aleatorio);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TableroOchoReinas> vecinos(TableroOchoReinas estado) {
        return estado.vecinos();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double costo(TableroOchoReinas estado) {
        return estado.paresEnAtaque();
    }
}

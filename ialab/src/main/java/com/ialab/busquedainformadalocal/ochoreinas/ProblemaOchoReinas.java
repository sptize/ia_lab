package com.ialab.busquedainformadalocal.ochoreinas;

import com.ialab.busquedainformadalocal.core.IProblemaBusquedaLocal;

import java.util.List;
import java.util.Random;

/**
 * Adapta {@link TableroOchoReinas} al contrato {@link IProblemaBusquedaLocal}.
 */
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

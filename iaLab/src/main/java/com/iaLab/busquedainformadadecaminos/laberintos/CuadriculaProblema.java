package com.iaLab.busquedainformadadecaminos.laberintos;

import com.iaLab.busquedainformadadecaminos.core.IProblemaBusqueda;
import com.iaLab.busquedainformadadecaminos.core.Paso;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapta un {@link Cuadricula} al contrato {@link IProblemaBusqueda}.
 */
public final class CuadriculaProblema implements IProblemaBusqueda<Posicion> {
  
  /**
   * Costo mínimo de una celda; mantiene la heurística admisible.
   */
  private static final int MIN_COSTO_CELDA = 1;
  
  private final Cuadricula mapa;
  
  /**
   * Crea el problema de búsqueda asociado a una cuadrícula.
   *
   * @param mapa cuadrícula que define el espacio de búsqueda
   */
  public CuadriculaProblema(Cuadricula mapa) {
    this.mapa = mapa;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Posicion estadoInicial() {
    return mapa.inicio();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean esMeta(Posicion estado) {
    return estado.equals(mapa.meta());
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public List<Paso<Posicion>> sucesores(Posicion estado) {
    List<Paso<Posicion>> pasos = new ArrayList<>();
    for (Direccion direccion : Direccion.values()) {
      Posicion next = estado.mover(direccion);
      if (mapa.esTransitable(next)) {
        pasos.add(new Paso<>(direccion.getEtiqueta(), next, mapa.costoDe(next)));
      }
    }
    return pasos;
  }
  
  /**
   * Calcula la distancia Manhattan a la meta, que no sobreestima el costo real.
   *
   * @param estado estado cuya heurística se calcula
   * @return estimación del costo restante hasta la meta
   */
  @Override
  public double heuristica(Posicion estado) {
    return (double) estado.manhattanDistanciaA(mapa.meta()) * MIN_COSTO_CELDA;
  }
}

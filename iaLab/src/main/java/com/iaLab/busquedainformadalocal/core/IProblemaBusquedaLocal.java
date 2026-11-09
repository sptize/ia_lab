package com.iaLab.busquedainformadalocal.core;


import java.util.List;
import java.util.Random;

/**
 * Problema de búsqueda local: no importa el camino, solo el estado final.
 *
 * @param <S> tipo del estado
 */
public interface IProblemaBusquedaLocal<S> {
  
  /**
   * Genera un estado inicial aleatorio.
   *
   * @param aleatorio generador de números aleatorios
   * @return estado inicial generado
   */
  S estadoAleatorio(Random aleatorio);
  
  /**
   * Devuelve los vecinos del estado indicado.
   *
   * @param estado estado cuyos vecinos se solicitan
   * @return estados vecinos
   */
  List<S> vecinos(S estado);
  
  /**
   * Calcula la función de costo que se debe minimizar.
   *
   * @param estado estado cuyo costo se calcula
   * @return costo finito del estado que se debe minimizar; cero representa una solución
   */
  double costo(S estado);
  
  /**
   * Indica si el estado satisface el problema.
   *
   * @param estado estado que se evalúa
   * @return {@code true} si el costo del estado es cero
   */
  default boolean esSolucion(S estado) {
    return costo(estado) == 0;
  }
}

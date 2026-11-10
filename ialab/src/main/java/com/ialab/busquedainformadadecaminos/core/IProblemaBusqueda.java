package com.ialab.busquedainformadadecaminos.core;

import java.util.List;

/**
 * Contrato de un problema de búsqueda en grafo con heurística.
 * Los algoritmos dependen solo de esta interfaz, no de un dominio concreto.
 * Los estados deben ser inmutables mientras se usan en la búsqueda y mientras
 * el resultado los conserve. En particular, los datos que determinan
 * {@code equals} y {@code hashCode} no deben cambiar. Esta condición también
 * aplica a los objetos mutables anidados dentro de un estado.
 * Los problemas deben proporcionar sucesores con costos finitos y no negativos,
 * además de una heurística finita y no negativa para cada estado evaluado. Para que una
 * búsqueda termine, el espacio de estados alcanzables debe ser finito y cada
 * estado debe tener un número finito de sucesores.
 *
 * <p>En particular, A* devuelve una solución de costo mínimo si la heurística
 * es admisible: para todo estado, no sobreestima el costo real mínimo restante
 * hasta una meta. Esta propiedad depende del dominio y no puede comprobarse
 * genéricamente en tiempo de ejecución.</p>
 *
 * @param <S> tipo del estado (inmutable y con {@code equals}/{@code hashCode}
 *            estables)
 */
public interface IProblemaBusqueda<S> {
  
  /**
   * Devuelve el estado inicial del problema.
   *
   * @return estado inicial
   */
  S estadoInicial();
  
  /**
   * Indica si el estado indicado satisface la meta.
   *
   * @param estado estado que se evalúa
   * @return {@code true} si el estado es meta
   */
  boolean esMeta(S estado);
  
  /**
   * Devuelve los estados alcanzables desde el estado indicado, junto con la acción y el costo de cada paso.
   *
   * @param estado estado cuyos sucesores se solicitan
   * @return transiciones disponibles desde el estado
   */
  List<Paso<S>> sucesores(S estado);
  
  /**
   * Estima el costo restante desde el estado hasta la meta.
   *
   * @param estado estado cuya distancia estimada se calcula
   * @return estimación heurística finita y no negativa del costo restante; para garantizar
   * optimalidad con A*, no debe sobreestimar el costo mínimo real
   */
  double heuristica(S estado);
}

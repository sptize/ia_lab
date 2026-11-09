package com.iaLab.busquedaadversarial.core;

import java.util.List;

/**
 * Contrato que los algoritmos de búsqueda adversarial necesitan de cualquier juego.
 * No contiene lógica de ningún juego concreto.
 *
 * <p>Convención: mayor valor = mejor para MAX. MAX intenta maximizar; MIN intenta minimizar.</p>
 */
public interface IEstadoJuego {
  
  /**
   * Indica si el estado es final. En los árboles de prueba equivale a una hoja (sin hijos).
   *
   * @return {@code true} si el estado es terminal
   */
  boolean esTerminal();
  
  /**
   * Devuelve la utilidad del estado. Solo es válida si {@link #esTerminal()} es {@code true}.
   *
   * @return utilidad del estado terminal
   * @throws IllegalStateException si el estado no es terminal
   */
  int utilidad();
  
  /**
   * Indica a quién le toca jugar en este estado.
   *
   * @return {@code true} si juega MAX, {@code false} si juega MIN
   */
  boolean turnoDeMax();
  
  /**
   * Devuelve los sucesores (hijos) del estado, siempre en el mismo orden para el mismo estado.
   * En los árboles preconstruidos devuelve la lista ya existente; en los juegos se generan
   * a partir del estado.
   *
   * <p>Se usa el comodín {@code ? extends IEstadoJuego} para que cada implementación pueda
   * devolver su propio tipo ({@code List<NodoArbol>}, {@code List<EstadoTresEnRaya>}, ...).</p>
   *
   * @return sucesores del estado
   */
  List<? extends IEstadoJuego> sucesores();
}

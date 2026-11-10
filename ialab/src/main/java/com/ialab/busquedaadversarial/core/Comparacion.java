package com.ialab.busquedaadversarial.core;

import java.util.Locale;

/**
 * Compara los resultados de Minimax clásico y alfa-beta. Existe una sola vez para que el
 * ejemplo del árbol y el del juego muestren la tabla y calculen la reducción igual.
 */
public final class Comparacion {
  
  private Comparacion() {
  }
  
  /**
   * Calcula la reducción porcentual de nodos visitados:
   * {@code ((nodosMinimax - nodosAlfaBeta) / nodosMinimax) * 100}.
   *
   * @param minimax  resultado de Minimax clásico
   * @param alfaBeta resultado de Minimax con poda alfa-beta
   * @return porcentaje de reducción de nodos visitados
   */
  public static double reduccionNodos(ResultadoMinimax minimax, ResultadoMinimax alfaBeta) {
    long base = minimax.nodosVisitados();
    return (base - alfaBeta.nodosVisitados()) * 100.0 / base;
  }
  
  /**
   * Construye la tabla comparativa con los valores de las dos ejecuciones.
   *
   * @param minimax  resultado de Minimax clásico
   * @param alfaBeta resultado de Minimax con poda alfa-beta
   * @return tabla lista para imprimir
   */
  public static String tabla(ResultadoMinimax minimax, ResultadoMinimax alfaBeta) {
    StringBuilder sb = new StringBuilder();
    sb.append(fila("Métrica", "Minimax", "Alfa-beta"));
    sb.append("-----------------+------------+-----------\n");
    sb.append(fila("Valor obtenido", minimax.valor(), alfaBeta.valor()));
    sb.append(fila("Nodos visitados", minimax.nodosVisitados(), alfaBeta.nodosVisitados()));
    sb.append(fila("Hojas evaluadas", minimax.hojasEvaluadas(), alfaBeta.hojasEvaluadas()));
    sb.append(String.format(Locale.ROOT,
        "Reducción de nodos (alfa-beta respecto a Minimax): %.2f %%%n",
        reduccionNodos(minimax, alfaBeta)));
    return sb.toString();
  }
  
  private static String fila(String metrica, Object minimax, Object alfaBeta) {
    return String.format(Locale.ROOT, "%-16s | %10s | %10s%n", metrica, minimax, alfaBeta);
  }
}

package com.ialab.busquedaadversarial.core;

/**
 * Resultado de una búsqueda adversarial: valor minimax y estadísticas de ejecución.
 *
 * <p>Definiciones (idénticas para Minimax clásico y para alfa-beta):</p>
 * <ul>
 *   <li><b>Nodo visitado:</b> cada invocación de la función recursiva, incluida la raíz.
 *       Se cuenta al entrar, antes de saber si el estado es terminal.</li>
 *   <li><b>Hoja evaluada:</b> invocación sobre un estado terminal, en la que se llama a
 *       {@link IEstadoJuego#utilidad()}.</li>
 * </ul>
 *
 * <p>Por tanto, siempre se cumple: hojasEvaluadas &lt;= nodosVisitados.</p>
 */
public final class ResultadoMinimax {
  
  private int valor;
  private long nodosVisitados;
  private long hojasEvaluadas;
  
  // Solo Minimax (mismo paquete) modifica los contadores; el resto únicamente los lee.
  void asignarValor(int valor) {
    this.valor = valor;
  }
  
  void contarNodo() {
    nodosVisitados++;
  }
  
  void contarHoja() {
    hojasEvaluadas++;
  }
  
  /**
   * Devuelve el valor minimax del estado inicial.
   *
   * @return valor minimax
   */
  public int valor() {
    return valor;
  }
  
  /**
   * Devuelve la cantidad de nodos visitados (invocaciones recursivas, raíz incluida).
   *
   * @return nodos visitados
   */
  public long nodosVisitados() {
    return nodosVisitados;
  }
  
  /**
   * Devuelve la cantidad de hojas evaluadas (estados terminales cuya utilidad se consultó).
   *
   * @return hojas evaluadas
   */
  public long hojasEvaluadas() {
    return hojasEvaluadas;
  }
  
  @Override
  public String toString() {
    return "valor=" + valor + ", nodosVisitados=" + nodosVisitados
        + ", hojasEvaluadas=" + hojasEvaluadas;
  }
}

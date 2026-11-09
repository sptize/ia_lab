package com.iaLab.busquedainformadadecaminos.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nodo del árbol de búsqueda: recuerda su padre para reconstruir el camino.
 */
record Nodo<S>(S estado, Nodo<S> padre, String accion, double costoCamino, double heuristica) {
  
  static <S> Nodo<S> raiz(S estado, double heuristica) {
    return new Nodo<>(estado, null, null, 0, heuristica);
  }
  
  Nodo<S> hijo(Paso<S> paso, double heuristica) {
    return new Nodo<>(paso.estado(), this, paso.accion(), costoCamino + paso.costo(), heuristica);
  }
  
  /**
   * Nodos desde la raíz hasta este nodo, en orden.
   */
  List<Nodo<S>> caminoDesdeLaRaiz() {
    List<Nodo<S>> camino = new ArrayList<>();
    for (Nodo<S> nodo = this; nodo != null; nodo = nodo.padre) {
      camino.add(nodo);
    }
    Collections.reverse(camino);
    return camino;
  }
}

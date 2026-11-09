package com.iaLab.busquedainformadadecaminos.core;

import java.util.*;

/**
 * Patrón Template Method: fija el esqueleto de la búsqueda "primero el mejor"
 * y deja a las subclases dos decisiones:
 * <ul>
 *   <li>{@link #prioridad}: cómo se ordena la frontera (f = g + h en A*, f = h en Greedy).</li>
 *   <li>{@link #encolar}: cuándo vale la pena (re)encolar un estado ya conocido.</li>
 * </ul>
 *
 * <p>El problema debe cumplir las precondiciones de {@link IProblemaBusqueda}:
 * costos finitos no negativos y heurísticas finitas no negativas. La terminación requiere
 * un espacio de estados alcanzables finito y ramificación finita. Los estados
 * se usan como claves de un {@link HashMap} y se conservan en los nodos para
 * reconstruir el camino; por eso sus datos de igualdad y hash deben permanecer
 * estables mientras se ejecuta la búsqueda.</p>
 */
abstract class BusquedaPrimeroMejor implements IAlgoritmoBusqueda {
  
  private static double validarHeuristica(double heuristica) {
    if (!Double.isFinite(heuristica) || heuristica < 0) {
      throw new IllegalArgumentException("la heurística debe ser finita y no negativa");
    }
    return heuristica;
  }
  
  /**
   * Ordena por prioridad, desempata por menor heurística y luego por orden de llegada.
   *
   * @param <S> tipo del estado
   * @return comparador determinista para las entradas de la frontera
   */
  private static <S> Comparator<QueueEntry<S>> ordenDeEntrada() {
    return Comparator.<QueueEntry<S>>comparingDouble(QueueEntry::prioridad)
        .thenComparingDouble(entry -> entry.nodo().heuristica())
        .thenComparingLong(QueueEntry::orden);
  }
  
  /**
   * Calcula la prioridad de expansión del estado.
   *
   * @param costoCamino costo acumulado desde el estado inicial
   * @param heuristica  estimación del costo restante hasta la meta
   * @return prioridad de la entrada; un valor menor se expande antes
   */
  abstract double prioridad(double costoCamino, double heuristica);
  
  /**
   * @param costoConocido  mejor costo conocido hacia el estado, o {@code null} si es nuevo
   * @param costoCandidato costo del nuevo camino hacia ese estado
   * @return {@code true} si el candidato debe añadirse a la frontera
   */
  abstract boolean encolar(Double costoConocido, double costoCandidato);
  
  /**
   * Ejecuta la búsqueda primero el mejor en el problema indicado.
   *
   * @param <S>      tipo de estado del problema
   * @param problema problema que se va a resolver
   * @return resultado de la búsqueda, o vacío si no se alcanza una meta
   * @throws NullPointerException     si {@code problema} es {@code null}
   * @throws IllegalArgumentException si el problema produce costos o
   *                                  heurísticas negativas/no finitas, o una suma/prioridad no finita
   */
  @Override
  public final <S> Optional<ResultadoBusqueda<S>> solucionar(IProblemaBusqueda<S> problema) {
    Objects.requireNonNull(problema, "problema");
    
    PriorityQueue<QueueEntry<S>> frontera = new PriorityQueue<>(ordenDeEntrada());
    Map<S, Double> mejoresCostos = new HashMap<>();
    long secuencia = 0;
    int expandidos = 0;
    
    S inicial = problema.estadoInicial();
    double heuristicaInicial = validarHeuristica(problema.heuristica(inicial));
    Nodo<S> raiz = Nodo.raiz(inicial, heuristicaInicial);
    frontera.add(new QueueEntry<>(raiz, calcularPrioridad(0, raiz.heuristica()), secuencia++));
    mejoresCostos.put(inicial, 0.0);
    
    while (!frontera.isEmpty()) {
      Nodo<S> actual = frontera.poll().nodo();
      
      if (actual.costoCamino() > mejoresCostos.get(actual.estado())) {
        continue; // entrada obsoleta: se encontró un camino mejor después de encolarla
      }
      if (problema.esMeta(actual.estado())) {
        return Optional.of(ResultadoBusqueda.deObjetivo(actual, expandidos));
      }
      
      expandidos++;
      for (Paso<S> paso : problema.sucesores(actual.estado())) {
        double costoCandidato = actual.costoCamino() + paso.costo();
        if (!Double.isFinite(costoCandidato)) {
          throw new IllegalArgumentException("el costo acumulado del camino debe ser finito");
        }
        if (encolar(mejoresCostos.get(paso.estado()), costoCandidato)) {
          double heuristica = validarHeuristica(problema.heuristica(paso.estado()));
          Nodo<S> hijo = actual.hijo(paso, heuristica);
          frontera.add(new QueueEntry<>(hijo, calcularPrioridad(hijo.costoCamino(), hijo.heuristica()), secuencia++));
          mejoresCostos.put(paso.estado(), costoCandidato);
        }
      }
    }
    return Optional.empty();
  }
  
  private double calcularPrioridad(double costoCamino, double heuristica) {
    double valor = prioridad(costoCamino, heuristica);
    if (!Double.isFinite(valor)) {
      throw new IllegalArgumentException("la prioridad de búsqueda debe ser finita");
    }
    return valor;
  }
  
  private record QueueEntry<S>(Nodo<S> nodo, double prioridad, long orden) {
  }
}

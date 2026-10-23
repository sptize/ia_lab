package com.busquedainformada.ia_lab.algoritmos;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;

/**
 * Patrón Template Method: fija el esqueleto de la búsqueda "primero el mejor"
 * y deja a las subclases dos decisiones:
 * <ul>
 *   <li>{@link #prioridad}: cómo se ordena la frontera (f = g + h en A*, f = h en Greedy).</li>
 *   <li>{@link #encolar}: cuándo vale la pena (re)encolar un estado ya conocido.</li>
 * </ul>
 */
public abstract class BusquedaPrimeroMejor implements IAlgoritmoBusqueda {

    /**
     * Calcula la prioridad de expansión del estado.
     *
     * @param costoCamino costo acumulado desde el estado inicial
     * @param heuristica estimación del costo restante hasta la meta
     * @return prioridad de la entrada; un valor menor se expande antes
     */
    protected abstract double prioridad(double costoCamino, double heuristica);

    /**
     * @param costoConocido     mejor costo conocido hacia el estado, o {@code null} si es nuevo
     * @param costoCandidato    costo del nuevo camino hacia ese estado
     * @return {@code true} si el candidato debe añadirse a la frontera
     */
    protected abstract boolean encolar(Double costoConocido, double costoCandidato);

    /**
     * Ejecuta la búsqueda primero el mejor en el problema indicado.
     *
     * @param <S> tipo de estado del problema
     * @param problema problema que se va a resolver
     * @return resultado de la búsqueda, o vacío si no se alcanza una meta
     * @throws NullPointerException si {@code problema} es {@code null}
     */
    @Override
    public final <S> Optional<ResultadoBusqueda<S>> solucionar(IProblemaBusqueda<S> problema) {
        Objects.requireNonNull(problema, "problema");

        PriorityQueue<QueueEntry<S>> frontera = new PriorityQueue<>(ordenDeEntrada());
        Map<S, Double> mejoresCostos = new HashMap<>();
        long secuencia = 0;
        int expandido = 0;

        S inicial = problema.estadoInicial();
        Nodo<S> raiz = Nodo.raiz(inicial, problema.heuristica(inicial));
        frontera.add(new QueueEntry<>(raiz, prioridad(0, raiz.heuristica()), secuencia++));
        mejoresCostos.put(inicial, 0.0);

        while (!frontera.isEmpty()) {
            Nodo<S> actual = frontera.poll().nodo();

            if (actual.costoCamino()> mejoresCostos.get(actual.estado())) {
                continue; // entrada obsoleta: se encontró un camino mejor después de encolarla
            }
            if (problema.esMeta(actual.estado())) {
                return Optional.of(ResultadoBusqueda.deObjetivo(actual, expandido));
            }

            expandido++;
            for (Paso<S> paso : problema.sucesores(actual.estado())) {
                double costoCandidato = actual.costoCamino() + paso.costo();
                if (encolar(mejoresCostos.get(paso.estado()), costoCandidato)) {
                    mejoresCostos.put(paso.estado(), costoCandidato);
                    Nodo<S> hijo = actual.hijo(paso, problema.heuristica(paso.estado()));
                    frontera.add(new QueueEntry<>(hijo, prioridad(hijo.costoCamino(), hijo.heuristica()), secuencia++));
                }
            }
        }
        return Optional.empty();
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

    private record QueueEntry<S>(Nodo<S> nodo, double prioridad, long orden) {
    }
}

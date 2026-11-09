package com.iaLab.consola;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.iaLab.busquedainformadadecaminos.grafos.Grafo;
import com.iaLab.busquedainformadalocal.core.HillClimbing;
import com.iaLab.busquedainformadalocal.core.ResultadoBusquedaLocal;
import com.iaLab.busquedainformadalocal.grafos.ProblemaGrafoLocal;

/** Ejecuta Hill Climbing sobre un grafo desde el nodo de inicio declarado. */
public final class ComandoHillClimbingGrafos implements Comando {

    private static final List<String> GRAFO_EJEMPLO = List.of(
            "# ejemplo de grafo",
            "inicio: S",
            "meta: G",
            "nodos:",
            "S 6",
            "A 2",
            "B 4",          
            "G 0",
            "aristas:",
            "S A",  
            "S B",  
            "A S",
            "B G");

    @Override
    public String titulo() {
        return "Hill Climbing - grafos (sin reinicios; búsqueda de la meta guiada por heurística)";
    }

    @Override
    public void execute(Consola consola) {
        Optional<EntradaGrafo> entrada = escogerGrafo(consola);
        if (entrada.isEmpty()) {
            return;
        }

        Grafo instancia = entrada.get().grafo();
        ProblemaGrafoLocal problema = new ProblemaGrafoLocal(instancia);
        HillClimbing<String> hillClimbing = new HillClimbing<>(new Random());
        ResultadoBusquedaLocal<String> resultado =
                hillClimbing.solucionarDesde(problema, instancia.getInicio());

        consola.println();
        consola.println("Grafo utilizado:");
        entrada.get().lineas().forEach(consola::println);
        consola.println();
        if (resultado.solucion()) {
            consola.println("Meta alcanzada: " + resultado.estado() + ".");
        } else {
            consola.println("Atascado en un óptimo local: el nodo " + resultado.estado()
                    + " tiene heurística " + resultado.costo() + ".");
        }
        consola.println("Pasos: " + resultado.pasos() + " | Reinicios: " + resultado.reinicios());
        consola.println("Recorrido: " + String.join(" -> ", resultado.recorrido()));
    }

    private Optional<EntradaGrafo> escogerGrafo(Consola consola) {
        consola.println();
        consola.println("Ingrese el grafo (una línea por cada línea; vacío para usar el ejemplo):");
        consola.println("Hill Climbing usa la heurística y no necesita costos en las aristas.");
        List<String> lineas = consola.pedirLineasConVolver(
                "Adicione detalles del grafo -inicio, meta, nodos, aristas- (línea vacía para terminar; escriba 'volver' para cancelar):");
        if (lineas.isEmpty()) {
            lineas = GRAFO_EJEMPLO;
        }
        try {
            return Optional.of(new EntradaGrafo(Grafo.parsear(lineas, true), List.copyOf(lineas)));
        } catch (IllegalArgumentException e) {
            consola.println("Error al parsear el grafo: " + e.getMessage());
            return Optional.empty();
        }
    }

    private record EntradaGrafo(Grafo grafo, List<String> lineas) {
    }
}

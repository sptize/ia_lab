package com.iaLab.consola;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.iaLab.busquedaadversarial.arbolgenerico.Arboles;
import com.iaLab.busquedaadversarial.arbolgenerico.ImpresoraArbol;
import com.iaLab.busquedaadversarial.arbolgenerico.NodoArbol;
import com.iaLab.busquedaadversarial.arbolgenerico.NodoTrazado;
import com.iaLab.busquedaadversarial.core.Comparacion;
import com.iaLab.busquedaadversarial.core.Minimax;
import com.iaLab.busquedaadversarial.core.ResultadoMinimax;

/**
 * Ejecuta Minimax clásico y alfa-beta sobre un árbol de prueba, muestra qué subárboles
 * podó alfa-beta y compara cuántos nodos visitó cada algoritmo.
 */
public final class ComandoMinimaxArbol implements Comando {

    /** {@inheritDoc} */
    @Override
    public String titulo() {
        return "Minimax vs alfa-beta - árbol de ejemplo";
    }

    /**
     * Pide un árbol de prueba, lo resuelve con ambos algoritmos y muestra el resultado.
     *
     * @param consola consola utilizada para elegir el árbol y mostrar los resultados
     */
    @Override
    public void execute(Consola consola) {
        List<Arboles.Caso> casos = Arboles.casos();
        consola.println();
        consola.println("Árboles disponibles:");
        for (int i = 0; i < casos.size(); i++) {
            consola.println((i + 1) + ". " + casos.get(i).nombre());
        }
        consola.println("0. Volver al menú principal");
        int opcion = consola.pedirOpcionDeMapaConVolver(
                "Elige un árbol (1 a " + casos.size() + "): ", 0, casos.size());
        if (opcion == 0) {
            return;
        }
        Arboles.Caso caso = casos.get(opcion - 1);
        NodoArbol raiz = caso.raiz();

        // Mismo árbol y mismo orden de sucesores para los dos algoritmos.
        List<NodoArbol> ordenMinimax = new ArrayList<>();
        List<NodoArbol> ordenAlfaBeta = new ArrayList<>();
        ResultadoMinimax minimax = Minimax.resolverConEstadisticas(new NodoTrazado(raiz, ordenMinimax));
        ResultadoMinimax alfaBeta = Minimax.resolverConPodaYEstadisticas(new NodoTrazado(raiz, ordenAlfaBeta));

        consola.println();
        consola.println("--- " + caso.nombre() + " (valor esperado: " + caso.valorEsperado() + ") ---");
        consola.println("Árbol (alfa-beta marca los subárboles que no exploró):");
        consola.println(ImpresoraArbol.dibujar(raiz, new HashSet<>(ordenAlfaBeta)));
        consola.println("Orden de visita Minimax  : " + nombres(ordenMinimax));
        consola.println("Orden de visita Alfa-beta: " + nombres(ordenAlfaBeta));
        consola.println();
        consola.println(Comparacion.tabla(minimax, alfaBeta).stripTrailing());
    }

    private static String nombres(List<NodoArbol> nodos) {
        return nodos.stream().map(NodoArbol::getNombre).collect(Collectors.joining(" "));
    }
}

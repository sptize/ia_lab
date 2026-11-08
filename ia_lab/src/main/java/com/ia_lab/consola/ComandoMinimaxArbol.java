package com.ia_lab.consola;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.ia_lab.busquedaadversarial.arbolgenerico.Arboles;
import com.ia_lab.busquedaadversarial.arbolgenerico.ImpresoraArbol;
import com.ia_lab.busquedaadversarial.arbolgenerico.NodoArbol;
import com.ia_lab.busquedaadversarial.arbolgenerico.NodoTrazado;
import com.ia_lab.busquedaadversarial.core.Comparacion;
import com.ia_lab.busquedaadversarial.core.Minimax;
import com.ia_lab.busquedaadversarial.core.ResultadoMinimax;

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
        int opcion = consola.pedirOpcionDeMapa("Elige un árbol (1 a " + casos.size() + "): ", 1, casos.size());
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

package com.ia_lab.busquedaadversarial.arbolgenerico;

import java.util.List;
import java.util.Set;

/**
 * Dibuja los árboles de prueba pequeños (no los árboles de los juegos).
 * Opcionalmente marca los subárboles que un algoritmo no llegó a explorar.
 * Devuelve el dibujo como texto para que quien lo use decida dónde mostrarlo.
 */
public final class ImpresoraArbol {

    private ImpresoraArbol() {}

    /**
     * Dibuja el árbol completo.
     *
     * @param raiz raíz del árbol
     * @return dibujo del árbol, sin salto de línea final
     */
    public static String dibujar(NodoArbol raiz) {
        return dibujar(raiz, null);
    }

    /**
     * Dibuja el árbol. Si {@code visitados} no es {@code null}, cada subárbol que el algoritmo
     * no exploró se marca como podado y no se desciende en él.
     *
     * @param raiz raíz del árbol
     * @param visitados nodos que el algoritmo visitó, o {@code null} para dibujarlo todo
     * @return dibujo del árbol, sin salto de línea final
     */
    public static String dibujar(NodoArbol raiz, Set<NodoArbol> visitados) {
        StringBuilder sb = new StringBuilder(raiz.toString());
        List<NodoArbol> hijos = raiz.sucesores();
        for (int i = 0; i < hijos.size(); i++) {
            dibujar(sb, hijos.get(i), "", i == hijos.size() - 1, visitados);
        }
        return sb.toString();
    }

    private static void dibujar(StringBuilder sb, NodoArbol nodo, String prefijo,
                                boolean esUltimo, Set<NodoArbol> visitados) {
        boolean podado = visitados != null && !visitados.contains(nodo);
        String marca = podado ? "   [podado: " + nodo.contarNodos() + " nodo(s) sin visitar]" : "";
        sb.append('\n').append(prefijo).append(esUltimo ? "└── " : "├── ").append(nodo).append(marca);
        if (podado) return;

        String prefijoHijos = prefijo + (esUltimo ? "    " : "│   ");
        List<NodoArbol> hijos = nodo.sucesores();
        for (int i = 0; i < hijos.size(); i++) {
            dibujar(sb, hijos.get(i), prefijoHijos, i == hijos.size() - 1, visitados);
        }
    }
}

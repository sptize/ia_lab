package com.ia_lab.busquedaadversarial.arbolgenerico;

import java.util.ArrayList;
import java.util.List;

import com.ia_lab.busquedaadversarial.core.IEstadoJuego;

/**
 * Envoltorio de {@link NodoArbol} que anota en qué orden visita cada nodo un algoritmo. Sirve para
 * mostrar qué nodos exploró Minimax y cuáles alfa-beta, y para comprobar por otra vía
 * que los contadores de {@code ResultadoMinimax} son correctos. No modifica el árbol ni el algoritmo.
 *
 * <p>Se anota en {@link #esTerminal()} porque Minimax y alfa-beta lo llaman exactamente una vez, y
 * lo primero, al entrar en cada nodo. Si eso dejara de cumplirse, las pruebas del árbol
 * (que comparan este registro con los contadores) lo detectarían.</p>
 */
public final class NodoTrazado implements IEstadoJuego {

    private final NodoArbol real;
    private final List<NodoArbol> visitados;     // compartido por todo el recorrido

    /**
     * Envuelve un nodo para registrar su visita.
     *
     * @param real nodo que se envuelve
     * @param visitados lista compartida en la que se anota cada nodo al visitarlo
     */
    public NodoTrazado(NodoArbol real, List<NodoArbol> visitados) {
        this.real = real;
        this.visitados = visitados;
    }

    @Override
    public boolean esTerminal() {
        visitados.add(real);
        return real.esTerminal();
    }

    @Override
    public int utilidad() {
        return real.utilidad();
    }

    @Override
    public boolean turnoDeMax() {
        return real.turnoDeMax();
    }

    @Override
    public List<NodoTrazado> sucesores() {
        List<NodoTrazado> hijos = new ArrayList<>();
        for (NodoArbol hijo : real.sucesores()) {
            hijos.add(new NodoTrazado(hijo, visitados));
        }
        return hijos;
    }

    @Override
    public String toString() {
        return real.toString();
    }
}

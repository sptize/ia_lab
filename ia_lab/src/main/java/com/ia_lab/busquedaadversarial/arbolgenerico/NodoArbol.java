package com.ia_lab.busquedaadversarial.arbolgenerico;

import java.util.ArrayList;
import java.util.List;

import com.ia_lab.busquedaadversarial.core.IEstadoJuego;

/**
 * Nodo genérico del árbol de prueba. Implementa {@link IEstadoJuego} para que Minimax pueda
 * recorrerlo sin saber si es un árbol preconstruido o un juego real.
 *
 * <p>Un nodo interno guarda qué jugador mueve en él (MAX o MIN); una hoja guarda su valor.</p>
 */
public class NodoArbol implements IEstadoJuego {

    private final String nombre;            // etiqueta para mostrar el árbol (A, B1...)
    private final Integer valor;            // null si es nodo interno
    private final boolean esMax;            // true = MAX, false = MIN (sin significado en hojas)
    private final List<NodoArbol> hijos = new ArrayList<>();

    /**
     * Crea un nodo interno; los hijos se añaden con {@link #agregar(NodoArbol)}.
     *
     * @param nombre etiqueta del nodo
     * @param esMax {@code true} si en este nodo mueve MAX, {@code false} si mueve MIN
     */
    public NodoArbol(String nombre, boolean esMax) {
        this.nombre = nombre;
        this.valor = null;
        this.esMax = esMax;
    }

    /**
     * Crea una hoja con su valor de utilidad.
     *
     * @param nombre etiqueta del nodo
     * @param valorHoja utilidad de la hoja
     */
    public NodoArbol(String nombre, int valorHoja) {
        this.nombre = nombre;
        this.valor = valorHoja;
        this.esMax = false;
    }

    /**
     * Añade un hijo al nodo.
     *
     * @param hijo nodo hijo
     * @return este mismo nodo, para encadenar llamadas
     * @throws IllegalStateException si este nodo es una hoja
     */
    public NodoArbol agregar(NodoArbol hijo) {
        if (valor != null) {
            throw new IllegalStateException("La hoja " + nombre + " no puede tener hijos");
        }
        hijos.add(hijo);
        return this;
    }

    /**
     * Devuelve la etiqueta del nodo.
     *
     * @return nombre del nodo
     */
    public String getNombre() {
        return nombre;
    }

    // ---------- Implementación de IEstadoJuego ----------

    @Override
    public boolean esTerminal() {
        return hijos.isEmpty();
    }

    @Override
    public int utilidad() {
        if (!esTerminal()) {
            throw new IllegalStateException(
                    "utilidad() solo es válido en hojas. Nodo: " + nombre);
        }
        if (valor == null) {
            throw new IllegalStateException(
                    "El nodo " + nombre + " no tiene hijos ni valor (¿árbol mal construido?)");
        }
        return valor;
    }

    @Override
    public boolean turnoDeMax() {
        return esMax;
    }

    @Override
    public List<NodoArbol> sucesores() {
        // List<NodoArbol> es subtipo de List<? extends IEstadoJuego>
        return hijos;
    }

    // ---------- Utilidades para verificar el árbol ----------

    /**
     * Cuenta los nodos del subárbol.
     *
     * @return total de nodos del subárbol, este incluido
     */
    public int contarNodos() {
        int total = 1;
        for (NodoArbol hijo : hijos) total += hijo.contarNodos();
        return total;
    }

    /**
     * Cuenta las hojas del subárbol.
     *
     * @return total de hojas del subárbol
     */
    public int contarHojas() {
        if (hijos.isEmpty()) return 1;
        int total = 0;
        for (NodoArbol hijo : hijos) total += hijo.contarHojas();
        return total;
    }

    /**
     * Comprueba que cada nodo interno tenga hijos internos del jugador contrario (las hojas no cuentan).
     *
     * @return {@code true} si los jugadores se alternan en todo el subárbol
     */
    public boolean alternaJugadores() {
        for (NodoArbol hijo : hijos) {
            if (!hijo.esTerminal() && hijo.esMax == esMax) return false;
            if (!hijo.alternaJugadores()) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return esTerminal() ? nombre + " = " + valor : nombre + (esMax ? " (MAX)" : " (MIN)");
    }
}

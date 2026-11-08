package com.iaLab.busquedaadversarial.arbolgenerico;

import java.util.List;

/**
 * Árboles de prueba pequeños, verificables a mano. Cada uno aporta algo distinto:
 *   - basico():     explica el funcionamiento elemental de Minimax.
 *   - multinivel(): varios niveles, para ver la recursividad y la alternancia MAX/MIN.
 *   - negativo():   todos los valores son negativos, para detectar acumuladores mal inicializados.
 *   - empateRaizMax() y empateRaizMin(): la poda ocurre justo con β == α, una vez en un nodo
 *                   MIN y otra en un nodo MAX, para comprobar la condición β <= α en ambas ramas.
 */
public final class Arboles {

    private Arboles() {}

    /**
     * Árbol de prueba con lo que se espera de él (calculado a mano, no por el programa):
     *  - valorEsperado:          valor minimax.
     *  - nodosAlfaBetaEsperados: nodos que visita alfa-beta explorando los hijos en orden.
     */
    public record Caso(String nombre, NodoArbol raiz, int valorEsperado, int nodosAlfaBetaEsperados) { }

    /**
     * Devuelve los árboles de prueba, cada uno con sus resultados esperados. En cada llamada
     * se construyen árboles nuevos.
     *
     * @return casos de prueba en orden fijo
     */
    public static List<Caso> casos() {
        return List.of(
                new Caso("Árbol básico (2 niveles)", basico(), 3, 6),
                new Caso("Árbol multinivel (MAX-MIN-MAX)", multinivel(), 7, 12),
                new Caso("Árbol con valores negativos", negativo(), -2, 7),
                new Caso("Empate β == α, raíz MAX (poda en un nodo MIN)", empateRaizMax(), 3, 6),
                new Caso("Empate α == β, raíz MIN (poda en un nodo MAX)", empateRaizMin(), 5, 6));
    }

    /**
     * Árbol del enunciado: raíz MAX, un nivel MIN y las hojas.
     *
     *            A (MAX)
     *          /         \
     *       B (MIN)     C (MIN)
     *       /   \       /   \
     *      3     5     2     8
     *
     * B = min(3,5) = 3;  C = min(2,8) = 2;  A = max(3,2) = 3.
     * Minimax esperado: 3.
     * Alfa-beta: tras B, α = 3. En C, la hoja 2 deja β = 2 <= α, así que C2 (8) se poda.
     */
    public static NodoArbol basico() {
        NodoArbol raiz = new NodoArbol("A", true);

        NodoArbol b = new NodoArbol("B", false);
        b.agregar(new NodoArbol("B1", 3))
                .agregar(new NodoArbol("B2", 5));

        NodoArbol c = new NodoArbol("C", false);
        c.agregar(new NodoArbol("C1", 2))
                .agregar(new NodoArbol("C2", 8));

        raiz.agregar(b).agregar(c);
        return raiz;
    }

    /**
     * Árbol de 4 niveles (MAX → MIN → MAX → hojas).
     *
     *                    Root (MAX)
     *                  /            \
     *              A (MIN)         B (MIN)
     *             /      \        /      \
     *         A1(MAX)  A2(MAX) B1(MAX) B2(MAX)
     *          / \      / \      / \      / \
     *         4   7    1   9    2   2    6   0
     *
     * A1 = 7, A2 = 9  →  A = min(7,9) = 7.
     * B1 = 2, B2 = 6  →  B = min(2,6) = 2.
     * Root = max(7,2) = 7.
     * Minimax esperado: 7.
     * Alfa-beta: tras A, α = 7. En B, B1 vale 2, luego β = 2 <= α y se poda B2 (con sus hojas).
     */
    public static NodoArbol multinivel() {
        NodoArbol raiz = new NodoArbol("Root", true);

        // --- Rama A (MIN) ---
        NodoArbol a = new NodoArbol("A", false);

        NodoArbol a1 = new NodoArbol("A1", true);
        a1.agregar(new NodoArbol("A1a", 4))
                .agregar(new NodoArbol("A1b", 7));

        NodoArbol a2 = new NodoArbol("A2", true);
        a2.agregar(new NodoArbol("A2a", 1))
                .agregar(new NodoArbol("A2b", 9));

        a.agregar(a1).agregar(a2);

        // --- Rama B (MIN) ---
        NodoArbol b = new NodoArbol("B", false);

        NodoArbol b1 = new NodoArbol("B1", true);
        b1.agregar(new NodoArbol("B1a", 2))
                .agregar(new NodoArbol("B1b", 2));

        NodoArbol b2 = new NodoArbol("B2", true);
        b2.agregar(new NodoArbol("B2a", 6))
                .agregar(new NodoArbol("B2b", 0));

        b.agregar(b1).agregar(b2);

        raiz.agregar(a).agregar(b);
        return raiz;
    }

    /**
     * Árbol con valores negativos: detecta acumuladores mal inicializados
     * (por ejemplo, un MAX que empiece en 0 en lugar de -∞ devolvería 0).
     *
     *              Root (MAX)
     *             /          \
     *          X (MIN)      Y (MIN)
     *           /  \         /  \
     *         -1   -3      -2   -2
     *
     * X = min(-1,-3) = -3;  Y = min(-2,-2) = -2;  Root = max(-3,-2) = -2.
     * Minimax esperado: -2.
     * Alfa-beta: tras X, α = -3. En Y, β = -2 nunca es <= α, así que NO poda nada:
     * visita los mismos 7 nodos que Minimax (la poda depende de los valores y del orden).
     */
    public static NodoArbol negativo() {
        NodoArbol raiz = new NodoArbol("Root", true);

        NodoArbol x = new NodoArbol("X", false);
        x.agregar(new NodoArbol("X1", -1))
                .agregar(new NodoArbol("X2", -3));

        NodoArbol y = new NodoArbol("Y", false);
        y.agregar(new NodoArbol("Y1", -2))
                .agregar(new NodoArbol("Y2", -2));

        raiz.agregar(x).agregar(y);
        return raiz;
    }

    /**
     * Árbol donde la poda ocurre justo cuando β == α, para comprobar la condición β <= α
     * (con β < α no se podaría C2).
     *
     *            A (MAX)
     *          /         \
     *       B (MIN)     C (MIN)
     *       /   \       /   \
     *      3     5     3     9
     *
     * B = min(3,5) = 3;  C = min(3,9) = 3;  A = max(3,3) = 3.
     * Minimax esperado: 3.
     * Alfa-beta: tras B, α = 3. En C, la hoja 3 deja β = 3 <= α: C2 (9) se poda aunque
     * no pueda mejorar ni empeorar el resultado (empate).
     */
    public static NodoArbol empateRaizMax() {
        NodoArbol raiz = new NodoArbol("A", true);

        NodoArbol b = new NodoArbol("B", false);
        b.agregar(new NodoArbol("B1", 3))
                .agregar(new NodoArbol("B2", 5));

        NodoArbol c = new NodoArbol("C", false);
        c.agregar(new NodoArbol("C1", 3))
                .agregar(new NodoArbol("C2", 9));

        raiz.agregar(b).agregar(c);
        return raiz;
    }

    /**
     * Igual que empateRaizMax() pero con la raíz MIN: la poda con α == β ocurre en un nodo MAX.
     *
     *            R (MIN)
     *          /         \
     *       B (MAX)     C (MAX)
     *       /   \       /   \
     *      5     2     5     9
     *
     * B = max(5,2) = 5;  C = max(5,9) = 9;  R = min(5,9) = 5.
     * Minimax esperado: 5.
     * Alfa-beta: tras B, β = 5. En C, la hoja 5 deja α = 5 >= β: C2 (9) se poda.
     * (Alfa-beta devuelve 5 para C en lugar de 9, y aun así el valor de R es correcto.)
     */
    public static NodoArbol empateRaizMin() {
        NodoArbol raiz = new NodoArbol("R", false);

        NodoArbol b = new NodoArbol("B", true);
        b.agregar(new NodoArbol("B1", 5))
                .agregar(new NodoArbol("B2", 2));

        NodoArbol c = new NodoArbol("C", true);
        c.agregar(new NodoArbol("C1", 5))
                .agregar(new NodoArbol("C2", 9));

        raiz.agregar(b).agregar(c);
        return raiz;
    }
}

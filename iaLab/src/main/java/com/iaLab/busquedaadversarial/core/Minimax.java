package com.iaLab.busquedaadversarial.core;

/**
 * Algoritmo Minimax y su variante con poda alfa-beta.
 * Completamente independiente del juego: solo usa la interfaz {@link IEstadoJuego}.
 *
 * <p>Cada algoritmo está escrito una sola vez. Las estadísticas se recogen con dos líneas
 * al inicio de cada llamada recursiva (contar el nodo y, si es terminal, contar la hoja),
 * sin alterar ninguna decisión del algoritmo. Los métodos que devuelven solo el valor
 * ({@link #resolver} y {@link #resolverConPoda}) reutilizan los mismos.</p>
 */
public final class Minimax {

    private Minimax() {}   // clase de utilidades, no instanciable.

    // ------------------------------------------------------------------
    //  API pública
    // ------------------------------------------------------------------

    /**
     * Minimax clásico: solo el valor.
     *
     * @param estado estado inicial
     * @return valor minimax del estado
     */
    public static int resolver(IEstadoJuego estado) {
        return resolverConEstadisticas(estado).valor();
    }

    /**
     * Minimax con poda alfa-beta: solo el valor.
     *
     * @param estado estado inicial
     * @return valor minimax del estado
     */
    public static int resolverConPoda(IEstadoJuego estado) {
        return resolverConPodaYEstadisticas(estado).valor();
    }

    /**
     * Minimax clásico (sin poda) con estadísticas.
     *
     * @param estado estado inicial
     * @return valor minimax y estadísticas de la búsqueda
     */
    public static ResultadoMinimax resolverConEstadisticas(IEstadoJuego estado) {
        ResultadoMinimax resultado = new ResultadoMinimax();
        resultado.asignarValor(minimax(estado, resultado));
        return resultado;
    }

    /**
     * Minimax con poda alfa-beta, con estadísticas.
     *
     * @param estado estado inicial
     * @return valor minimax y estadísticas de la búsqueda
     */
    public static ResultadoMinimax resolverConPodaYEstadisticas(IEstadoJuego estado) {
        ResultadoMinimax resultado = new ResultadoMinimax();
        resultado.asignarValor(alfaBeta(estado, Integer.MIN_VALUE, Integer.MAX_VALUE, resultado));
        return resultado;
    }

    // ------------------------------------------------------------------
    //  Minimax clásico
    // ------------------------------------------------------------------

    /**
     * Recorre todo el árbol. Devuelve el valor que MAX puede garantizarse si ambos
     * jugadores juegan de forma óptima. Coste: O(b^d), con b = factor de ramificación
     * y d = profundidad.
     */
    private static int minimax(IEstadoJuego estado, ResultadoMinimax estadisticas) {
        estadisticas.contarNodo();                  // cada invocación = un nodo visitado (raíz incluida)

        // Caso base: terminal --> devolvemos la utilidad tal cual.
        if (estado.esTerminal()) {
            estadisticas.contarHoja();              // hoja evaluada
            return estado.utilidad();
        }

        // Caso recursivo: depende de quién mueve en este nodo.
        if (estado.turnoDeMax()) {
            int mejor = Integer.MIN_VALUE;          // -∞, no 0
            for (IEstadoJuego hijo : estado.sucesores()) {
                mejor = Math.max(mejor, minimax(hijo, estadisticas));
            }
            return mejor;
        } else {
            int mejor = Integer.MAX_VALUE;          // +∞, no 0
            for (IEstadoJuego hijo : estado.sucesores()) {
                mejor = Math.min(mejor, minimax(hijo, estadisticas));
            }
            return mejor;
        }
    }

    // ------------------------------------------------------------------
    //  Minimax con poda alfa-beta
    // ------------------------------------------------------------------

    /**
     * Mismo resultado que {@link #minimax}, pero puede evitar explorar ramas.
     *
     * <p>α = mejor valor ya garantizado para MAX (cota inferior).
     * β = mejor valor ya garantizado para MIN (cota superior).
     * Si β &lt;= α, la rama actual no puede mejorar a ningún ancestro y se corta.</p>
     *
     * <p>Cuántos nodos se ahorran depende del orden en que se exploran los sucesores y de
     * las características del árbol: en algunos casos no se poda nada.</p>
     */
    private static int alfaBeta(IEstadoJuego estado, int alfa, int beta, ResultadoMinimax estadisticas) {
        estadisticas.contarNodo();                  // mismo criterio que en minimax()

        if (estado.esTerminal()) {
            estadisticas.contarHoja();              // mismo criterio que en minimax()
            return estado.utilidad();
        }

        if (estado.turnoDeMax()) {
            int mejor = Integer.MIN_VALUE;
            for (IEstadoJuego hijo : estado.sucesores()) {
                mejor = Math.max(mejor, alfaBeta(hijo, alfa, beta, estadisticas));
                alfa = Math.max(alfa, mejor);       // MAX sube la cota inferior.
                if (beta <= alfa) break;            // poda β
            }
            return mejor;
        } else {
            int mejor = Integer.MAX_VALUE;
            for (IEstadoJuego hijo : estado.sucesores()) {
                mejor = Math.min(mejor, alfaBeta(hijo, alfa, beta, estadisticas));
                beta = Math.min(beta, mejor);       // MIN baja la cota superior.
                if (beta <= alfa) break;            // poda α
            }
            return mejor;
        }
    }
}

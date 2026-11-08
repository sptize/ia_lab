package com.iaLab.consola;

import com.iaLab.busquedaadversarial.core.Comparacion;
import com.iaLab.busquedaadversarial.core.Minimax;
import com.iaLab.busquedaadversarial.core.ResultadoMinimax;
import com.iaLab.busquedaadversarial.tresenraya.EstadoTresEnRaya;

/**
 * Ejecuta Minimax clásico y alfa-beta sobre un tablero de tres en raya y compara cuántos
 * nodos visitó cada algoritmo.
 */
public final class ComandoMinimaxTresEnRaya implements Comando {

    /** {@inheritDoc} */
    @Override
    public String titulo() {
        return "Minimax vs alfa-beta - tres en raya";
    }

    /**
     * Pide un tablero (o usa el vacío), lo resuelve con ambos algoritmos y muestra el resultado.
     *
     * @param consola consola utilizada para leer el tablero y mostrar los resultados
     */
    @Override
    public void execute(Consola consola) {
        consola.println();
        consola.println("Tablero de 9 celdas: X (MAX, empieza), O (MIN) y '.' o '-' para vacío; '/' separa filas.");
        consola.println("Ejemplo: XO./.../...");
        String texto = consola.preguntar("Tablero (vacío para usar el tablero vacío): ");

        EstadoTresEnRaya estado;
        if (texto.isEmpty()) {
            estado = new EstadoTresEnRaya();
        } else {
            try {
                estado = EstadoTresEnRaya.desdeTexto(texto);
            } catch (IllegalArgumentException e) {
                consola.println("Tablero inválido: " + e.getMessage());
                return;
            }
        }

        // Mismo estado inicial y mismo orden de sucesores para ambos algoritmos.
        ResultadoMinimax minimax = Minimax.resolverConEstadisticas(estado);
        ResultadoMinimax alfaBeta = Minimax.resolverConPodaYEstadisticas(estado);

        consola.println();
        consola.println("Estado " + estado);
        consola.println(Comparacion.tabla(minimax, alfaBeta).stripTrailing());
    }
}

package com.ia_lab.busquedaadversarial.tresenraya;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ia_lab.busquedaadversarial.core.IEstadoJuego;

/**
 * Tres en raya (3x3). X es MAX y empieza siempre; O es MIN.
 * Utilidad: +1 si gana X, -1 si gana O, 0 en empate.
 */
public class EstadoTresEnRaya implements IEstadoJuego {

    public static final char VACIA = '.';
    public static final char X = 'X';   // MAX
    public static final char O = 'O';   // MIN

    // Tablero: índice = fila*3 + columna
    private final char[] tablero;

    /** Crea el tablero vacío. */
    public EstadoTresEnRaya() {
        this.tablero = new char[9];
        Arrays.fill(this.tablero, VACIA); // Todas las posiciones con '.'
    }

    private EstadoTresEnRaya(char[] tablero) {
        this.tablero = tablero; // Ya viene clonado por quien llama.
    }

    /**
     * Construye un estado desde "XO./.O./..X" (acepta '.' o '-' como vacío; ignora '/' y
     * espacios). Rechaza con {@link IllegalArgumentException} todo tablero que no pueda aparecer
     * en una partida real, jugando X primero y alternando:
     * <ul>
     *   <li>longitud distinta de 9 o símbolos que no sean X, O, '.' o '-';</li>
     *   <li>cantidades incompatibles: debe ser #X == #O o #X == #O + 1;</li>
     *   <li>ambos jugadores con tres en raya;</li>
     *   <li>la partida continuó tras una victoria: si ganó X debe ser #X == #O + 1 (le toca
     *       a O); si ganó O debe ser #X == #O (le toca a X).</li>
     * </ul>
     *
     * @param texto tablero en texto
     * @return estado correspondiente al tablero
     * @throws IllegalArgumentException si el texto no describe un tablero posible
     */
    public static EstadoTresEnRaya desdeTexto(String texto) {
        if (texto == null)
            throw new IllegalArgumentException("El tablero no puede ser null");
        String limpio = texto.replace("/", "").replace(" ", "").replace('-', '.');
        if (limpio.length() != 9)
            throw new IllegalArgumentException("Se esperaban 9 celdas: " + texto);
        char[] celdas = limpio.toCharArray();
        for (char c : celdas) {
            if (c != VACIA && c != X && c != O)
                throw new IllegalArgumentException("Símbolo no válido: " + c);
        }
        EstadoTresEnRaya estado = new EstadoTresEnRaya(celdas);
        estado.validarAlcanzable();
        return estado;
    }

    // ---------- IEstadoJuego ----------

    @Override
    public boolean esTerminal() {
        return haGanado(X) || haGanado(O) || estaLleno();
    }

    @Override
    public int utilidad() {
        if (!esTerminal())
            throw new IllegalStateException("utilidad() solo en terminal: " + this);
        if (haGanado(X)) return +1;   // MAX ganó
        if (haGanado(O)) return -1;   // MIN ganó
        return 0;                      // Tablero lleno sin ganador.
    }

    @Override
    public boolean turnoDeMax() {
        // X empieza y se alterna: mueve X cuando #X == #O.
        return contar(X) == contar(O);
    }

    @Override
    public List<EstadoTresEnRaya> sucesores() {
        List<EstadoTresEnRaya> resultado = new ArrayList<>();
        if (esTerminal()) return resultado;         // Sin hijos en hoja.
        char simbolo = turnoDeMax() ? X : O;
        for (int i = 0; i < 9; i++) {
            if (tablero[i] == VACIA) {
                char[] siguiente = tablero.clone(); // Clonar antes de mutar.
                siguiente[i] = simbolo;
                resultado.add(new EstadoTresEnRaya(siguiente));
            }
        }
        return resultado;
    }

    // ---------- Ayudas ----------

    /**
     * Devuelve el tablero como texto, p. ej. "XO./.X./...". Se puede volver a leer con
     * {@link #desdeTexto(String)}.
     *
     * @return tablero en texto, con '/' entre filas
     */
    public String tableroComoTexto() {
        StringBuilder sb = new StringBuilder();
        for (int f = 0; f < 3; f++) {
            if (f > 0) sb.append('/');
            sb.append(tablero[f * 3]).append(tablero[f * 3 + 1]).append(tablero[f * 3 + 2]);
        }
        return sb.toString();
    }

    private void validarAlcanzable() {
        int xs = contar(X), os = contar(O);
        if (xs != os && xs != os + 1)
            throw new IllegalArgumentException("Cantidades incompatibles: #X=" + xs + ", #O=" + os
                    + " (X empieza, así que #X debe ser #O o #O+1): " + tableroComoTexto());

        boolean xGana = haGanado(X), oGana = haGanado(O);
        if (xGana && oGana)
            throw new IllegalArgumentException("Ambos jugadores tienen tres en raya: " + tableroComoTexto());
        if (xGana && xs != os + 1)
            throw new IllegalArgumentException("X ya había ganado, pero la partida continuó: " + tableroComoTexto());
        if (oGana && xs != os)
            throw new IllegalArgumentException("O ya había ganado, pero la partida continuó: " + tableroComoTexto());
    }

    private int contar(char simbolo) {
        int n = 0;
        for (char c : tablero) {
            if (c == simbolo) n++;
        }
        return n;
    }

    private boolean estaLleno() {
        return contar(VACIA) == 0;
    }

    private boolean haGanado(char simbolo) {
        // filas
        for (int f = 0; f < 3; f++)
            if (tablero[f * 3] == simbolo && tablero[f * 3 + 1] == simbolo && tablero[f * 3 + 2] == simbolo)
                return true;

        // columnas
        for (int c = 0; c < 3; c++)
            if (tablero[c] == simbolo && tablero[3 + c] == simbolo && tablero[6 + c] == simbolo)
                return true;

        // diagonales
        if (tablero[0] == simbolo && tablero[4] == simbolo && tablero[8] == simbolo) return true;
        if (tablero[2] == simbolo && tablero[4] == simbolo && tablero[6] == simbolo) return true;

        return false;
    }

    @Override
    public String toString() {
        return tableroComoTexto() + " [" + (turnoDeMax() ? "MAX" : "MIN") + "]";
    }
}

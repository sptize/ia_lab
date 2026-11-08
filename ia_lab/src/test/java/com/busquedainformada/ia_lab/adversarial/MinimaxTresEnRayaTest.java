package com.busquedainformada.ia_lab.adversarial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ia_lab.busquedaadversarial.core.IEstadoJuego;
import com.ia_lab.busquedaadversarial.core.Minimax;
import com.ia_lab.busquedaadversarial.core.ResultadoMinimax;
import com.ia_lab.busquedaadversarial.tresenraya.EstadoTresEnRaya;

/**
 * Minimax y alfa-beta aplicados al tres en raya mediante {@link IEstadoJuego}.
 *
 * <p>Primero se verifica el juego (reglas, estados terminales, turnos, validación de tableros) y
 * después se ejecutan ambos algoritmos sobre los mismos estados iniciales. Las comprobaciones usan
 * reglas reimplementadas aquí (LINEAS, oráculo*) para no validar {@link EstadoTresEnRaya} con su
 * propio código.</p>
 */
class MinimaxTresEnRayaTest {

    private static final int[][] LINEAS = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};

    // Cifras conocidas del tres en raya jugado desde el tablero vacío.
    private static final int POSICIONES_ALCANZABLES = 5478;
    private static final int TERMINALES_GANA_X = 626;
    private static final int TERMINALES_GANA_O = 316;
    private static final int TERMINALES_EMPATE = 16;

    /** Resultado del recorrido completo del espacio de estados. */
    private record Recorrido(Set<String> vistos, int ganaX, int ganaO, int empates,
                             int erroresTerminal, int erroresSucesores, int erroresTurno) { }

    private static Recorrido recorrido;

    @BeforeAll
    static void recorrerEspacioDeEstados() {
        recorrido = recorrer();
    }

    // ------------------------------------------------------------------
    //  1) desdeTexto(): tableros aceptados y rechazados
    // ------------------------------------------------------------------

    @Test
    void desdeTextoAceptaTablerosPosiblesConElTurnoCorrecto() {
        aceptar(".../.../...", true, "tablero vacío (mueve MAX)");
        aceptar("XX./.O./..O", true, "X=2, O=2 (mueve MAX)");
        aceptar("X-O -X- --O", true, "acepta '-' como vacío e ignora espacios");
        aceptar("XXX/OO./...", false, "gana X con #X = #O + 1");
        aceptar("OOO/XX./X..", true, "gana O con #X = #O");
        aceptar("XOX/OOX/XXO", false, "tablero lleno sin ganador");
        aceptar("XXX/XOO/XOO", false, "X gana con dos líneas a la vez (jugada en la intersección)");
    }

    @Test
    void desdeTextoRechazaTablerosImposibles() {
        // (el tercer argumento es el fragmento del mensaje que identifica la regla que rechaza)
        rechazar(null, "null", "null");
        rechazar("XO", "longitud distinta de 9", "9 celdas");
        rechazar("XOZ......", "símbolo no válido", "Símbolo no válido");
        rechazar("XXX/.../...", "cantidades incompatibles (X juega 3 veces, O ninguna)", "Cantidades incompatibles");
        rechazar("XX./XX./...", "cantidades incompatibles (#X = #O + 4)", "Cantidades incompatibles");
        rechazar("O../.../...", "cantidades incompatibles (O no puede empezar)", "Cantidades incompatibles");
        rechazar("XXX/OOO/...", "ambos jugadores con tres en raya", "Ambos jugadores");
        rechazar("XXX/OOO/X..", "ambos jugadores con tres en raya (con #X = #O + 1)", "Ambos jugadores");
        rechazar("XXX/OO./..O", "la partida continuó después de ganar X", "X ya había ganado");
        rechazar("XOX/.OX/XO.", "la partida continuó después de ganar O", "O ya había ganado");
    }

    // ------------------------------------------------------------------
    //  2) Victorias de X y de O en las 8 líneas
    // ------------------------------------------------------------------

    @Test
    void xGanaEnCadaUnaDeLasOchoLineas() {
        for (int[] linea : LINEAS) {
            // X completa la línea y O tiene 2 fichas fuera de ella (#X = #O + 1, le toca a O).
            char[] tx = vacio();
            for (int i : linea) tx[i] = 'X';
            int puestas = 0;
            for (int i = 0; i < 9 && puestas < 2; i++) {
                if (tx[i] == '.') { tx[i] = 'O'; puestas++; }
            }
            String texto = new String(tx);
            EstadoTresEnRaya ex = EstadoTresEnRaya.desdeTexto(texto);
            assertTrue(ex.esTerminal(), texto + ": debe ser terminal");
            assertEquals(+1, ex.utilidad(), texto + ": utilidad +1");
            assertFalse(ex.turnoDeMax(), texto + ": turno de MIN");
            assertTrue(ex.sucesores().isEmpty(), texto + ": sin sucesores");
        }
    }

    @Test
    void oGanaEnCadaUnaDeLasOchoLineas() {
        for (int[] linea : LINEAS) {
            // O completa la línea y X tiene 3 fichas fuera de ella sin formar línea (#X = #O, le toca a X).
            char[] to = vacio();
            for (int i : linea) to[i] = 'O';
            for (int i : tresCeldasFueraDeLinea(linea)) to[i] = 'X';
            String texto = new String(to);
            EstadoTresEnRaya eo = EstadoTresEnRaya.desdeTexto(texto);
            assertTrue(eo.esTerminal(), texto + ": debe ser terminal");
            assertEquals(-1, eo.utilidad(), texto + ": utilidad -1");
            assertTrue(eo.turnoDeMax(), texto + ": turno de MAX");
            assertTrue(eo.sucesores().isEmpty(), texto + ": sin sucesores");
        }
    }

    @Test
    void empateYTableroEnCursoSeComportanComoSeEspera() {
        EstadoTresEnRaya empate = EstadoTresEnRaya.desdeTexto("XOX/OOX/XXO");
        assertTrue(empate.esTerminal(), "tablero lleno sin ganador: terminal");
        assertEquals(0, empate.utilidad(), "tablero lleno sin ganador: utilidad 0");
        assertTrue(empate.sucesores().isEmpty(), "tablero lleno sin ganador: sin sucesores");

        EstadoTresEnRaya enCurso = EstadoTresEnRaya.desdeTexto("XX./.O./..O");
        assertFalse(enCurso.esTerminal(), "tablero en curso: no es terminal");
        assertEquals(5, enCurso.sucesores().size(), "tablero en curso: un sucesor por cada casilla libre");
    }

    // ------------------------------------------------------------------
    //  3) Recorrido completo del espacio de estados
    // ------------------------------------------------------------------

    @Test
    void cadaSucesorEsUnMovimientoLegalYNoHayRepetidos() {
        assertEquals(0, recorrido.erroresSucesores(),
                "cada sucesor debe ser una casilla libre con el símbolo del turno, sin repetidos");
    }

    @Test
    void estadosTerminalesYUtilidadesCoincidenConElOraculo() {
        assertEquals(0, recorrido.erroresTerminal(),
                "terminales y utilidades (+1, -1, 0) deben coincidir con el oráculo en todas las posiciones");
    }

    @Test
    void losTurnosAlternanYCoincidenConLaCuentaDeFichas() {
        assertEquals(0, recorrido.erroresTurno(),
                "los turnos alternan entre padre e hijo y coinciden con #X == #O");
    }

    @Test
    void lasCifrasConocidasDelJuegoSeCumplen() {
        assertEquals(POSICIONES_ALCANZABLES, recorrido.vistos().size(), "posiciones alcanzables");
        assertEquals(TERMINALES_GANA_X, recorrido.ganaX(), "terminales en los que gana MAX");
        assertEquals(TERMINALES_GANA_O, recorrido.ganaO(), "terminales en los que gana MIN");
        assertEquals(TERMINALES_EMPATE, recorrido.empates(), "terminales en empate");
    }

    /** desdeTexto() debe aceptar exactamente las posiciones alcanzables, de entre los 3^9 tableros. */
    @Test
    void desdeTextoAceptaExactamenteLasPosicionesAlcanzables() {
        char[] simbolos = {'.', 'X', 'O'};
        int aceptados = 0, discrepancias = 0;
        for (int n = 0; n < 19683; n++) {
            char[] c = new char[9];
            int resto = n;
            for (int i = 0; i < 9; i++) {
                c[i] = simbolos[resto % 3];
                resto /= 3;
            }
            String tablero = new String(c);
            boolean aceptado;
            try {
                EstadoTresEnRaya.desdeTexto(tablero);
                aceptado = true;
                aceptados++;
            } catch (IllegalArgumentException e) {
                aceptado = false;
            }
            if (aceptado != recorrido.vistos().contains(tablero)) discrepancias++;
        }
        assertEquals(0, discrepancias, "discrepancias con las posiciones alcanzables");
        assertEquals(recorrido.vistos().size(), aceptados, "tableros aceptados");
    }

    // ------------------------------------------------------------------
    //  4) Minimax y alfa-beta sobre el juego
    // ------------------------------------------------------------------

    @Test
    void ambosAlgoritmosDanElValorEsperadoEnEstadosConocidos() {
        // tablero, valor esperado
        Object[][] casos = {
                {".../.../...", 0},          // árbol de juego completo: con juego óptimo es empate
                {"XO./.../...", +1},         // X abrió en una esquina y O respondió en el borde adyacente
                {"X../.O./...", 0},
                {"XX./.O./..O", +1},
                {"XXX/OO./...", +1},
                {"OOO/XX./X..", -1},
                {"XOX/OOX/XXO", 0},
        };

        for (Object[] caso : casos) {
            String tablero = (String) caso[0];
            int esperado = (Integer) caso[1];

            EstadoTresEnRaya estado = EstadoTresEnRaya.desdeTexto(tablero);
            // Mismo estado inicial y mismo orden de sucesores para ambos algoritmos.
            ResultadoMinimax minimax = Minimax.resolverConEstadisticas(estado);
            ResultadoMinimax alfaBeta = Minimax.resolverConPodaYEstadisticas(estado);
            long[] arbol = contarArbol(estado);   // conteo independiente de Minimax

            String ctx = tablero + ": ";
            assertEquals(esperado, minimax.valor(), ctx + "valor de Minimax");
            assertEquals(esperado, alfaBeta.valor(), ctx + "valor de alfa-beta");
            assertEquals(arbol[0], minimax.nodosVisitados(), ctx + "Minimax visita todos los nodos del árbol");
            assertEquals(arbol[1], minimax.hojasEvaluadas(), ctx + "Minimax evalúa todas las hojas del árbol");
            assertTrue(alfaBeta.nodosVisitados() <= minimax.nodosVisitados()
                            && alfaBeta.hojasEvaluadas() <= minimax.hojasEvaluadas(),
                    ctx + "alfa-beta nunca visita más que Minimax");
        }
    }

    /** En todas las posiciones alcanzables: mismo valor y alfa-beta nunca visita más. */
    @Test
    void enTodasLasPosicionesAlcanzablesAmbosAlgoritmosCoinciden() {
        int discrepancias = 0, conPoda = 0;
        for (String tablero : recorrido.vistos()) {
            EstadoTresEnRaya estado = EstadoTresEnRaya.desdeTexto(tablero);
            ResultadoMinimax minimax = Minimax.resolverConEstadisticas(estado);
            ResultadoMinimax alfaBeta = Minimax.resolverConPodaYEstadisticas(estado);
            if (minimax.valor() != alfaBeta.valor()
                    || alfaBeta.nodosVisitados() > minimax.nodosVisitados()
                    || alfaBeta.hojasEvaluadas() > minimax.hojasEvaluadas()) {
                discrepancias++;
            }
            if (alfaBeta.nodosVisitados() < minimax.nodosVisitados()) conPoda++;
        }
        assertEquals(0, discrepancias,
                "mismo valor en ambos algoritmos y alfa-beta sin visitar más nodos ni hojas que Minimax");
        assertTrue(conPoda > 0, "alfa-beta debe realizar podas efectivas en el tres en raya");
    }

    @Test
    void utilidadEnEstadoNoTerminalLanzaExcepcion() {
        assertThrows(IllegalStateException.class, () -> new EstadoTresEnRaya().utilidad());
    }

    // ------------------------------------------------------------------
    //  Recorrido del espacio de estados y ayudas (oráculo independiente de EstadoTresEnRaya)
    // ------------------------------------------------------------------

    /**
     * Recorre todas las posiciones alcanzables desde el tablero vacío usando sucesores()
     * y comprueba en cada una: movimientos legales, terminales, utilidades y turnos.
     */
    private static Recorrido recorrer() {
        Set<String> vistos = new HashSet<>();
        Deque<EstadoTresEnRaya> pendientes = new ArrayDeque<>();
        EstadoTresEnRaya inicial = new EstadoTresEnRaya();
        vistos.add(plano(inicial));
        pendientes.add(inicial);

        int ganaX = 0, ganaO = 0, empates = 0;
        int erroresTerminal = 0, erroresSucesores = 0, erroresTurno = 0;

        while (!pendientes.isEmpty()) {
            EstadoTresEnRaya s = pendientes.poll();
            String b = plano(s);
            boolean terminalEsperado = oraculoTerminal(b);

            if (s.esTerminal() != terminalEsperado) erroresTerminal++;
            if (s.turnoDeMax() != (cuenta(b, 'X') == cuenta(b, 'O'))) erroresTurno++;

            List<EstadoTresEnRaya> hijos = s.sucesores();
            if (terminalEsperado) {
                int esperada = oraculoUtilidad(b);
                if (s.esTerminal() && s.utilidad() != esperada) erroresTerminal++;
                if (esperada > 0) ganaX++; else if (esperada < 0) ganaO++; else empates++;
                if (!hijos.isEmpty()) erroresSucesores++;
            } else {
                if (hijos.size() != cuenta(b, '.')) erroresSucesores++;
                char simbolo = s.turnoDeMax() ? 'X' : 'O';
                Set<String> hijosDistintos = new HashSet<>();
                for (EstadoTresEnRaya h : hijos) {
                    String hb = plano(h);
                    if (!esMovimientoLegal(b, hb, simbolo)) erroresSucesores++;
                    if (h.turnoDeMax() == s.turnoDeMax()) erroresTurno++;
                    hijosDistintos.add(hb);
                    if (vistos.add(hb)) pendientes.add(h);
                }
                if (hijosDistintos.size() != hijos.size()) erroresSucesores++;
            }
        }
        return new Recorrido(vistos, ganaX, ganaO, empates, erroresTerminal, erroresSucesores, erroresTurno);
    }

    private static void aceptar(String tablero, boolean turnoMaxEsperado, String motivo) {
        EstadoTresEnRaya estado = EstadoTresEnRaya.desdeTexto(tablero);
        assertEquals(turnoMaxEsperado, estado.turnoDeMax(),
                "acepta " + tablero + " con turno de " + (turnoMaxEsperado ? "MAX" : "MIN") + " → " + motivo);
    }

    private static void rechazar(String tablero, String motivo, String fragmentoMensaje) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> EstadoTresEnRaya.desdeTexto(tablero), "debía rechazar " + tablero + " → " + motivo);
        assertTrue(error.getMessage().contains(fragmentoMensaje),
                "rechaza " + tablero + " → " + motivo + " (mensaje: " + error.getMessage() + ")");
    }

    private static String plano(EstadoTresEnRaya s) {
        return s.tableroComoTexto().replace("/", "");
    }

    private static char[] vacio() {
        char[] b = new char[9];
        Arrays.fill(b, '.');
        return b;
    }

    private static int cuenta(String b, char simbolo) {
        int n = 0;
        for (int i = 0; i < b.length(); i++) {
            if (b.charAt(i) == simbolo) n++;
        }
        return n;
    }

    private static boolean gana(String b, char jugador) {
        for (int[] l : LINEAS) {
            if (b.charAt(l[0]) == jugador && b.charAt(l[1]) == jugador && b.charAt(l[2]) == jugador) return true;
        }
        return false;
    }

    private static boolean oraculoTerminal(String b) {
        return gana(b, 'X') || gana(b, 'O') || cuenta(b, '.') == 0;
    }

    private static int oraculoUtilidad(String b) {
        return gana(b, 'X') ? +1 : (gana(b, 'O') ? -1 : 0);
    }

    /** Hijo = padre con exactamente una casilla libre ocupada por el símbolo del turno. */
    private static boolean esMovimientoLegal(String padre, String hijo, char simbolo) {
        int cambios = 0;
        for (int i = 0; i < 9; i++) {
            if (padre.charAt(i) != hijo.charAt(i)) {
                if (padre.charAt(i) != '.' || hijo.charAt(i) != simbolo) return false;
                cambios++;
            }
        }
        return cambios == 1;
    }

    /** Tres casillas fuera de la línea que no forman a su vez una línea (para colocar fichas de X). */
    private static int[] tresCeldasFueraDeLinea(int[] linea) {
        List<Integer> libres = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            final int celda = i;
            if (Arrays.stream(linea).noneMatch(c -> c == celda)) libres.add(i);
        }
        for (int a = 0; a < libres.size(); a++)
            for (int b = a + 1; b < libres.size(); b++)
                for (int c = b + 1; c < libres.size(); c++) {
                    int[] terna = {libres.get(a), libres.get(b), libres.get(c)};
                    boolean formaLinea = false;
                    for (int[] l : LINEAS) {
                        if (Arrays.equals(terna, l)) formaLinea = true;
                    }
                    if (!formaLinea) return terna;
                }
        throw new IllegalStateException("No hay tres casillas sin línea");
    }

    /** Cuenta {nodos, hojas} del árbol de juego recorriéndolo directamente (sin usar Minimax). */
    private static long[] contarArbol(IEstadoJuego estado) {
        long[] acumulado = new long[2];
        contarArbol(estado, acumulado);
        return acumulado;
    }

    private static void contarArbol(IEstadoJuego estado, long[] acumulado) {
        acumulado[0]++;
        if (estado.esTerminal()) {
            acumulado[1]++;
            return;
        }
        for (IEstadoJuego hijo : estado.sucesores()) {
            contarArbol(hijo, acumulado);
        }
    }
}

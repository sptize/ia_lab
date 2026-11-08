package com.busquedainformada.ia_lab.busquedadecaminos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ia_lab.busquedainformadadecaminos.core.BusquedaAEstrella;
import com.ia_lab.busquedainformadadecaminos.core.BusquedaPrimeroMejorVoraz;
import com.ia_lab.busquedainformadadecaminos.grafos.Grafo;
import com.ia_lab.busquedainformadadecaminos.grafos.GrafoProblema;

class GrafoParserTest {

    @Test
    void permiteAristasAntesDeNodosBuclesAristasRepetidasYNodosSinAristas() {
        Grafo grafo = Grafo.parsear(List.of(
                "inicio: S",
                "meta: G",
                "aristas:",
                "S S 1",
                "S G 2",
                "S G 2",
                "nodos:",
                "S 0",
                "G 0",
                "I 0"));

        assertEquals(3, grafo.aristasDesde("S").size());
        assertEquals(0, grafo.aristasDesde("G").size());
        assertEquals(0, grafo.aristasDesde("I").size());
        assertTrue(grafo.nodos().contains("I"));
    }

    @Test
    void inicioIgualAMetaProduceCaminoDeCostoCero() {
        Grafo grafo = Grafo.parsear(List.of(
                "inicio: S",
                "meta: S",
                "nodos:",
                "S 0"));

        var resultado = new BusquedaAEstrella().solucionar(new GrafoProblema(grafo)).orElseThrow();

        assertEquals(0.0, resultado.costoTotal());
        assertEquals(List.of("S"), resultado.estados());
    }

    @Test
    void permiteNodosInalcanzables() {
        Grafo grafo = Grafo.parsear(List.of(
                "inicio: S",
                "meta: G",
                "nodos:",
                "S 0",
                "G 0",
                "I 0",
                "aristas:",
                "I S 1"));

        assertFalse(new BusquedaAEstrella().solucionar(new GrafoProblema(grafo)).isPresent());
    }

    @Test
    void rechazaListaNulaYSinNodos() {
        assertTrue(assertThrows(IllegalArgumentException.class, () -> Grafo.parsear(null))
                .getMessage().contains("nula"));
        assertInvalid(List.of("inicio: S", "meta: G"), "al menos un nodo");
    }

    @Test
    void rechazaDirectivasDeInicioOMetaRepetidasVaciasOMultipalabra() {
        assertInvalid(List.of("inicio: S", "inicio: G", "meta: G", "nodos:", "S 0", "G 0"),
                "inicio no se puede repetir");
        assertInvalid(List.of("inicio:", "meta: G", "nodos:", "G 0"), "no puede estar vacío");
        assertInvalid(List.of("inicio: S A", "meta: G", "nodos:", "S 0", "G 0"),
                "un solo nombre");
        assertInvalid(List.of("inicio: S", "meta: G", "meta: G", "nodos:", "S 0", "G 0"),
                "meta no se puede repetir");
        assertInvalid(List.of("inicio: S", "meta:", "nodos:", "S 0"), "no puede estar vacío");
        assertInvalid(List.of("inicio: S", "meta: G H", "nodos:", "S 0", "G 0"),
                "un solo nombre");
    }

    @Test
    void rechazaDirectivaDirigidoRepetida() {
        assertInvalid(List.of("inicio: S", "meta: G", "dirigido: true", "dirigido: false",
                "nodos:", "S 0", "G 0"), "dirigido no se puede repetir");
        assertInvalid(List.of("inicio: S", "meta: G", "dirigido: false", "dirigido: false",
                "nodos:", "S 0", "G 0"), "dirigido no se puede repetir");
    }

    @Test
    void dirigidoEsTrueSiLaDirectivaNoAparece() {
        Grafo grafo = Grafo.parsear(List.of(
                "inicio: S",
                "meta: G",
                "nodos:",
                "S 0",
                "G 0",
                "aristas:",
                "S G 1"));

        assertEquals(1, grafo.aristasDesde("S").size());
        assertEquals(0, grafo.aristasDesde("G").size());
    }

    @Test
    void rechazaTextoEnLaLineaDeUnaDirectivaDeSeccion() {
        assertInvalid(List.of("inicio: S", "meta: G", "nodos: S 0", "G 0"),
                "no acepta texto en la misma línea");
        assertInvalid(List.of("inicio: S", "meta: G", "nodos:", "S 0", "G 0", "aristas: S G 1"),
                "no acepta texto en la misma línea");
    }

    @Test
    void rechazaNodosRepetidosYNombresQueContienenDosPuntos() {
        assertInvalid(List.of("inicio: S", "meta: G", "nodos:", "S 0", "S 1", "G 0"),
                "está repetido");
        assertInvalid(List.of("inicio: S", "meta: G", "nodos:", "S:otro 0", "G 0"),
                "directiva desconocida");
    }

    @Test
    void rechazaHeuristicasYCostosNoNumericosONoFinitos() {
        for (String valor : List.of("abc", "NaN", "Infinity", "-Infinity")) {
            assertInvalid(List.of("inicio: S", "meta: G", "nodos:", "S " + valor, "G 0"),
                    "heurística");
            assertInvalid(List.of("inicio: S", "meta: G", "nodos:", "S 0", "G 0",
                    "aristas:", "S G " + valor), "costo de la arista");
        }
    }

    @Test
    void errorPorExtremoDeAristaNoDeclaradoIndicaLaLinea() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> Grafo.parsear(List.of(
                        "inicio: S",
                        "meta: G",
                        "aristas:",
                        "S X 1",
                        "nodos:",
                        "S 0",
                        "G 0")));

        assertTrue(error.getMessage().contains("línea 4"));
    }

    @Test
    void porDefectoRechazaUnaAristaSinCosto() {
        assertInvalid(List.of("inicio: S", "meta: G", "nodos:", "S 0", "G 0", "aristas:", "S G"),
                "se esperaba una arista con costo");
    }

    @Test
    void conCostoOpcionalLasAristasSinCostoTienenCostoCeroYSeMarcan() {
        Grafo grafo = Grafo.parsear(List.of(
                "inicio: S", "meta: G", "nodos:", "S 0", "A 0", "G 0",
                "aristas:", "S G", "S A 5"), true);

        assertTrue(grafo.tieneCostosOmitidos());
        assertEquals(0.0, grafo.aristasDesde("S").get(0).costo());
        assertEquals(5.0, grafo.aristasDesde("S").get(1).costo());
    }

    @Test
    void sinAristasOmitidasElGrafoNoSeMarca() {
        List<String> lineas = List.of(
                "inicio: S", "meta: G", "nodos:", "S 0", "G 0", "aristas:", "S G 3");

        assertFalse(Grafo.parsear(lineas).tieneCostosOmitidos());
        assertFalse(Grafo.parsear(lineas, true).tieneCostosOmitidos());
    }

    @Test
    void conCostoOpcionalSigueRechazandoAristasInvalidas() {
        List<String> base = List.of("inicio: S", "meta: G", "nodos:", "S 0", "G 0", "aristas:");
        for (String arista : List.of("S", "S G 1 2", "S G -1", "S G abc", "S G NaN")) {
            List<String> lineas = new java.util.ArrayList<>(base);
            lineas.add(arista);
            assertThrows(IllegalArgumentException.class, () -> Grafo.parsear(lineas, true), arista);
        }
        List<String> sinNodoDestino = new java.util.ArrayList<>(base);
        sinNodoDestino.add("S X");
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> Grafo.parsear(sinNodoDestino, true));
        assertTrue(error.getMessage().contains("línea 7"), error.getMessage());
    }

    @Test
    void greedyEncuentraElMismoCaminoConOSinCostosEnLasAristas() {
        Grafo conCostos = Grafo.parsear(List.of(
                "inicio: S", "meta: G", "nodos:", "S 6", "A 2", "B 4", "G 0",
                "aristas:", "S A 4", "A G 20", "S B 2", "B G 3"));
        Grafo sinCostos = Grafo.parsear(List.of(
                "inicio: S", "meta: G", "nodos:", "S 6", "A 2", "B 4", "G 0",
                "aristas:", "S A", "A G", "S B", "B G"), true);

        var conocidos = new BusquedaPrimeroMejorVoraz().solucionar(new GrafoProblema(conCostos)).orElseThrow();
        var omitidos = new BusquedaPrimeroMejorVoraz().solucionar(new GrafoProblema(sinCostos)).orElseThrow();

        assertEquals(conocidos.estados(), omitidos.estados());
        assertEquals(conocidos.nodosExpandidos(), omitidos.nodosExpandidos());
    }

    @Test
    void soloGreedyPrescindeDeLosCostos() {
        assertFalse(new BusquedaPrimeroMejorVoraz().requiereCostos());
        assertTrue(new BusquedaAEstrella().requiereCostos());
    }

    private static void assertInvalid(List<String> lineas, String parteDelMensaje) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> Grafo.parsear(lineas));
        assertTrue(error.getMessage().contains(parteDelMensaje), error.getMessage());
    }
}

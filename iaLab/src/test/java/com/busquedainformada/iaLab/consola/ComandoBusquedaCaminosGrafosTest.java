package com.busquedainformada.iaLab.consola;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.iaLab.busquedainformadadecaminos.core.BusquedaAEstrella;
import com.iaLab.busquedainformadadecaminos.core.BusquedaPrimeroMejorVoraz;
import com.iaLab.consola.ComandoBusquedaCaminosGrafos;
import com.iaLab.consola.Consola;

class ComandoBusquedaCaminosGrafosTest {

    private static final String GRAFO_SIN_COSTOS = String.join("\n",
            "inicio: S", "meta: G", "nodos:", "S 2", "G 0", "aristas:", "S G", "", "");

    @Test
    void soloGreedyAceptaAristasSinCostoYNoMuestraUnCostoFalso() {
        String salida = ejecutar(new ComandoBusquedaCaminosGrafos(
                "Greedy - grafos", List.of(new BusquedaPrimeroMejorVoraz())), GRAFO_SIN_COSTOS);

        assertFalse(salida.contains("Error al parsear"), salida);
        assertTrue(salida.contains("Camino: S -> G"), salida);
        assertTrue(salida.contains("Costo total: no disponible"), salida);
    }

    @Test
    void aEstrellaYLaComparacionSiguenExigiendoElCosto() {
        String salidaAEstrella = ejecutar(new ComandoBusquedaCaminosGrafos(
                "A* - grafos", List.of(new BusquedaAEstrella())), GRAFO_SIN_COSTOS);
        String salidaComparacion = ejecutar(new ComandoBusquedaCaminosGrafos(
                "Comparar", List.of(new BusquedaAEstrella(), new BusquedaPrimeroMejorVoraz())), GRAFO_SIN_COSTOS);

        assertTrue(salidaAEstrella.contains("Error al parsear el grafo"), salidaAEstrella);
        assertTrue(salidaComparacion.contains("Error al parsear el grafo"), salidaComparacion);
    }

    @Test
    void greedyConCostosCompletosSigueMostrandoElCostoTotal() {
        String grafo = String.join("\n",
                "inicio: S", "meta: G", "nodos:", "S 2", "G 0", "aristas:", "S G 7", "", "");

        String salida = ejecutar(new ComandoBusquedaCaminosGrafos(
                "Greedy - grafos", List.of(new BusquedaPrimeroMejorVoraz())), grafo);

        assertTrue(salida.contains("Costo total: 7"), salida);
    }

    private static String ejecutar(ComandoBusquedaCaminosGrafos comando, String entrada) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream salida = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        Consola consola = new Consola(
                new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)), salida);
        comando.execute(consola);
        return bytes.toString(StandardCharsets.UTF_8);
    }
}

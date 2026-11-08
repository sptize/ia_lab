package com.busquedainformada.iaLab.consola;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.iaLab.consola.ComandoHillClimbingGrafos;
import com.iaLab.consola.Consola;

class ComandoHillClimbingGrafosTest {

    @Test
    void ejecutaHillClimbingSobreGrafoIngresadoSinCostos() {
        String entrada = String.join("\n",
                "inicio: S", "meta: G", "nodos:", "S 3", "A 1", "G 0",
                "aristas:", "S A", "A G", "", "");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream salida = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        Consola consola = new Consola(
                new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)), salida);

        new ComandoHillClimbingGrafos().execute(consola);

        String texto = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(texto.contains("Recorrido: S -> A -> G"), texto);
        assertTrue(texto.contains("Meta alcanzada."), texto);
    }
}

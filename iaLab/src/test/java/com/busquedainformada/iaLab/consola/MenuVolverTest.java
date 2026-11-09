package com.busquedainformada.iaLab.consola;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.iaLab.consola.Comando;
import com.iaLab.consola.Consola;
import com.iaLab.consola.Menu;

class MenuVolverTest {

    @Test
    void volverDesdeUnComandoRegresaAlMenuPrincipal() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream salida = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        Consola consola = new Consola(
                new ByteArrayInputStream("1\nvolver\n0\n".getBytes(StandardCharsets.UTF_8)), salida);
        Comando comando = new Comando() {
            @Override
            public String titulo() {
                return "Comando de prueba";
            }

            @Override
            public void execute(Consola consola) {
                consola.preguntarConVolver("Entrada (escribe volver para regresar): ");
            }
        };

        new Menu(consola, List.of(comando)).run();

        String texto = bytes.toString(StandardCharsets.UTF_8);
        assertEquals(2, texto.split("=== Algoritmos de búsqueda ===", -1).length - 1, texto);
        assertTrue(texto.contains("Volviendo al menú principal."), texto);
        assertTrue(texto.contains("¡Hasta luego!"), texto);
    }

    @Test
    void volverEnUnaEntradaMultilineaCancelaElComando() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream salida = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        Consola consola = new Consola(
                new ByteArrayInputStream("S 2\nvolver\n".getBytes(StandardCharsets.UTF_8)), salida);

        org.junit.jupiter.api.Assertions.assertThrows(
                Consola.VolverAlMenuException.class,
                () -> consola.pedirLineasConVolver("Grafo (escribe volver para regresar): "));
    }
}

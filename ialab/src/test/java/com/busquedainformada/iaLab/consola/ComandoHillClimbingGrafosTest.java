package com.busquedainformada.iaLab.consola;

import com.ialab.consola.ComandoHillClimbingGrafos;
import com.ialab.consola.Consola;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ComandoHillClimbingGrafosTest {
  
  private static String ejecutar(String entrada) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream salida = new PrintStream(bytes, true, StandardCharsets.UTF_8);
    Consola consola = new Consola(
        new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)), salida);
    
    new ComandoHillClimbingGrafos().execute(consola);
    return bytes.toString(StandardCharsets.UTF_8);
  }
  
  @Test
  void ejecutaHillClimbingSobreGrafoIngresadoSinCostos() {
    String entrada = String.join("\n",
        "inicio: S", "meta: G", "nodos:", "S 3", "A 1", "G 0",
        "aristas:", "S A", "A G", "", "");
    String texto = ejecutar(entrada);
    
    assertTrue(texto.contains("Recorrido: S -> A -> G"), texto);
    assertTrue(texto.contains("Meta alcanzada: G."), texto);
    assertTrue(texto.contains("Pasos: 2 | Reinicios: 0"), texto);
  }
  
  @Test
  void informaElOptimoLocalConElMismoFormatoDeResumen() {
    String entrada = String.join("\n",
        "inicio: S", "meta: G", "nodos:", "S 4", "A 1", "G 0",
        "aristas:", "S A", "A S", "", "");
    String texto = ejecutar(entrada);
    
    assertTrue(texto.contains("Atascado en un óptimo local: el nodo A tiene heurística 1.0."), texto);
    assertTrue(texto.contains("Pasos: 1 | Reinicios: 0"), texto);
    assertTrue(texto.contains("Recorrido: S -> A"), texto);
  }
  
  @Test
  void muestraElGrafoDeEjemploCuandoLaEntradaEstaVacia() {
    String texto = ejecutar("\n");
    
    assertTrue(texto.contains("Grafo utilizado:"), texto);
    assertTrue(texto.contains("inicio: S"), texto);
    assertTrue(texto.contains("meta: G"), texto);
    assertTrue(texto.contains("S A 4"), texto);
    assertTrue(texto.contains("C G 3"), texto);
  }
}

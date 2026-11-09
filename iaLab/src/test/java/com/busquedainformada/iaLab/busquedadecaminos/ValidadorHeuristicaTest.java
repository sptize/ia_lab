package com.busquedainformada.iaLab.busquedadecaminos;

import com.iaLab.busquedainformadadecaminos.core.BusquedaAEstrella;
import com.iaLab.busquedainformadadecaminos.core.BusquedaPrimeroMejorVoraz;
import com.iaLab.busquedainformadadecaminos.grafos.Grafo;
import com.iaLab.busquedainformadadecaminos.grafos.GrafoProblema;
import com.iaLab.busquedainformadadecaminos.grafos.ValidadorHeuristica;
import com.iaLab.consola.ComandoBusquedaCaminosGrafos;
import com.iaLab.consola.Consola;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorHeuristicaTest {
  
  private static List<String> lineas(String dirigido, String heuristicaA, String heuristicaS,
                                     String heuristicaG) {
    return List.of(
        "dirigido: " + dirigido,
        "inicio: S",
        "meta: G",
        "nodos:",
        heuristicaS,
        heuristicaA,
        "B 4",
        "C 3",
        heuristicaG,
        "aristas:",
        "S A 4",
        "A G 20",
        "S B 2",
        "B C 2",
        "C G 3");
  }
  
  private static void assertNoLineNumberPrefix(String mensaje) {
    assertFalse(mensaje.startsWith("línea "), mensaje);
  }
  
  @Test
  void ejemploEsAdmisibleEnModoNoDirigido() {
    assertDoesNotThrow(() -> Grafo.parsear(lineas("no", "A 2", "S 6", "G 0")));
  }
  
  @Test
  void laAdmisibilidadDependeDeLaDireccionDeLasAristas() {
    IllegalArgumentException noDirigido = assertThrows(IllegalArgumentException.class,
        () -> ValidadorHeuristica.validar(Grafo.parsear(lineas("no", "A 15", "S 6", "G 0"))));
    
    assertTrue(noDirigido.getMessage().contains("h(A) = 15.0"));
    assertTrue(noDirigido.getMessage().contains("(11.0)"));
    assertDoesNotThrow(() -> ValidadorHeuristica.validar(Grafo.parsear(lineas("sí", "A 15", "S 6", "G 0"))));
  }
  
  @Test
  void rechazaHeuristicaPositivaEnLaMeta() {
    IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
        () -> ValidadorHeuristica.validar(Grafo.parsear(lineas("no", "A 2", "S 6", "G 5"))));
    
    assertTrue(error.getMessage().contains("h(G) = 5.0"));
    assertTrue(error.getMessage().contains("(0.0)"));
    assertNoLineNumberPrefix(error.getMessage());
  }
  
  @Test
  void informaTodosLosNodosConHeuristicaInadmisible() {
    IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
        () -> ValidadorHeuristica.validar(Grafo.parsear(lineas("no", "A 15", "S 50", "G 0"))));
    
    assertTrue(error.getMessage().contains("h(S) = 50.0"));
    assertTrue(error.getMessage().contains("(7.0)"));
    assertTrue(error.getMessage().contains("h(A) = 15.0"));
    assertTrue(error.getMessage().contains("(11.0)"));
    assertNoLineNumberPrefix(error.getMessage());
  }
  
  @Test
  void parseaYGestionaGrafosConHeuristicasInadmisiblesSinValidacionAutomatica() {
    Grafo grafo = Grafo.parsear(lineas("no", "A 15", "S 50", "G 0"));
    
    var resultado = new BusquedaAEstrella().solucionar(new GrafoProblema(grafo)).orElseThrow();
    
    assertEquals(7.0, resultado.costoTotal());
  }
  
  @Test
  void ignoraHeuristicaDeNodoQueNoPuedeLlegarALaMeta() {
    assertDoesNotThrow(() -> Grafo.parsear(List.of(
        "inicio: S",
        "meta: G",
        "nodos:",
        "S 6",
        "A 2",
        "B 4",
        "C 3",
        "G 0",
        "Z 100",
        "aristas:",
        "S A 4",
        "A G 20",
        "S B 2",
        "B C 2",
        "C G 3")));
  }
  
  @Test
  void ejemploDeConsolaConservaLosCostosDeAEstrellaYVoraz() {
    ByteArrayOutputStream salida = new ByteArrayOutputStream();
    Consola consola = new Consola(
        new ByteArrayInputStream("\n".getBytes(StandardCharsets.UTF_8)),
        new PrintStream(salida, true, StandardCharsets.UTF_8));
    ComandoBusquedaCaminosGrafos comando = new ComandoBusquedaCaminosGrafos(
        "Búsqueda en grafos",
        List.of(new BusquedaAEstrella(), new BusquedaPrimeroMejorVoraz()));
    
    comando.execute(consola);
    
    String texto = salida.toString(StandardCharsets.UTF_8);
    assertTrue(texto.contains("Costo total: 7"));
    assertTrue(texto.contains("Costo total: 24"));
  }
}

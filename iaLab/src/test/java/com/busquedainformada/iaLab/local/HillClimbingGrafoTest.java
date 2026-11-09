package com.busquedainformada.iaLab.local;

import com.iaLab.busquedainformadadecaminos.grafos.Grafo;
import com.iaLab.busquedainformadalocal.core.HillClimbing;
import com.iaLab.busquedainformadalocal.core.ResultadoBusquedaLocal;
import com.iaLab.busquedainformadalocal.grafos.ProblemaGrafoLocal;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class HillClimbingGrafoTest {
  
  @Test
  void desciendeDesdeElInicioDeclaradoHastaLaMeta() {
    Grafo grafo = Grafo.parsear(List.of(
        "inicio: S", "meta: G", "nodos:", "S 5", "A 2", "G 7",
        "aristas:", "S A", "A G"), true);
    
    ResultadoBusquedaLocal<String> resultado =
        new HillClimbing<String>(new Random(1)).solucionarDesde(new ProblemaGrafoLocal(grafo), "S");
    
    assertTrue(resultado.solucion());
    assertEquals(0, resultado.costo());
    assertEquals(2, resultado.pasos());
    assertEquals(List.of("S", "A", "G"), resultado.recorrido());
  }
  
  @Test
  void informaUnOptimoLocalCuandoNoHayVecinoConMejorHeuristica() {
    Grafo grafo = Grafo.parsear(List.of(
        "inicio: S", "meta: G", "nodos:", "S 4", "A 1", "G 0",
        "aristas:", "S A", "A S"), true);
    
    ResultadoBusquedaLocal<String> resultado =
        new HillClimbing<String>(new Random(1)).solucionarDesde(new ProblemaGrafoLocal(grafo), "S");
    
    assertFalse(resultado.solucion());
    assertEquals("A", resultado.estado());
    assertEquals(1, resultado.costo());
    assertEquals(List.of("S", "A"), resultado.recorrido());
  }
  
  @Test
  void usaLasAristasDirigidasYTrataLaMetaComoCostoCero() {
    Grafo grafo = Grafo.parsear(List.of(
        "inicio: S", "meta: G", "nodos:", "S 3", "G 8",
        "aristas:", "S G"), true);
    ProblemaGrafoLocal problema = new ProblemaGrafoLocal(grafo);
    
    assertEquals(List.of("G"), problema.vecinos("S"));
    assertEquals(List.of(), problema.vecinos("G"));
    assertEquals(0, problema.costo("G"));
    assertTrue(problema.esSolucion("G"));
    assertFalse(problema.esSolucion("S"));
  }
}

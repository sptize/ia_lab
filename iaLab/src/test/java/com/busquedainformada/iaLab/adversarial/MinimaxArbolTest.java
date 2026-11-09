package com.busquedainformada.iaLab.adversarial;

import com.iaLab.busquedaadversarial.arbolgenerico.Arboles;
import com.iaLab.busquedaadversarial.arbolgenerico.NodoArbol;
import com.iaLab.busquedaadversarial.arbolgenerico.NodoTrazado;
import com.iaLab.busquedaadversarial.core.Minimax;
import com.iaLab.busquedaadversarial.core.ResultadoMinimax;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Minimax y alfa-beta sobre árboles genéricos. Ambos algoritmos reciben el mismo árbol, el mismo
 * estado inicial y el mismo orden de sucesores. Los valores esperados están calculados a mano.
 */
class MinimaxArbolTest {
  
  private static void verificar(Arboles.Caso caso) {
    String ctx = caso.nombre() + ": ";
    NodoArbol raiz = caso.raiz();
    
    // Mismo árbol y mismo orden de sucesores para los dos algoritmos.
    List<NodoArbol> ordenMinimax = new ArrayList<>();
    List<NodoArbol> ordenAlfaBeta = new ArrayList<>();
    ResultadoMinimax minimax = Minimax.resolverConEstadisticas(new NodoTrazado(raiz, ordenMinimax));
    ResultadoMinimax alfaBeta = Minimax.resolverConPodaYEstadisticas(new NodoTrazado(raiz, ordenAlfaBeta));
    
    Set<NodoArbol> exploradosAlfaBeta = new HashSet<>(ordenAlfaBeta);
    long hojasMinimaxRegistradas = ordenMinimax.stream().filter(NodoArbol::esTerminal).count();
    long hojasAlfaBetaRegistradas = ordenAlfaBeta.stream().filter(NodoArbol::esTerminal).count();
    
    assertTrue(raiz.alternaJugadores(), ctx + "los niveles alternan MAX/MIN");
    assertEquals(caso.valorEsperado(), minimax.valor(), ctx + "Minimax devuelve el valor esperado");
    assertEquals(caso.valorEsperado(), alfaBeta.valor(), ctx + "alfa-beta devuelve el valor esperado");
    assertEquals(minimax.valor(), Minimax.resolver(raiz),
        ctx + "resolver() coincide con resolverConEstadisticas()");
    assertEquals(alfaBeta.valor(), Minimax.resolverConPoda(raiz),
        ctx + "resolverConPoda() coincide con resolverConPodaYEstadisticas()");
    assertEquals(raiz.contarNodos(), minimax.nodosVisitados(),
        ctx + "Minimax visita todos los nodos del árbol");
    assertEquals(raiz.contarHojas(), minimax.hojasEvaluadas(),
        ctx + "Minimax evalúa todas las hojas del árbol");
    assertEquals(ordenMinimax.size(), minimax.nodosVisitados(),
        ctx + "los nodos contados por Minimax coinciden con el registro de visitas");
    assertEquals(ordenAlfaBeta.size(), alfaBeta.nodosVisitados(),
        ctx + "los nodos contados por alfa-beta coinciden con el registro de visitas");
    assertEquals(hojasMinimaxRegistradas, minimax.hojasEvaluadas(),
        ctx + "las hojas contadas por Minimax coinciden con el registro de visitas");
    assertEquals(hojasAlfaBetaRegistradas, alfaBeta.hojasEvaluadas(),
        ctx + "las hojas contadas por alfa-beta coinciden con el registro de visitas");
    assertEquals(ordenAlfaBeta.size(), exploradosAlfaBeta.size(),
        ctx + "alfa-beta no visita ningún nodo dos veces");
    assertEquals(caso.nodosAlfaBetaEsperados(), alfaBeta.nodosVisitados(),
        ctx + "alfa-beta visita los nodos esperados según el cálculo a mano");
    assertTrue(alfaBeta.nodosVisitados() <= minimax.nodosVisitados()
            && alfaBeta.hojasEvaluadas() <= minimax.hojasEvaluadas(),
        ctx + "alfa-beta nunca visita más que Minimax");
    assertEquals(raiz.contarNodos() - exploradosAlfaBeta.size(),
        minimax.nodosVisitados() - alfaBeta.nodosVisitados(),
        ctx + "nodos ahorrados = nodos del árbol que alfa-beta no exploró");
  }
  
  @Test
  void cadaArbolCumpleTodasLasVerificaciones() {
    for (Arboles.Caso caso : Arboles.casos()) {
      verificar(caso);
    }
  }
  
  @Test
  void alfaBetaVisitaMenosNodosQueMinimaxEnAlMenosUnArbol() {
    boolean seObservoPoda = false;
    for (Arboles.Caso caso : Arboles.casos()) {
      ResultadoMinimax minimax = Minimax.resolverConEstadisticas(caso.raiz());
      ResultadoMinimax alfaBeta = Minimax.resolverConPodaYEstadisticas(caso.raiz());
      seObservoPoda |= alfaBeta.nodosVisitados() < minimax.nodosVisitados();
    }
    assertTrue(seObservoPoda, "Alfa-beta debió visitar menos nodos que Minimax en algún árbol (hubo poda)");
  }
}

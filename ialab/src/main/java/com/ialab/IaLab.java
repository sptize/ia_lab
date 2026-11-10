package com.ialab;

import com.ialab.busquedainformadadecaminos.core.BusquedaAEstrella;
import com.ialab.busquedainformadadecaminos.core.BusquedaPrimeroMejorVoraz;
import com.ialab.consola.*;

import java.util.List;


/**
 * Punto de entrada: compone los comandos y lanza el menú.
 */
public final class IaLab {
  
  /**
   * Crea la consola, registra los comandos disponibles y ejecuta el menú principal.
   *
   *
   */
  void main() {
    Consola consola = new Consola(System.in, System.out);
    List<Comando> comandos = List.of(
        //Búsqueda informada
        new ComandoBusquedaCaminos("A* - laberinto", List.of(new BusquedaAEstrella())),
        new ComandoBusquedaCaminosGrafos("A* - grafos", List.of(new BusquedaAEstrella())),
        new ComandoBusquedaCaminos("Greedy Best-First - laberinto", List.of(new BusquedaPrimeroMejorVoraz())),
        new ComandoBusquedaCaminosGrafos("Greedy Best-First - grafos", List.of(new BusquedaPrimeroMejorVoraz())),
        new ComandoBusquedaCaminos
            ("Comparar A* vs Greedy en laberinto", List.of(new BusquedaAEstrella(), new BusquedaPrimeroMejorVoraz())),
        new ComandoBusquedaCaminosGrafos("Comparar A* vs Greedy en un grafo", List.of(new BusquedaAEstrella(), new BusquedaPrimeroMejorVoraz())),
        //Búsqueda local
        new ComandoHillClimbingGrafos(),
        new ComandoOchoReinas(),
        //Búsqueda adversarial
        new ComandoMinimaxArbol(),
        new ComandoMinimaxTresEnRaya()
    );
    new Menu(consola, comandos).run();
  }
}

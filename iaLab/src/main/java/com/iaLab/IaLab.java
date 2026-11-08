package com.iaLab;

import java.util.List;

import com.iaLab.busquedainformadadecaminos.core.BusquedaAEstrella;
import com.iaLab.busquedainformadadecaminos.core.BusquedaPrimeroMejorVoraz;
import com.iaLab.consola.Comando;
import com.iaLab.consola.ComandoBusquedaCaminos;
import com.iaLab.consola.ComandoBusquedaCaminosGrafos;
import com.iaLab.consola.ComandoMinimaxArbol;
import com.iaLab.consola.ComandoMinimaxTresEnRaya;
import com.iaLab.consola.ComandoOchoReinas;
import com.iaLab.consola.Consola;
import com.iaLab.consola.Menu;


/** Punto de entrada: compone los comandos y lanza el menú. */
public final class IaLab {

    /**
     * Crea la consola, registra los comandos disponibles y ejecuta el menú principal.
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        Consola consola = new Consola(System.in, System.out);
        List<Comando> comandos = List.of(
                //Problemas aplicados a casos de uso concretos
                new ComandoBusquedaCaminos("A* - laberinto", List.of(new BusquedaAEstrella())),
                new ComandoBusquedaCaminos("Greedy Best-First - laberinto", List.of(new BusquedaPrimeroMejorVoraz())),                              
                new ComandoBusquedaCaminos("Comparar A* vs Greedy en laberinto", List.of(new BusquedaAEstrella(), new BusquedaPrimeroMejorVoraz())),
                new ComandoOchoReinas(),  
                //Problemas que usan grafos a modo general
                new ComandoBusquedaCaminosGrafos("A* - grafos", List.of(new BusquedaAEstrella())),
                new ComandoBusquedaCaminosGrafos("Greedy Best-First - grafos", List.of(new BusquedaPrimeroMejorVoraz())),
                new ComandoBusquedaCaminosGrafos("Comparar A* vs Greedy en un grafo", List.of(new BusquedaAEstrella(), new BusquedaPrimeroMejorVoraz())),
                //Búsqueda adversarial
                new ComandoMinimaxArbol(),
                new ComandoMinimaxTresEnRaya()

            
            );
        new Menu(consola, comandos).run();
    }
}

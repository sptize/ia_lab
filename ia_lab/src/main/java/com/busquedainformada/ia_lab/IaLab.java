
package com.busquedainformada.ia_lab;

import java.util.List;

import com.busquedainformada.ia_lab.algoritmos.BusquedaAEstrella;
import com.busquedainformada.ia_lab.algoritmos.BusquedaPrimeroMejorVoraz;
import com.busquedainformada.ia_lab.consola.ComandoBusquedaCaminos;
import com.busquedainformada.ia_lab.consola.ComandoOchoReinas;
import com.busquedainformada.ia_lab.consola.Comando;
import com.busquedainformada.ia_lab.consola.Consola;
import com.busquedainformada.ia_lab.consola.Menu;

/** Punto de entrada: compone los comandos y lanza el menú. */
public final class IaLab {

    /**
     * Crea la consola, registra los comandos disponibles y ejecuta el menú principal.
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        Consola consola = new Consola(System.in, System.out);
        List<Comando> comandos = List.of(new ComandoBusquedaCaminos("A* - camino de menor costo", List.of(new BusquedaAEstrella())),
                new ComandoBusquedaCaminos("Greedy Best-First - camino", List.of(new BusquedaPrimeroMejorVoraz())),
                new ComandoOchoReinas(),
                
                new ComandoBusquedaCaminos("Comparar A* vs Greedy", List.of(new BusquedaAEstrella(), new BusquedaPrimeroMejorVoraz())));
        new Menu(consola, comandos).run();
    }
    //TODO adicionar minimax
}

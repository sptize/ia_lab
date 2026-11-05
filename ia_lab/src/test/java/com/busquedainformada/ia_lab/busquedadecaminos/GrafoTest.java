package com.busquedainformada.ia_lab.busquedadecaminos;

import java.util.List;

import com.busquedainformada.ia_lab.algoritmos.BusquedaAEstrella;
import com.busquedainformada.ia_lab.algoritmos.BusquedaPrimeroMejorVoraz;
import com.busquedainformada.ia_lab.algoritmos.IAlgoritmoBusqueda;

public class GrafoTest {
     public static void main(String[] a) {
        Grafo grafo = Grafo.ejemplo();

        //Test de búsqueda de caminos en el grafo usando A* y Greedy Best-First Search
        var problema = new GrafoProblema(grafo);
        List<IAlgoritmoBusqueda> algoritmos = List.of(new BusquedaAEstrella(), new BusquedaPrimeroMejorVoraz());
        
        for (IAlgoritmoBusqueda algoritmo : algoritmos) {

            var resultado = algoritmo.solucionar(problema);
            resultado.ifPresentOrElse(r -> {
                System.out.println("Algoritmo: " + algoritmo.getClass().getSimpleName());
                System.out.println("Costo total: " + r.costoTotal());
                System.out.println("Pasos: " + r.acciones().size());
                System.out.println("Nodos expandidos: " + r.nodosExpandidos());
                System.out.println("Movimientos: " + String.join(" , ", r.acciones()));
                System.out.println();
            }, () -> {
                System.out.println("No se encontró solución para el algoritmo: " + algoritmo.getClass().getSimpleName());
            });
        }
      
        //Tests de parsing       
        Grafo grafoParseado = Grafo.parsear(
            List.of(
                "# ejemplo de grafo",               
                "inicio: S",
                "meta: G", "nodos:", "S 6", "A 2", "B 4", "C 3", "G 0",
                "aristas:", "S A 4", "A G 20", "S B 2", "B C 2", "C G 3"));
        System.out.println("Grafo parseado:");
        System.out.println(grafoParseado);
        System.out.println("Inicio: " + grafoParseado.getInicio());
        System.out.println("Meta: " + grafoParseado.getMeta());
        System.out.println("Nodos: " + grafoParseado.nodos());
        System.out.println("Heurística de B: " + grafoParseado.getHeuristica("B"));
        System.out.println("Aristas desde A: " + grafoParseado.aristasDesde("A"));

        //Test de búsqueda de caminos en el grafo parseado usando A* y Greedy Best-First Search
        var problemaParseado = new GrafoProblema(grafoParseado);
        List<IAlgoritmoBusqueda> algoritmosParseados = List.of(new BusquedaAEstrella(), new BusquedaPrimeroMejorVoraz());
        for (IAlgoritmoBusqueda algoritmo : algoritmosParseados) {
            var resultado = algoritmo.solucionar(problemaParseado);
            resultado.ifPresentOrElse(r -> {
                System.out.println("Algoritmo: " + algoritmo.getClass().getSimpleName());
                System.out.println("Costo total: " + r.costoTotal());
                System.out.println("Pasos: " + r.acciones().size());
                System.out.println("Nodos expandidos: " + r.nodosExpandidos());
                System.out.println("Movimientos: " + String.join(" , ", r.acciones()));
                System.out.println();
            }, () -> {
                System.out.println("No se encontró solución para el algoritmo: " + algoritmo.getClass().getSimpleName());
            });
        }

        invalido("inicio no definido", "nodos:", "A 1", "B 2", "aristas:", "A B 1");
        invalido("meta no definido", "inicio: A", "nodos:", "A 1", "B 2", "aristas:", "A B 1");
        invalido("nodo con heurística negativa", "inicio: A", "meta: B", "nodos:", "A 1", "B -2", "aristas:", "A B 1");     
        invalido("NaN en heurística", "inicio: A", "meta: B", "nodos:", "A 1", "B NaN", "aristas:", "A B 1");
        invalido("arista con costo negativo", "inicio: A", "meta: B", "nodos:", "A 1", "B 2", "aristas:", "A B -1");
        invalido("arista con costo NaN", "inicio: A", "meta: B", "nodos:", "A 1", "B 2", "aristas:", "A B NaN");



    }
    static void invalido(String caso, String... lineas){
        try {
            Grafo.parsear(List.of(lineas));
            System.out.println("Error: el caso " + caso + " debería haber lanzado una excepción.");
        } catch (IllegalArgumentException e) {
            System.out.println("Caso " + caso + " correctamente detectado como inválido: " + e.getMessage());
        }
    }



}

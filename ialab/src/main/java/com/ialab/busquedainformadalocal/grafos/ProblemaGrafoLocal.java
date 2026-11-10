package com.ialab.busquedainformadalocal.grafos;

import com.ialab.grafo.Grafo;
import com.ialab.busquedainformadalocal.core.IProblemaBusquedaLocal;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Adapta un grafo al contrato de búsqueda local, usando la heurística como costo.
 */
public final class ProblemaGrafoLocal implements IProblemaBusquedaLocal<String> {
  
  private final Grafo grafo;
  private final List<String> nodos;
  
  public ProblemaGrafoLocal(Grafo grafo) {
    this.grafo = Objects.requireNonNull(grafo, "grafo");
    this.nodos = List.copyOf(grafo.nodos());
  }
  
  @Override
  public String estadoAleatorio(Random aleatorio) {
    Objects.requireNonNull(aleatorio, "aleatorio");
    return nodos.get(aleatorio.nextInt(nodos.size()));
  }
  
  @Override
  public List<String> vecinos(String estado) {
    return grafo.aristasDesde(estado).stream()
        .map(Grafo.Arista::destino)
        .toList();
  }
  
  @Override
  public double costo(String estado) {
    return estado.equals(grafo.getMeta()) ? 0 : grafo.getHeuristica(estado);
  }
  
  @Override
  public boolean esSolucion(String estado) {
    return estado.equals(grafo.getMeta());
  }
}

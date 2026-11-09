package com.busquedainformada.iaLab.busquedadecaminos;

import com.iaLab.busquedainformadadecaminos.laberintos.Cuadricula;
import com.iaLab.busquedainformadadecaminos.laberintos.Posicion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CuadriculaTest {
  
  @Test
  void costoDeSoloAceptaPosicionesTransitables() {
    Cuadricula mapa = Cuadricula.parse(List.of("S#G", ".2."));
    
    assertEquals(2, mapa.costoDe(new Posicion(1, 1)));
    assertThrows(IllegalArgumentException.class, () -> mapa.costoDe(new Posicion(0, 1)));
    assertThrows(IllegalArgumentException.class, () -> mapa.costoDe(new Posicion(-1, 0)));
  }
}

package com.busquedainformada.iaLab.busquedadecaminos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

import com.iaLab.busquedainformadadecaminos.laberintos.Cuadricula;
import com.iaLab.busquedainformadadecaminos.laberintos.Posicion;

class CuadriculaTest {

    @Test
    void costoDeSoloAceptaPosicionesTransitables() {
        Cuadricula mapa = Cuadricula.parse(List.of("S#G", ".2."));

        assertEquals(2, mapa.costoDe(new Posicion(1, 1)));
        assertThrows(IllegalArgumentException.class, () -> mapa.costoDe(new Posicion(0, 1)));
        assertThrows(IllegalArgumentException.class, () -> mapa.costoDe(new Posicion(-1, 0)));
    }
}

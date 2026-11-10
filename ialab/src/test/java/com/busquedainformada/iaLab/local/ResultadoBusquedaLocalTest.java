package com.busquedainformada.iaLab.local;

import com.ialab.busquedainformadalocal.core.ResultadoBusquedaLocal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResultadoBusquedaLocalTest {
  
  @Test
  void validaCostoFinitoContadoresYCoherenciaDeSolucion() {
    assertDoesNotThrow(() -> new ResultadoBusquedaLocal<>("estado", 0, true, 0, 0));
    assertDoesNotThrow(() -> new ResultadoBusquedaLocal<>("estado", -2, false, 0, 0));
    assertThrows(IllegalArgumentException.class,
        () -> new ResultadoBusquedaLocal<>("estado", Double.NaN, false, 0, 0));
    assertThrows(IllegalArgumentException.class,
        () -> new ResultadoBusquedaLocal<>("estado", Double.POSITIVE_INFINITY, false, 0, 0));
    assertThrows(NullPointerException.class,
        () -> new ResultadoBusquedaLocal<>(null, 1, false, 0, 0));
    assertThrows(IllegalArgumentException.class,
        () -> new ResultadoBusquedaLocal<>("estado", 1, true, 0, 0));
    assertThrows(IllegalArgumentException.class,
        () -> new ResultadoBusquedaLocal<>("estado", 1, false, -1, 0));
    assertThrows(IllegalArgumentException.class,
        () -> new ResultadoBusquedaLocal<>("estado", 1, false, 0, -1));
  }
}

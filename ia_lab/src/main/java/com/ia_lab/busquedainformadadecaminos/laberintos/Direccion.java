package com.ia_lab.busquedainformadadecaminos.laberintos;


/** Movimientos permitidos (4 direcciones). */
public enum Direccion {
    ARRIBA("ARRIBA", -1, 0),
    ABAJO("ABAJO", 1, 0),
    IZQUIERDA("IZQUIERDA", 0, -1),
    DERECHA("DERECHA", 0, 1);

    private final String etiqueta;
    private final int filaDelta;
    private final int colDelta;

    Direccion(String etiqueta, int filaDelta, int colDelta) {
        this.etiqueta = etiqueta;
        this.filaDelta = filaDelta;
        this.colDelta = colDelta;
    }

    /**
     * Devuelve la etiqueta textual de la dirección.
     *
     * @return etiqueta de la dirección
     */
    String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Devuelve el desplazamiento de fila de la dirección.
     *
     * @return desplazamiento de fila
     */
    int getFilaDelta() {
        return filaDelta;
    }

    /**
     * Devuelve el desplazamiento de columna de la dirección.
     *
     * @return desplazamiento de columna
     */
    int getColDelta() {
        return colDelta;
    }
}

package com.iaLab.consola;


/**
 * Patrón Comandos: cada opción del menú encapsula una acción ejecutable.
 */
public interface Comando {
  
  /**
   * Devuelve el título que se muestra en el menú.
   *
   * @return título del comando
   */
  String titulo();
  
  /**
   * Ejecuta el comando utilizando la consola indicada.
   *
   * @param consola consola para interactuar con el usuario
   */
  void execute(Consola consola);
}

package com.example.demo.exceptions;

/**
 * Excepción lanzada cuando no se encuentra un usuario en el sistema.
 */
public class UsuarioNoEncontradoException extends RuntimeException {
    
    /**
     * Constructor por defecto
     */
    public UsuarioNoEncontradoException() {
        super("Usuario no encontrado");
    }
    
    /**
     * Constructor con mensaje personalizado
     * @param mensaje Mensaje descriptivo de la excepción
     */
    public UsuarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
    
    /**
     * Constructor con mensaje y causa de la excepción
     * @param mensaje Mensaje descriptivo de la excepción
     * @param causa Causa original de la excepción
     */
    public UsuarioNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
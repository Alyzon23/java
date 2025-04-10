package com.example.demo.exceptions;

public class CampoInvalidoException extends RuntimeException {
    public CampoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
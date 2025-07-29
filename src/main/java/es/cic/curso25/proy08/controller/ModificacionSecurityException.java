package es.cic.curso25.proy08.controller;

public class ModificacionSecurityException extends RuntimeException{

    public ModificacionSecurityException(){
        super("Intento de modificación en la creación");
    }

    public ModificacionSecurityException(String mensaje){
        super(mensaje);
    }

    public ModificacionSecurityException(String mensaje, Throwable throwable){
        super(mensaje, throwable);
    }
}

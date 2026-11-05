package notificaciones;

public interface Sujeto {
    void suscribir(Observador observador);
    void desuscribir(Observador observador);
    void notificarObservadores(String mensaje);
}
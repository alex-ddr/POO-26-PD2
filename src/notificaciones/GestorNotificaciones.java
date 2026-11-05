package notificaciones;

import java.util.ArrayList;
import java.util.List;

public class GestorNotificaciones implements Sujeto {
    private final List<Observador> observadores;

    public GestorNotificaciones() {
        this.observadores = new ArrayList<>();
    }

    @Override
    public void suscribir(Observador observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void desuscribir(Observador observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores(String mensaje) {
        for (Observador obs : observadores) {
            obs.actualizar(mensaje);
        }
    }

    public void notificarEditorialEspecifica(Observador editorial, String mensaje) {
        if (observadores.contains(editorial)) {
            editorial.actualizar(mensaje);
        }
    }
}
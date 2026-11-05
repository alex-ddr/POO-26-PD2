package datos;

import notificaciones.Observador;

public class Editorial implements Observador {
    private int id;
    private String nombre;
    private Reserva reservaAsociada;

    public Editorial(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.reservaAsociada = null;
    }

    @Override
    public void actualizar(String mensaje) {
        System.out.println("[NOTIFICACIÓN -> Editorial '" + nombre + "' (ID: " + id + ")]: " + mensaje);
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Reserva getReservaAsociada() {
        return reservaAsociada;
    }

    public void setReservaAsociada(Reserva reservaAsociada) {
        this.reservaAsociada = reservaAsociada;
    }

    @Override
    public String toString() {
        String infoReserva = (reservaAsociada != null)
                ? "Stand ID " + reservaAsociada.getStand().getId() + " (" + reservaAsociada.getStand().getEstado() + ")"
                : "Sin reserva";
        return "Editorial [ID=" + id + ", Nombre='" + nombre + "', Reserva=" + infoReserva + "]";
    }
}
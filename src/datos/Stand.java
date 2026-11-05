package datos;

import java.util.Objects;

public class Stand {
    private int id;
    private String nombre;
    private double metrosCuadrados;
    private double precioPorMetroCuadrado;
    private EstadoStand estado;

    public Stand(int id, String nombre, double metrosCuadrados, double precioPorMetroCuadrado) {
        this.id = id;
        this.nombre = nombre;
        this.metrosCuadrados = metrosCuadrados;
        this.precioPorMetroCuadrado = precioPorMetroCuadrado;
        this.estado = EstadoStand.DISPONIBLE;
    }

    public double calcularPrecioTotal() {
        return this.metrosCuadrados * this.precioPorMetroCuadrado;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getMetrosCuadrados() {
        return metrosCuadrados;
    }

    public double getPrecioPorMetroCuadrado() {
        return precioPorMetroCuadrado;
    }

    public void setPrecioPorMetroCuadrado(double precioPorMetroCuadrado) {
        this.precioPorMetroCuadrado = precioPorMetroCuadrado;
    }

    public EstadoStand getEstado() {
        return estado;
    }

    public void setEstado(EstadoStand estado) {
        this.estado = estado;
    }

    @Override
    public int hashCode() {
        return id + Objects.hashCode(nombre) + Double.hashCode(metrosCuadrados);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Stand other = (Stand) obj;
        return id == other.id
                && Double.compare(metrosCuadrados, other.metrosCuadrados) == 0
                && Objects.equals(nombre, other.nombre);
    }

    @Override
    public String toString() {
        return String.format("Stand [ID=%d, Nombre='%s', Area=%.2f m2, Precio/m2=$%.2f, CostoTotal=$%.2f, Estado=%s]",
                id, nombre, metrosCuadrados, precioPorMetroCuadrado, calcularPrecioTotal(), estado);
    }
}
package datos;

import java.time.LocalDate;

public class Reserva {
    private Stand stand;
    private Editorial editorial;
    private LocalDate fechaCreacion;
    private double costoTotalAcordado;
    private double montoPagado;
    private boolean descuentoProntoPagoAplicado;

    public Reserva(Stand stand, Editorial editorial, LocalDate fechaCreacion) {
        this.stand = stand;
        this.editorial = editorial;
        this.fechaCreacion = fechaCreacion;
        this.costoTotalAcordado = stand.calcularPrecioTotal();
        this.montoPagado = 0.0;
        this.descuentoProntoPagoAplicado = false;
    }

    public void aplicarDescuentoProntoPago(double porcentajeDescuento) {
        if (!descuentoProntoPagoAplicado && porcentajeDescuento > 0 && porcentajeDescuento < 100) {
            double factor = 1.0 - (porcentajeDescuento / 100.0);
            this.costoTotalAcordado = stand.calcularPrecioTotal() * factor;
            this.descuentoProntoPagoAplicado = true;
        }
    }

    public void abonarPago(double monto) {
        if (monto > 0) {
            this.montoPagado += monto;
        }
    }

    public double obtenerPorcentajePagado() {
        if (costoTotalAcordado == 0) return 100.0;
        return (montoPagado / costoTotalAcordado) * 100.0;
    }

    public double obtenerSaldoPendiente() {
        return Math.max(0.0, costoTotalAcordado - montoPagado);
    }

    public boolean estaLiquidada() {
        return Double.compare(montoPagado, costoTotalAcordado) >= 0;
    }

    public Stand getStand() {
        return stand;
    }

    public Editorial getEditorial() {
        return editorial;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public double getCostoTotalAcordado() {
        return costoTotalAcordado;
    }

    public double getMontoPagado() {
        return montoPagado;
    }

    public boolean isDescuentoProntoPagoAplicado() {
        return descuentoProntoPagoAplicado;
    }

    @Override
    public String toString() {
        return String.format("Reserva [Editorial='%s', Stand=%d, Fecha=%s, Pagado=$%.2f / $%.2f (%.1f%%), EstadoStand=%s]",
                editorial.getNombre(), stand.getId(), fechaCreacion, montoPagado, costoTotalAcordado,
                obtenerPorcentajePagado(), stand.getEstado());
    }
}
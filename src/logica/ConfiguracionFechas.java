package logica;

import java.time.LocalDate;

public class ConfiguracionFechas {
    private LocalDate fechaLimiteReserva;
    private LocalDate fechaDescuentoProntoPago;
    private LocalDate fechaLimitePagoTotal;
    private int diasMaximosParaPagarReserva;
    private double porcentajeDescuentoProntoPago;

    public ConfiguracionFechas(LocalDate fechaLimiteReserva,
                               LocalDate fechaDescuentoProntoPago,
                               LocalDate fechaLimitePagoTotal,
                               int diasMaximosParaPagarReserva,
                               double porcentajeDescuentoProntoPago) {
        this.fechaLimiteReserva = fechaLimiteReserva;
        this.fechaDescuentoProntoPago = fechaDescuentoProntoPago;
        this.fechaLimitePagoTotal = fechaLimitePagoTotal;
        this.diasMaximosParaPagarReserva = diasMaximosParaPagarReserva;
        this.porcentajeDescuentoProntoPago = porcentajeDescuentoProntoPago;
    }

    public LocalDate getFechaLimiteReserva() {
        return fechaLimiteReserva;
    }

    public void setFechaLimiteReserva(LocalDate fechaLimiteReserva) {
        this.fechaLimiteReserva = fechaLimiteReserva;
    }

    public LocalDate getFechaDescuentoProntoPago() {
        return fechaDescuentoProntoPago;
    }

    public void setFechaDescuentoProntoPago(LocalDate fechaDescuentoProntoPago) {
        this.fechaDescuentoProntoPago = fechaDescuentoProntoPago;
    }

    public LocalDate getFechaLimitePagoTotal() {
        return fechaLimitePagoTotal;
    }

    public void setFechaLimitePagoTotal(LocalDate fechaLimitePagoTotal) {
        this.fechaLimitePagoTotal = fechaLimitePagoTotal;
    }

    public int getDiasMaximosParaPagarReserva() {
        return diasMaximosParaPagarReserva;
    }

    public void setDiasMaximosParaPagarReserva(int diasMaximosParaPagarReserva) {
        this.diasMaximosParaPagarReserva = diasMaximosParaPagarReserva;
    }

    public double getPorcentajeDescuentoProntoPago() {
        return porcentajeDescuentoProntoPago;
    }

    public void setPorcentajeDescuentoProntoPago(double porcentajeDescuentoProntoPago) {
        this.porcentajeDescuentoProntoPago = porcentajeDescuentoProntoPago;
    }
}
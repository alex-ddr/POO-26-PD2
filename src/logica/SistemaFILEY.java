package logica;

import datos.Editorial;
import datos.EstadoStand;
import datos.Reserva;
import datos.Stand;
import notificaciones.GestorNotificaciones;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SistemaFILEY {
    private static final double PORCENTAJE_MINIMO_OCUPACION = 15.0;

    private final Map<Integer, Stand> catalogoStands;
    private final Map<Integer, Editorial> catalogoEditoriales;
    private final List<Reserva> listaReservas;
    private final ConfiguracionFechas configuracionFechas;
    private final GestorNotificaciones gestorNotificaciones;

    public SistemaFILEY(ConfiguracionFechas configuracionFechas) {
        this.catalogoStands = new HashMap<>();
        this.catalogoEditoriales = new HashMap<>();
        this.listaReservas = new ArrayList<>();
        this.configuracionFechas = configuracionFechas;
        this.gestorNotificaciones = new GestorNotificaciones();
    }

    // 1. Registro de entidades
    public void registrarStand(Stand stand) {
        catalogoStands.put(stand.getId(), stand);
    }

    public void registrarEditorial(Editorial editorial) {
        catalogoEditoriales.put(editorial.getId(), editorial);
        gestorNotificaciones.suscribir(editorial);
    }

    // 2. Administración de estados y bloqueo administrativo
    public boolean bloquearStand(int idStand) {
        Stand stand = catalogoStands.get(idStand);
        if (stand != null && stand.getEstado() == EstadoStand.DISPONIBLE) {
            stand.setEstado(EstadoStand.BLOQUEADO);
            return true;
        }
        return false;
    }

    public boolean liberarStandBloqueado(int idStand) {
        Stand stand = catalogoStands.get(idStand);
        if (stand != null && stand.getEstado() == EstadoStand.BLOQUEADO) {
            stand.setEstado(EstadoStand.DISPONIBLE);
            return true;
        }
        return false;
    }

    // 3. Creación de reserva estándar (Disponible -> Reservado)
    public boolean reservarStand(int idEditorial, int idStand, LocalDate fechaActual) {
        if (fechaActual.isAfter(configuracionFechas.getFechaLimiteReserva())) {
            System.out.println("Error: La fecha límite para realizar reservas ha expirado.");
            return false;
        }

        Editorial editorial = catalogoEditoriales.get(idEditorial);
        Stand stand = catalogoStands.get(idStand);

        if (editorial == null || stand == null) {
            System.out.println("Error: Editorial o Stand inexistente.");
            return false;
        }

        if (editorial.getReservaAsociada() != null) {
            System.out.println("Error: La editorial ya cuenta con una reserva activa.");
            return false;
        }

        if (stand.getEstado() != EstadoStand.DISPONIBLE) {
            System.out.println("Error: El stand " + idStand + " no está DISPONIBLE (Estado actual: " + stand.getEstado() + ").");
            return false;
        }

        stand.setEstado(EstadoStand.RESERVADO);
        Reserva nuevaReserva = new Reserva(stand, editorial, fechaActual);
        editorial.setReservaAsociada(nuevaReserva);
        listaReservas.add(nuevaReserva);
        return true;
    }

    // 6. Funcionalidad Opcional: Transferir un stand BLOQUEADO directamente a una editorial (pasa a RESERVADO)
    public boolean transferirStandBloqueadoAEditorial(int idStand, int idEditorial, LocalDate fechaActual) {
        Stand stand = catalogoStands.get(idStand);
        Editorial editorial = catalogoEditoriales.get(idEditorial);

        if (stand == null || editorial == null) {
            System.out.println("Error en transferencia: Stand o Editorial no encontrados.");
            return false;
        }

        if (stand.getEstado() != EstadoStand.BLOQUEADO) {
            System.out.println("Error en transferencia: El stand debe estar en estado BLOQUEADO.");
            return false;
        }

        if (editorial.getReservaAsociada() != null) {
            System.out.println("Error en transferencia: La editorial ya tiene una reserva asignada.");
            return false;
        }

        stand.setEstado(EstadoStand.RESERVADO);
        Reserva reservaTransferida = new Reserva(stand, editorial, fechaActual);
        editorial.setReservaAsociada(reservaTransferida);
        listaReservas.add(reservaTransferida);
        gestorNotificaciones.notificarEditorialEspecifica(
                editorial,
                "Se le ha transferido directamente el Stand #" + stand.getId() + " desde administración. Estado actual: RESERVADO."
        );
        return true;
    }

    // 4. Gestión de pagos y transición de RESERVADO a OCUPADO (mínimo 15%)
    public boolean registrarPago(int idEditorial, double monto, LocalDate fechaPago) {
        Editorial editorial = catalogoEditoriales.get(idEditorial);
        if (editorial == null || editorial.getReservaAsociada() == null) {
            System.out.println("Error de pago: La editorial no tiene una reserva activa.");
            return false;
        }

        Reserva reserva = editorial.getReservaAsociada();
        Stand stand = reserva.getStand();

        // Verificar si el plazo individual de reserva venció (si aún está en estado RESERVADO)
        LocalDate fechaVencimientoReserva = reserva.getFechaCreacion()
                .plusDays(configuracionFechas.getDiasMaximosParaPagarReserva());

        if (stand.getEstado() == EstadoStand.RESERVADO && fechaPago.isAfter(fechaVencimientoReserva)) {
            System.out.println("Error: El plazo máximo de " + configuracionFechas.getDiasMaximosParaPagarReserva()
                    + " días para confirmar la reserva ha vencido.");
            return false;
        }

        if (fechaPago.isAfter(configuracionFechas.getFechaLimitePagoTotal())) {
            System.out.println("Error: La fecha límite de pago total ha expirado.");
            return false;
        }

        // Aplicar descuento por pronto pago si se realiza antes o en la fecha límite de pronto pago
        if (!fechaPago.isAfter(configuracionFechas.getFechaDescuentoProntoPago())
                && !reserva.isDescuentoProntoPagoAplicado()) {
            reserva.aplicarDescuentoProntoPago(configuracionFechas.getPorcentajeDescuentoProntoPago());
            System.out.println("¡Descuento por pronto pago (" + configuracionFechas.getPorcentajeDescuentoProntoPago()
                    + "%) aplicado a la editorial " + editorial.getNombre() + "!");
        }

        reserva.abonarPago(monto);

        // Regla del 15%: pasar de RESERVADO a OCUPADO
        if (stand.getEstado() == EstadoStand.RESERVADO
                && Double.compare(reserva.obtenerPorcentajePagado(), PORCENTAJE_MINIMO_OCUPACION) >= 0) {
            stand.setEstado(EstadoStand.OCUPADO);
            System.out.printf("El Stand #%d ha alcanzado el %.1f%% de pago (>= 15%%) y pasa a estado OCUPADO.%n",
                    stand.getId(), reserva.obtenerPorcentajePagado());
        }

        return true;
    }

    // 5. Comprobación de vencimientos y ejecución de notificaciones (Patrón Observer)
    public void comprobarVencimientosYNotificar(LocalDate fechaActual, int diasAvisoPrevio) {
        System.out.println("\n--- Ejecutando comprobación de vencimientos (Fecha actual: " + fechaActual + ") ---");

        // A. Aviso general de cierre de reservas (para editoriales sin reserva)
        long diasParaCierreReservas = ChronoUnit.DAYS.between(fechaActual, configuracionFechas.getFechaLimiteReserva());
        if (diasParaCierreReservas >= 0 && diasParaCierreReservas <= diasAvisoPrevio) {
            for (Editorial ed : catalogoEditoriales.values()) {
                if (ed.getReservaAsociada() == null) {
                    gestorNotificaciones.notificarEditorialEspecifica(ed,
                            "Quedan " + diasParaCierreReservas + " días para la fecha límite de reserva de stands ("
                                    + configuracionFechas.getFechaLimiteReserva() + ").");
                }
            }
        }

        // B. Avisos específicos para editoriales con reserva activa
        for (Reserva reserva : listaReservas) {
            if (reserva.estaLiquidada()) {
                continue;
            }

            Editorial ed = reserva.getEditorial();
            Stand st = reserva.getStand();

            // 1. Vencimiento del tiempo máximo para pagar el 15% de la reserva
            if (st.getEstado() == EstadoStand.RESERVADO) {
                LocalDate limiteConfirmacion = reserva.getFechaCreacion()
                        .plusDays(configuracionFechas.getDiasMaximosParaPagarReserva());
                long diasRestantesReserva = ChronoUnit.DAYS.between(fechaActual, limiteConfirmacion);

                if (diasRestantesReserva >= 0 && diasRestantesReserva <= diasAvisoPrevio) {
                    gestorNotificaciones.notificarEditorialEspecifica(ed,
                            "Su reserva del Stand #" + st.getId() + " vence en " + diasRestantesReserva
                                    + " días (" + limiteConfirmacion + "). Requiere cubrir al menos el 15% para pasar a OCUPADO.");
                }
            }

            // 2. Vencimiento de fecha de descuento por pronto pago
            if (!reserva.isDescuentoProntoPagoAplicado()) {
                long diasParaProntoPago = ChronoUnit.DAYS.between(fechaActual, configuracionFechas.getFechaDescuentoProntoPago());
                if (diasParaProntoPago >= 0 && diasParaProntoPago <= diasAvisoPrevio) {
                    gestorNotificaciones.notificarEditorialEspecifica(ed,
                            "Aproveche el descuento por pronto pago antes del "
                                    + configuracionFechas.getFechaDescuentoProntoPago()
                                    + " (quedan " + diasParaProntoPago + " días).");
                }
            }

            // 3. Vencimiento de fecha límite para cubrir el pago total
            long diasParaPagoTotal = ChronoUnit.DAYS.between(fechaActual, configuracionFechas.getFechaLimitePagoTotal());
            if (diasParaPagoTotal >= 0 && diasParaPagoTotal <= diasAvisoPrevio) {
                gestorNotificaciones.notificarEditorialEspecifica(ed,
                        "La fecha límite para liquidar el pago total de su Stand #" + st.getId()
                                + " (Saldo pendiente: $" + String.format("%.2f", reserva.obtenerSaldoPendiente())
                                + ") vence en " + diasParaPagoTotal + " días ("
                                + configuracionFechas.getFechaLimitePagoTotal() + ").");
            }
        }
    }

    public ConfiguracionFechas getConfiguracionFechas() {
        return configuracionFechas;
    }

    public void mostrarResumenSistema() {
        System.out.println("\n=== ESTADO DE LOS STANDS ===");
        catalogoStands.values().forEach(System.out::println);
        System.out.println("\n=== ESTADO DE LAS RESERVAS ===");
        listaReservas.forEach(System.out::println);
    }
}
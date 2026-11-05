import datos.Editorial;
import datos.Stand;
import logica.ConfiguracionFechas;
import logica.SistemaFILEY;

import java.time.LocalDate;

public class MainFILEY {
    public static void main(String[] args) {
        // 1. Configuración inicial de fechas
        ConfiguracionFechas config = new ConfiguracionFechas(
                LocalDate.of(2026, 11, 20), // Fecha límite de reserva
                LocalDate.of(2026, 11, 25), // Fecha descuento pronto pago
                LocalDate.of(2026, 12, 15), // Fecha límite pago total
                10,                         // 10 días máximo para pagar el 15% de una reserva
                10.0                        // 10% de descuento por pronto pago
        );

        SistemaFILEY sistema = new SistemaFILEY(config);

        // 2. Creación y registro de Stands
        Stand s1 = new Stand(1, "Stand A - Pasillo Central", 25.0, 1000.0); // $25,000
        Stand s2 = new Stand(2, "Stand B - Entrada Norte", 40.0, 1200.0);   // $48,000
        Stand s3 = new Stand(3, "Stand C - Pabellón Infantil", 20.0, 900.0);// $18,000

        sistema.registrarStand(s1);
        sistema.registrarStand(s2);
        sistema.registrarStand(s3);

        // 3. Registro de Editoriales (se suscriben automáticamente al Observer)
        Editorial ed1 = new Editorial(101, "Fondo de Cultura Económica");
        Editorial ed2 = new Editorial(102, "Editorial Planeta");
        Editorial ed3 = new Editorial(103, "Ediciones UADY");

        sistema.registrarEditorial(ed1);
        sistema.registrarEditorial(ed2);
        sistema.registrarEditorial(ed3);

        LocalDate hoy = LocalDate.of(2026, 11, 10);

        // 4. Reserva normal del Stand 1 por Editorial 101
        sistema.reservarStand(101, 1, hoy);

        // 5. Bloqueo administrativo del Stand 2 y uso de la funcionalidad opcional (transferencia directa)
        sistema.bloquearStand(2);
        System.out.println("Stand 2 bloqueado por administrador: " + s2);
        sistema.transferirStandBloqueadoAEditorial(2, 102, hoy);

        // 6. Prueba de pagos:
        // Stand 1 cuesta $25,000. Con 10% de descuento por pronto pago = $22,500.
        // El 15% mínimo de $22,500 es $3,375.
        System.out.println("\n--- Procesando pagos ---");
        sistema.registrarPago(101, 2000.0, LocalDate.of(2026, 11, 12)); // < 15%, sigue RESERVADO
        sistema.registrarPago(101, 2000.0, LocalDate.of(2026, 11, 14)); // Total $4,000 (> 15%), pasa a OCUPADO

        sistema.mostrarResumenSistema();

        // 7. Comprobación de vencimientos (Patrón Observer en acción)
        // Simulamos estar al 18 de noviembre de 2026 (a 2 días del cierre de reservas y del vencimiento del Stand 2)
        sistema.comprobarVencimientosYNotificar(LocalDate.of(2026, 11, 18), 5);

        // 8. Modificación dinámica de fechas configurables
        config.setFechaLimitePagoTotal(LocalDate.of(2026, 12, 20));
        sistema.comprobarVencimientosYNotificar(LocalDate.of(2026, 12, 17), 5);
    }
}
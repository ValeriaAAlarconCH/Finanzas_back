package com.example.finanzas_back.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.finanzas_back.interfaces.INotificacionService;
import com.example.finanzas_back.dtos.NotificacionDto;
import com.example.finanzas_back.entities.*;
import com.example.finanzas_back.repositories.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacionService implements INotificacionService {

    @Autowired
    private CuotaRepository cuotaRepository;

    @Autowired
    private CreditoRepository creditoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ModelMapper modelMapper;

    // En producción, esto sería una tabla en la BD
    private List<NotificacionDto> notificacionesSistema = new ArrayList<>();

    @Override
    public void enviarNotificacionVencimiento(Cuota cuota) {
        try {
            if (cuota == null || cuota.getCredito() == null ||
                    cuota.getCredito().getCliente() == null) {
                return;
            }

            Cliente cliente = cuota.getCredito().getCliente();
            String email = cliente.getEmail();

            if (email != null && !email.isEmpty()) {
                // Crear contenido del email
                String asunto = "Recordatorio: Cuota #" + cuota.getNumero_cuota() + " próxima a vencer";
                String mensaje = construirMensajeVencimiento(cuota, cliente);

                // En producción, aquí se integraría con un servicio de email
                // Por ahora, solo creamos la notificación en el sistema
                crearNotificacionSistema("email", "Notificación de vencimiento enviada a " + email,
                        cuota.getId_cuota());

                System.out.println("Email enviado a " + email + ": " + asunto);
            }

        } catch (Exception e) {
            System.err.println("Error enviando notificación de vencimiento: " + e.getMessage());
        }
    }

    @Override
    public void enviarNotificacionPagoConfirmado(Cuota cuota) {
        try {
            if (cuota == null || cuota.getCredito() == null ||
                    cuota.getCredito().getCliente() == null) {
                return;
            }

            Cliente cliente = cuota.getCredito().getCliente();
            String email = cliente.getEmail();

            if (email != null && !email.isEmpty()) {
                String asunto = "Confirmación de pago - Cuota #" + cuota.getNumero_cuota();
                String mensaje = construirMensajePagoConfirmado(cuota, cliente);

                crearNotificacionSistema("email", "Confirmación de pago enviada a " + email,
                        cuota.getId_cuota());

                System.out.println("Email de confirmación enviado a " + email);
            }

        } catch (Exception e) {
            System.err.println("Error enviando notificación de pago: " + e.getMessage());
        }
    }

    @Override
    public void enviarNotificacionCreditoAprobado(Credito credito) {
        try {
            if (credito == null || credito.getCliente() == null) {
                return;
            }

            Cliente cliente = credito.getCliente();
            String email = cliente.getEmail();

            if (email != null && !email.isEmpty()) {
                String asunto = "¡Felicidades! Su crédito ha sido aprobado";
                String mensaje = construirMensajeCreditoAprobado(credito, cliente);

                crearNotificacionSistema("email", "Aprobación de crédito enviada a " + email,
                        credito.getId_credito());

                System.out.println("Email de aprobación enviado a " + email);
            }

        } catch (Exception e) {
            System.err.println("Error enviando notificación de aprobación: " + e.getMessage());
        }
    }

    @Override
    public void enviarNotificacionRecordatorio(LocalDate fecha) {
        try {
            // Buscar cuotas que vencen en 3 días
            LocalDate fechaVencimiento = fecha.plusDays(3);

            List<Cuota> cuotasPorVencer = cuotaRepository.findByFechaVencimientoBetween(
                    fecha, fechaVencimiento);

            // Filtrar solo cuotas pendientes
            cuotasPorVencer = cuotasPorVencer.stream()
                    .filter(c -> "pendiente".equals(c.getEstado()))
                    .collect(Collectors.toList());

            // Enviar notificación para cada cuota
            for (Cuota cuota : cuotasPorVencer) {
                enviarNotificacionVencimiento(cuota);
            }

            // Crear notificación de sistema
            crearNotificacionSistema("sistema",
                    "Se enviaron " + cuotasPorVencer.size() + " recordatorios de vencimiento",
                    null);

        } catch (Exception e) {
            System.err.println("Error enviando recordatorios: " + e.getMessage());
        }
    }

    @Override
    public void crearNotificacionSistema(String tipo, String mensaje, Long referenciaId) {
        NotificacionDto notificacion = new NotificacionDto();
        notificacion.setTipo(tipo);
        notificacion.setDestinatario("sistema");
        notificacion.setAsunto("Notificación del sistema");
        notificacion.setMensaje(mensaje);
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setEnviada(true);
        notificacion.setLeida(false);
        notificacion.setReferenciaId(referenciaId);
        notificacion.setPrioridad("media");

        // Generar ID (en producción sería autoincremental de BD)
        notificacion.setIdNotificacion(System.currentTimeMillis());

        notificacionesSistema.add(notificacion);
    }

    @Override
    public List<NotificacionDto> obtenerNotificacionesPendientes() {
        // Filtrar notificaciones no leídas
        return notificacionesSistema.stream()
                .filter(n -> !n.getLeida())
                .sorted((n1, n2) -> n2.getFechaEnvio().compareTo(n1.getFechaEnvio()))
                .collect(Collectors.toList());
    }

    @Override
    public void marcarComoLeida(Long idNotificacion) {
        notificacionesSistema.stream()
                .filter(n -> n.getIdNotificacion().equals(idNotificacion))
                .findFirst()
                .ifPresent(n -> n.setLeida(true));
    }

    @Override
    public void eliminarNotificacion(Long idNotificacion) {
        notificacionesSistema.removeIf(n -> n.getIdNotificacion().equals(idNotificacion));
    }

    @Override
    @Scheduled(cron = "0 0 9 * * ?") // Todos los días a las 9:00 AM
    public void programarNotificacionesDiarias() {
        System.out.println("Ejecutando notificaciones diarias...");
        enviarNotificacionRecordatorio(LocalDate.now());
    }

    @Override
    @Scheduled(cron = "0 0 10 1 * ?") // Primer día de cada mes a las 10:00 AM
    public void programarNotificacionesMensuales() {
        System.out.println("Ejecutando notificaciones mensuales...");

        // Aquí se podrían programar notificaciones mensuales como:
        // - Resumen mensual para clientes
        // - Reportes para administradores
        // - Recordatorios de mantenimiento

        crearNotificacionSistema("sistema",
                "Notificaciones mensuales procesadas para " + LocalDate.now().getMonth(),
                null);
    }

    // Métodos auxiliares privados
    private String construirMensajeVencimiento(Cuota cuota, Cliente cliente) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Estimado/a ").append(cliente.getNombres()).append(" ").append(cliente.getApellidos()).append(",\n\n");
        mensaje.append("Le recordamos que su cuota #").append(cuota.getNumero_cuota()).append("\n");
        mensaje.append("del crédito ").append(cuota.getCredito().getNumero_contrato()).append("\n");
        mensaje.append("tiene fecha de vencimiento: ").append(cuota.getFecha_vencimiento()).append("\n");
        mensaje.append("Monto a pagar: S/ ").append(String.format("%.2f", cuota.getTotal_cuota())).append("\n\n");
        mensaje.append("Por favor realice el pago antes de la fecha indicada.\n\n");
        mensaje.append("Saludos cordiales,\n");
        mensaje.append("Equipo de Finanzas Hipotecarias");

        return mensaje.toString();
    }

    private String construirMensajePagoConfirmado(Cuota cuota, Cliente cliente) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Estimado/a ").append(cliente.getNombres()).append(" ").append(cliente.getApellidos()).append(",\n\n");
        mensaje.append("Confirmamos el pago de su cuota #").append(cuota.getNumero_cuota()).append("\n");
        mensaje.append("del crédito ").append(cuota.getCredito().getNumero_contrato()).append("\n");
        mensaje.append("Monto pagado: S/ ").append(String.format("%.2f", cuota.getTotal_cuota())).append("\n");
        mensaje.append("Nuevo saldo: S/ ").append(String.format("%.2f", cuota.getSaldo_final())).append("\n\n");
        mensaje.append("¡Gracias por su pago puntual!\n\n");
        mensaje.append("Saludos cordiales,\n");
        mensaje.append("Equipo de Finanzas Hipotecarias");

        return mensaje.toString();
    }

    private String construirMensajeCreditoAprobado(Credito credito, Cliente cliente) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Estimado/a ").append(cliente.getNombres()).append(" ").append(cliente.getApellidos()).append(",\n\n");
        mensaje.append("¡Felicidades! Su solicitud de crédito ha sido APROBADA.\n\n");
        mensaje.append("Detalles del crédito:\n");
        mensaje.append("- Número de contrato: ").append(credito.getNumero_contrato()).append("\n");
        mensaje.append("- Monto aprobado: S/ ").append(String.format("%.2f", credito.getMonto_principal())).append("\n");
        mensaje.append("- Tasa anual: ").append(credito.getTasa_anual()).append("%\n");
        mensaje.append("- Plazo: ").append(credito.getPlazo_meses()).append(" meses\n");
        mensaje.append("- Cuota mensual aproximada: S/ ").append(calcularCuotaAproximada(credito)).append("\n\n");
        mensaje.append("Pronto nos comunicaremos con usted para la firma de contratos.\n\n");
        mensaje.append("Saludos cordiales,\n");
        mensaje.append("Equipo de Finanzas Hipotecarias");

        return mensaje.toString();
    }

    private Double calcularCuotaAproximada(Credito credito) {
        // Cálculo simplificado de cuota
        Double tasaMensual = credito.getTasa_anual() / 12 / 100;
        Integer plazo = credito.getPlazo_meses();
        Double monto = credito.getMonto_principal();

        if (tasaMensual == 0) {
            return monto / plazo;
        }

        Double factor = Math.pow(1 + tasaMensual, plazo);
        return monto * (tasaMensual * factor) / (factor - 1);
    }

    public void procesarPagoConfirmado(Long idCuota) {
        Cuota cuota = cuotaRepository.findById(idCuota).orElse(null);
        if (cuota != null) {
            enviarNotificacionPagoConfirmado(cuota);
        }
    }

    public void procesarCreditoAprobado(Long idCredito) {
        Credito credito = creditoRepository.findById(idCredito).orElse(null);
        if (credito != null) {
            enviarNotificacionCreditoAprobado(credito);
        }
    }
}

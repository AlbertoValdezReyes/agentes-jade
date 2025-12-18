package Clinica.ui;

import Clinica.*;
import javafx.application.Platform;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Puente de comunicacion entre los agentes JADE y la interfaz JavaFX.
 * Permite la comunicacion bidireccional thread-safe.
 */
public class GUIBridge {

    private static GUIBridge instance;
    private ClinicaClienteApp app;

    // Latches para sincronizacion de formularios
    private CountDownLatch datosMedicosLatch;
    private CountDownLatch sintomasLatch;
    private CountDownLatch pagoLatch;

    // Referencias atomicas para datos recibidos de formularios
    private AtomicReference<Paciente> pacienteActual = new AtomicReference<>();
    private AtomicReference<Consulta> consultaActual = new AtomicReference<>();
    private AtomicReference<Pago> pagoActual = new AtomicReference<>();

    // Callbacks para notificar al agente
    private Consumer<Cita> onCitaCreada;
    private Consumer<String> onCitaCancelada;

    private GUIBridge() {
    }

    public static synchronized GUIBridge getInstance() {
        if (instance == null) {
            instance = new GUIBridge();
        }
        return instance;
    }

    public void setApp(ClinicaClienteApp app) {
        this.app = app;
    }

    public ClinicaClienteApp getApp() {
        return app;
    }

    // Metodos para agregar mensajes al chat desde los agentes
    public void agregarMensajeRecepcionista(String mensaje) {
        if (app != null) {
            Platform.runLater(() -> app.agregarMensaje(mensaje, MensajeChat.TipoAgente.RECEPCIONISTA));
        }
    }

    public void agregarMensajeFisioterapeuta(String mensaje) {
        if (app != null) {
            Platform.runLater(() -> app.agregarMensaje(mensaje, MensajeChat.TipoAgente.FISIOTERAPEUTA));
        }
    }

    public void agregarMensajeAyudante(String mensaje) {
        if (app != null) {
            Platform.runLater(() -> app.agregarMensaje(mensaje, MensajeChat.TipoAgente.AYUDANTE));
        }
    }

    public void agregarMensajeSistema(String mensaje) {
        if (app != null) {
            Platform.runLater(() -> app.agregarMensaje(mensaje, MensajeChat.TipoAgente.SISTEMA));
        }
    }

    // Metodos para solicitar datos al usuario mediante formularios
    public void solicitarDatosMedicos(String idCita) {
        datosMedicosLatch = new CountDownLatch(1);
        if (app != null) {
            Platform.runLater(() -> app.mostrarFormularioMedico(idCita));
        }
    }

    public Paciente esperarDatosMedicos() {
        try {
            if (datosMedicosLatch != null) {
                datosMedicosLatch.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return pacienteActual.get();
    }

    public void enviarDatosMedicos(Paciente paciente) {
        pacienteActual.set(paciente);
        if (datosMedicosLatch != null) {
            datosMedicosLatch.countDown();
        }
    }

    public void solicitarSintomas(String idCita, Paciente paciente) {
        sintomasLatch = new CountDownLatch(1);
        if (app != null) {
            Platform.runLater(() -> app.mostrarFormularioSintomas(idCita, paciente));
        }
    }

    public Consulta esperarSintomas() {
        try {
            if (sintomasLatch != null) {
                sintomasLatch.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return consultaActual.get();
    }

    public void enviarSintomas(Consulta consulta) {
        consultaActual.set(consulta);
        if (sintomasLatch != null) {
            sintomasLatch.countDown();
        }
    }

    public void solicitarPago(String idCita, double monto) {
        pagoLatch = new CountDownLatch(1);
        if (app != null) {
            Platform.runLater(() -> app.mostrarFormularioPago(idCita, monto));
        }
    }

    public Pago esperarPago() {
        try {
            if (pagoLatch != null) {
                pagoLatch.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return pagoActual.get();
    }

    public void enviarPago(Pago pago) {
        pagoActual.set(pago);
        if (pagoLatch != null) {
            pagoLatch.countDown();
        }
    }

    // Metodos para mostrar el diagnostico
    public void mostrarDiagnostico(Consulta consulta) {
        if (app != null) {
            Platform.runLater(() -> app.mostrarDiagnostico(consulta));
        }
    }

    // Callbacks para el agente
    public void setOnCitaCreada(Consumer<Cita> callback) {
        this.onCitaCreada = callback;
    }

    public void notificarCitaCreada(Cita cita) {
        if (onCitaCreada != null) {
            onCitaCreada.accept(cita);
        }
    }

    public void setOnCitaCancelada(Consumer<String> callback) {
        this.onCitaCancelada = callback;
    }

    public void notificarCitaCancelada(String idCita) {
        if (onCitaCancelada != null) {
            onCitaCancelada.accept(idCita);
        }
    }

    // Metodo para actualizar el estado en la UI
    public void actualizarEstado(String estado) {
        if (app != null) {
            Platform.runLater(() -> app.actualizarEstado(estado));
        }
    }

    // Metodo para habilitar/deshabilitar controles
    public void habilitarNuevaCita(boolean habilitar) {
        if (app != null) {
            Platform.runLater(() -> app.habilitarNuevaCita(habilitar));
        }
    }

    // Metodo para reiniciar la interfaz despues de completar o cancelar
    public void reiniciarInterfaz() {
        if (app != null) {
            Platform.runLater(() -> app.reiniciarInterfaz());
        }
    }
}

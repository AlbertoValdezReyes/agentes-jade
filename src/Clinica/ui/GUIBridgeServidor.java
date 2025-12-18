package Clinica.ui;

import Clinica.*;
import javafx.application.Platform;

/**
 * Puente de comunicacion entre los agentes del servidor (Fisioterapeuta y Ayudante)
 * y la interfaz JavaFX del servidor.
 */
public class GUIBridgeServidor {

    private static GUIBridgeServidor instance;
    private ClinicaServidorApp app;

    private GUIBridgeServidor() {
    }

    public static synchronized GUIBridgeServidor getInstance() {
        if (instance == null) {
            instance = new GUIBridgeServidor();
        }
        return instance;
    }

    public void setApp(ClinicaServidorApp app) {
        this.app = app;
    }

    public ClinicaServidorApp getApp() {
        return app;
    }

    // Metodos para agregar mensajes al log desde los agentes
    public void logFisioterapeuta(String mensaje) {
        if (app != null) {
            Platform.runLater(() -> app.agregarLog(mensaje, "FISIOTERAPEUTA"));
        }
    }

    public void logAyudante(String mensaje) {
        if (app != null) {
            Platform.runLater(() -> app.agregarLog(mensaje, "AYUDANTE"));
        }
    }

    public void logSistema(String mensaje) {
        if (app != null) {
            Platform.runLater(() -> app.agregarLog(mensaje, "SISTEMA"));
        }
    }

    // Metodos para actualizar informacion de citas
    public void actualizarCita(Cita cita) {
        if (app != null) {
            Platform.runLater(() -> app.actualizarInfoCita(cita));
        }
    }

    public void actualizarPaciente(Paciente paciente, String idCita) {
        if (app != null) {
            Platform.runLater(() -> app.actualizarInfoPaciente(paciente, idCita));
        }
    }

    public void mostrarDiagnostico(Consulta consulta, String idCita) {
        if (app != null) {
            Platform.runLater(() -> app.mostrarDiagnostico(consulta, idCita));
        }
    }

    public void actualizarEstado(String estado) {
        if (app != null) {
            Platform.runLater(() -> app.actualizarEstado(estado));
        }
    }

    public void limpiarInfo() {
        if (app != null) {
            Platform.runLater(() -> app.limpiarInfo());
        }
    }
}

package Clinica;

import jade.content.AgentAction;

/**
 * Accion para confirmar una cita.
 */
public class ConfirmarCita implements AgentAction {

    private String idCita;
    private String mensaje;
    private boolean confirmada;

    public ConfirmarCita() {
    }

    public String getIdCita() {
        return idCita;
    }

    public void setIdCita(String idCita) {
        this.idCita = idCita;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isConfirmada() {
        return confirmada;
    }

    public void setConfirmada(boolean confirmada) {
        this.confirmada = confirmada;
    }
}

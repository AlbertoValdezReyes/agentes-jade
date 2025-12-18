package Clinica;

import jade.content.AgentAction;

/**
 * Accion para cancelar una cita existente.
 */
public class CancelarCita implements AgentAction {

    private String idCita;
    private String motivo;

    public CancelarCita() {
    }

    public String getIdCita() {
        return idCita;
    }

    public void setIdCita(String idCita) {
        this.idCita = idCita;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}

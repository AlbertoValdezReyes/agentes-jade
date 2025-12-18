package Clinica;

import jade.content.AgentAction;

/**
 * Accion para solicitar datos medicos del paciente.
 * El Ayudante envia esta accion al Recepcionista.
 */
public class SolicitarDatos implements AgentAction {

    private String idCita;

    public SolicitarDatos() {
    }

    public String getIdCita() {
        return idCita;
    }

    public void setIdCita(String idCita) {
        this.idCita = idCita;
    }
}

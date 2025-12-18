package Clinica;

import jade.content.AgentAction;

/**
 * Accion para consultar los sintomas del paciente.
 * El Fisioterapeuta envia esta accion al Recepcionista.
 */
public class ConsultarSintomas implements AgentAction {

    private String idCita;
    private Paciente paciente;

    public ConsultarSintomas() {
    }

    public String getIdCita() {
        return idCita;
    }

    public void setIdCita(String idCita) {
        this.idCita = idCita;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
}

package Clinica;

import jade.content.AgentAction;

/**
 * Accion para enviar los datos medicos del paciente.
 * El Recepcionista envia esta accion al Ayudante con los datos recopilados.
 */
public class EnviarDatosMedicos implements AgentAction {

    private Paciente paciente;
    private String idCita;

    public EnviarDatosMedicos() {
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getIdCita() {
        return idCita;
    }

    public void setIdCita(String idCita) {
        this.idCita = idCita;
    }
}

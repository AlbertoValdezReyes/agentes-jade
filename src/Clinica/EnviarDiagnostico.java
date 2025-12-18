package Clinica;

import jade.content.AgentAction;

/**
 * Accion para enviar el diagnostico al paciente.
 * El Fisioterapeuta envia esta accion al Recepcionista con el tratamiento.
 */
public class EnviarDiagnostico implements AgentAction {

    private Consulta consulta;
    private String idCita;

    public EnviarDiagnostico() {
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    public String getIdCita() {
        return idCita;
    }

    public void setIdCita(String idCita) {
        this.idCita = idCita;
    }
}

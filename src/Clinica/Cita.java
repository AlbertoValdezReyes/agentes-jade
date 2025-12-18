package Clinica;

import jade.content.Concept;

/**
 * Concepto que representa una cita en la clinica de fisioterapia.
 */
public class Cita implements Concept {

    // Estados de la cita
    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_CONFIRMADA = "CONFIRMADA";
    public static final String ESTADO_EN_PROCESO = "EN_PROCESO";
    public static final String ESTADO_COMPLETADA = "COMPLETADA";
    public static final String ESTADO_CANCELADA = "CANCELADA";

    private String id;
    private String nombrePaciente;
    private String hora;
    private String tipoTerapia;
    private String estado;
    private Pago pago;
    private Paciente datosPaciente;
    private Consulta consulta;

    public Cita() {
        this.estado = ESTADO_PENDIENTE;
        this.id = generarId();
    }

    private String generarId() {
        return "CITA-" + System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String n) {
        nombrePaciente = n;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String h) {
        hora = h;
    }

    public String getTipoTerapia() {
        return tipoTerapia;
    }

    public void setTipoTerapia(String t) {
        tipoTerapia = t;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public Paciente getDatosPaciente() {
        return datosPaciente;
    }

    public void setDatosPaciente(Paciente datosPaciente) {
        this.datosPaciente = datosPaciente;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }
}

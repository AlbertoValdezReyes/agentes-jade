package Clinica;

import jade.content.Concept;

/**
 * Clase de datos que representa una consulta medica con sintomas y diagnostico.
 */
public class Consulta implements Concept {

    private String zonaDolor;
    private String descripcionSintomas;
    private int nivelDolor;
    private String procedimiento;
    private int sesionesRecomendadas;
    private String medicamentos;
    private String ejercicios;

    public Consulta() {
    }

    public String getZonaDolor() {
        return zonaDolor;
    }

    public void setZonaDolor(String zonaDolor) {
        this.zonaDolor = zonaDolor;
    }

    public String getDescripcionSintomas() {
        return descripcionSintomas;
    }

    public void setDescripcionSintomas(String descripcionSintomas) {
        this.descripcionSintomas = descripcionSintomas;
    }

    public int getNivelDolor() {
        return nivelDolor;
    }

    public void setNivelDolor(int nivelDolor) {
        this.nivelDolor = nivelDolor;
    }

    public String getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(String procedimiento) {
        this.procedimiento = procedimiento;
    }

    public int getSesionesRecomendadas() {
        return sesionesRecomendadas;
    }

    public void setSesionesRecomendadas(int sesionesRecomendadas) {
        this.sesionesRecomendadas = sesionesRecomendadas;
    }

    public String getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(String medicamentos) {
        this.medicamentos = medicamentos;
    }

    public String getEjercicios() {
        return ejercicios;
    }

    public void setEjercicios(String ejercicios) {
        this.ejercicios = ejercicios;
    }
}

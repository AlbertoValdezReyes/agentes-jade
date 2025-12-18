package Clinica;

import jade.content.onto.*;
import jade.content.schema.*;

/**
 * Ontologia de la clinica de fisioterapia.
 * Define todos los conceptos y acciones del sistema.
 */
public class ClinicaOntology extends Ontology {

    public static final String ONTOLOGY_NAME = "ontologia-clinica";

    // Vocabulario - Conceptos
    public static final String CITA = "Cita";
    public static final String CITA_ID = "id";
    public static final String CITA_PACIENTE = "nombrePaciente";
    public static final String CITA_HORA = "hora";
    public static final String CITA_TIPO = "tipoTerapia";
    public static final String CITA_ESTADO = "estado";
    public static final String CITA_PAGO = "pago";
    public static final String CITA_DATOS_PACIENTE = "datosPaciente";
    public static final String CITA_CONSULTA = "consulta";

    public static final String PACIENTE = "Paciente";
    public static final String PACIENTE_NOMBRE = "nombre";
    public static final String PACIENTE_ALTURA = "altura";
    public static final String PACIENTE_PESO = "peso";
    public static final String PACIENTE_DIABETES = "diabetes";
    public static final String PACIENTE_ALERGIAS = "alergias";

    public static final String PAGO = "Pago";
    public static final String PAGO_TIPO = "tipoPago";
    public static final String PAGO_MONTO = "monto";
    public static final String PAGO_NUMERO_TARJETA = "numeroTarjeta";
    public static final String PAGO_PAGADO = "pagado";

    public static final String CONSULTA = "Consulta";
    public static final String CONSULTA_ZONA = "zonaDolor";
    public static final String CONSULTA_SINTOMAS = "descripcionSintomas";
    public static final String CONSULTA_NIVEL_DOLOR = "nivelDolor";
    public static final String CONSULTA_PROCEDIMIENTO = "procedimiento";
    public static final String CONSULTA_SESIONES = "sesionesRecomendadas";
    public static final String CONSULTA_MEDICAMENTOS = "medicamentos";
    public static final String CONSULTA_EJERCICIOS = "ejercicios";

    // Vocabulario - Acciones
    public static final String AGENDAR = "Agendar";
    public static final String AGENDAR_CITA = "cita";

    public static final String SOLICITAR_DATOS = "SolicitarDatos";
    public static final String SOLICITAR_DATOS_ID_CITA = "idCita";

    public static final String ENVIAR_DATOS_MEDICOS = "EnviarDatosMedicos";
    public static final String ENVIAR_DATOS_MEDICOS_PACIENTE = "paciente";
    public static final String ENVIAR_DATOS_MEDICOS_ID_CITA = "idCita";

    public static final String CONSULTAR_SINTOMAS = "ConsultarSintomas";
    public static final String CONSULTAR_SINTOMAS_ID_CITA = "idCita";
    public static final String CONSULTAR_SINTOMAS_PACIENTE = "paciente";

    public static final String ENVIAR_SINTOMAS = "EnviarSintomas";
    public static final String ENVIAR_SINTOMAS_CONSULTA = "consulta";
    public static final String ENVIAR_SINTOMAS_ID_CITA = "idCita";

    public static final String ENVIAR_DIAGNOSTICO = "EnviarDiagnostico";
    public static final String ENVIAR_DIAGNOSTICO_CONSULTA = "consulta";
    public static final String ENVIAR_DIAGNOSTICO_ID_CITA = "idCita";

    public static final String REALIZAR_PAGO = "RealizarPago";
    public static final String REALIZAR_PAGO_PAGO = "pago";
    public static final String REALIZAR_PAGO_ID_CITA = "idCita";

    public static final String CANCELAR_CITA = "CancelarCita";
    public static final String CANCELAR_CITA_ID = "idCita";
    public static final String CANCELAR_CITA_MOTIVO = "motivo";

    public static final String CONFIRMAR_CITA = "ConfirmarCita";
    public static final String CONFIRMAR_CITA_ID = "idCita";
    public static final String CONFIRMAR_CITA_MENSAJE = "mensaje";
    public static final String CONFIRMAR_CITA_CONFIRMADA = "confirmada";

    private static Ontology instancia = new ClinicaOntology();

    public static Ontology getInstance() {
        return instancia;
    }

    private ClinicaOntology() {
        super(ONTOLOGY_NAME, BasicOntology.getInstance());
        try {
            // === CONCEPTOS ===

            // Esquema del Concepto Paciente
            add(new ConceptSchema(PACIENTE), Paciente.class);
            ConceptSchema pacienteSchema = (ConceptSchema) getSchema(PACIENTE);
            pacienteSchema.add(PACIENTE_NOMBRE, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
            pacienteSchema.add(PACIENTE_ALTURA, (PrimitiveSchema) getSchema(BasicOntology.FLOAT), ObjectSchema.OPTIONAL);
            pacienteSchema.add(PACIENTE_PESO, (PrimitiveSchema) getSchema(BasicOntology.FLOAT), ObjectSchema.OPTIONAL);
            pacienteSchema.add(PACIENTE_DIABETES, (PrimitiveSchema) getSchema(BasicOntology.BOOLEAN), ObjectSchema.OPTIONAL);
            pacienteSchema.add(PACIENTE_ALERGIAS, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);

            // Esquema del Concepto Pago
            add(new ConceptSchema(PAGO), Pago.class);
            ConceptSchema pagoSchema = (ConceptSchema) getSchema(PAGO);
            pagoSchema.add(PAGO_TIPO, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
            pagoSchema.add(PAGO_MONTO, (PrimitiveSchema) getSchema(BasicOntology.FLOAT), ObjectSchema.OPTIONAL);
            pagoSchema.add(PAGO_NUMERO_TARJETA, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
            pagoSchema.add(PAGO_PAGADO, (PrimitiveSchema) getSchema(BasicOntology.BOOLEAN), ObjectSchema.OPTIONAL);

            // Esquema del Concepto Consulta
            add(new ConceptSchema(CONSULTA), Consulta.class);
            ConceptSchema consultaSchema = (ConceptSchema) getSchema(CONSULTA);
            consultaSchema.add(CONSULTA_ZONA, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
            consultaSchema.add(CONSULTA_SINTOMAS, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
            consultaSchema.add(CONSULTA_NIVEL_DOLOR, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.OPTIONAL);
            consultaSchema.add(CONSULTA_PROCEDIMIENTO, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
            consultaSchema.add(CONSULTA_SESIONES, (PrimitiveSchema) getSchema(BasicOntology.INTEGER), ObjectSchema.OPTIONAL);
            consultaSchema.add(CONSULTA_MEDICAMENTOS, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
            consultaSchema.add(CONSULTA_EJERCICIOS, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);

            // Esquema del Concepto Cita
            add(new ConceptSchema(CITA), Cita.class);
            ConceptSchema citaSchema = (ConceptSchema) getSchema(CITA);
            citaSchema.add(CITA_ID, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
            citaSchema.add(CITA_PACIENTE, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
            citaSchema.add(CITA_HORA, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
            citaSchema.add(CITA_TIPO, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
            citaSchema.add(CITA_ESTADO, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
            citaSchema.add(CITA_PAGO, (ConceptSchema) getSchema(PAGO), ObjectSchema.OPTIONAL);
            citaSchema.add(CITA_DATOS_PACIENTE, (ConceptSchema) getSchema(PACIENTE), ObjectSchema.OPTIONAL);
            citaSchema.add(CITA_CONSULTA, (ConceptSchema) getSchema(CONSULTA), ObjectSchema.OPTIONAL);

            // === ACCIONES ===

            // Esquema de la Accion Agendar
            add(new AgentActionSchema(AGENDAR), Agendar.class);
            AgentActionSchema agendarSchema = (AgentActionSchema) getSchema(AGENDAR);
            agendarSchema.add(AGENDAR_CITA, (ConceptSchema) getSchema(CITA));

            // Esquema de la Accion SolicitarDatos
            add(new AgentActionSchema(SOLICITAR_DATOS), SolicitarDatos.class);
            AgentActionSchema solicitarSchema = (AgentActionSchema) getSchema(SOLICITAR_DATOS);
            solicitarSchema.add(SOLICITAR_DATOS_ID_CITA, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);

            // Esquema de la Accion EnviarDatosMedicos
            add(new AgentActionSchema(ENVIAR_DATOS_MEDICOS), EnviarDatosMedicos.class);
            AgentActionSchema enviarDatosSchema = (AgentActionSchema) getSchema(ENVIAR_DATOS_MEDICOS);
            enviarDatosSchema.add(ENVIAR_DATOS_MEDICOS_PACIENTE, (ConceptSchema) getSchema(PACIENTE), ObjectSchema.OPTIONAL);
            enviarDatosSchema.add(ENVIAR_DATOS_MEDICOS_ID_CITA, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);

            // Esquema de la Accion ConsultarSintomas
            add(new AgentActionSchema(CONSULTAR_SINTOMAS), ConsultarSintomas.class);
            AgentActionSchema consultarSchema = (AgentActionSchema) getSchema(CONSULTAR_SINTOMAS);
            consultarSchema.add(CONSULTAR_SINTOMAS_ID_CITA, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
            consultarSchema.add(CONSULTAR_SINTOMAS_PACIENTE, (ConceptSchema) getSchema(PACIENTE), ObjectSchema.OPTIONAL);

            // Esquema de la Accion EnviarSintomas
            add(new AgentActionSchema(ENVIAR_SINTOMAS), EnviarSintomas.class);
            AgentActionSchema enviarSintomasSchema = (AgentActionSchema) getSchema(ENVIAR_SINTOMAS);
            enviarSintomasSchema.add(ENVIAR_SINTOMAS_CONSULTA, (ConceptSchema) getSchema(CONSULTA), ObjectSchema.OPTIONAL);
            enviarSintomasSchema.add(ENVIAR_SINTOMAS_ID_CITA, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);

            // Esquema de la Accion EnviarDiagnostico
            add(new AgentActionSchema(ENVIAR_DIAGNOSTICO), EnviarDiagnostico.class);
            AgentActionSchema diagnosticoSchema = (AgentActionSchema) getSchema(ENVIAR_DIAGNOSTICO);
            diagnosticoSchema.add(ENVIAR_DIAGNOSTICO_CONSULTA, (ConceptSchema) getSchema(CONSULTA), ObjectSchema.OPTIONAL);
            diagnosticoSchema.add(ENVIAR_DIAGNOSTICO_ID_CITA, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);

            // Esquema de la Accion RealizarPago
            add(new AgentActionSchema(REALIZAR_PAGO), RealizarPago.class);
            AgentActionSchema pagoActionSchema = (AgentActionSchema) getSchema(REALIZAR_PAGO);
            pagoActionSchema.add(REALIZAR_PAGO_PAGO, (ConceptSchema) getSchema(PAGO), ObjectSchema.OPTIONAL);
            pagoActionSchema.add(REALIZAR_PAGO_ID_CITA, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);

            // Esquema de la Accion CancelarCita
            add(new AgentActionSchema(CANCELAR_CITA), CancelarCita.class);
            AgentActionSchema cancelarSchema = (AgentActionSchema) getSchema(CANCELAR_CITA);
            cancelarSchema.add(CANCELAR_CITA_ID, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
            cancelarSchema.add(CANCELAR_CITA_MOTIVO, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);

            // Esquema de la Accion ConfirmarCita
            add(new AgentActionSchema(CONFIRMAR_CITA), ConfirmarCita.class);
            AgentActionSchema confirmarSchema = (AgentActionSchema) getSchema(CONFIRMAR_CITA);
            confirmarSchema.add(CONFIRMAR_CITA_ID, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
            confirmarSchema.add(CONFIRMAR_CITA_MENSAJE, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
            confirmarSchema.add(CONFIRMAR_CITA_CONFIRMADA, (PrimitiveSchema) getSchema(BasicOntology.BOOLEAN), ObjectSchema.OPTIONAL);

        } catch (OntologyException oe) {
            oe.printStackTrace();
        }
    }
}

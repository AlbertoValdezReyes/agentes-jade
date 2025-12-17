package Clinica;

import jade.content.onto.*;
import jade.content.schema.*;

public class ClinicaOntology extends Ontology {
    
    public static final String ONTOLOGY_NAME = "ontologia-clinica";

    // Vocabulario
    public static final String CITA = "Cita";
    public static final String CITA_PACIENTE = "nombrePaciente";
    public static final String CITA_HORA = "hora";
    public static final String CITA_TIPO = "tipoTerapia";

    public static final String AGENDAR = "Agendar";
    public static final String AGENDAR_CITA = "cita";

    private static Ontology instancia = new ClinicaOntology();

    public static Ontology getInstance() {
        return instancia;
    }

    private ClinicaOntology() {
        super(ONTOLOGY_NAME, BasicOntology.getInstance());
        try {
            // Esquema del Concepto Cita
            add(new ConceptSchema(CITA), Cita.class);
            ConceptSchema cs = (ConceptSchema) getSchema(CITA);
            cs.add(CITA_PACIENTE, (PrimitiveSchema) getSchema(BasicOntology.STRING));
            cs.add(CITA_HORA, (PrimitiveSchema) getSchema(BasicOntology.STRING));
            cs.add(CITA_TIPO, (PrimitiveSchema) getSchema(BasicOntology.STRING));

            // Esquema de la Acción Agendar
            add(new AgentActionSchema(AGENDAR), Agendar.class);
            AgentActionSchema as = (AgentActionSchema) getSchema(AGENDAR);
            as.add(AGENDAR_CITA, (ConceptSchema) getSchema(CITA));

        } catch (OntologyException oe) {
            oe.printStackTrace();
        }
    }
}
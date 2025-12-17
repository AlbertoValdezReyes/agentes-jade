package Clinica;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;

public class Fisioterapeuta extends Agent {

    private Codec codec = new SLCodec();
    private Ontology ontologia = ClinicaOntology.getInstance();

    protected void setup() {
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontologia);

        System.out.println("Fisioterapeuta listo esperando citas...");
        addBehaviour(new GestionarCitasBehaviour());
    }

    class GestionarCitasBehaviour extends CyclicBehaviour {
        public void action() {
            // Solo recibir mensajes que usen nuestra ontología
            MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchLanguage(codec.getName()),
                    MessageTemplate.MatchOntology(ontologia.getName()));
            
            ACLMessage msg = receive(mt);

            if (msg != null) {
                try {
                    ContentElement ce = getContentManager().extractContent(msg);
                    
                    if (ce instanceof Agendar) {
                        Agendar agendar = (Agendar) ce;
                        Cita cita = agendar.getCita();
                        
                        System.out.println("\n[FISIOTERAPEUTA]: Solicitud recibida para " + cita.getNombrePaciente());
                        System.out.println("Evaluando disponibilidad...");

                        // 1. Avisar al Ayudante
                        ACLMessage msgAyudante = new ACLMessage(ACLMessage.INFORM);
                        msgAyudante.addReceiver(new AID("Ayudante", AID.ISLOCALNAME));
                        msgAyudante.setLanguage(codec.getName());
                        msgAyudante.setOntology(ontologia.getName());
                        msgAyudante.setContent("Preparar sala para: " + cita.getTipoTerapia());
                        send(msgAyudante);
                        
                        // 2. Confirmar a Recepcionista
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent("Cita Aceptada para " + cita.getHora());
                        send(reply);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                block();
            }
        }
    }
}

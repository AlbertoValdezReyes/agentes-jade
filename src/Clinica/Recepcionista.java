package Clinica;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Recepcionista extends Agent {

    private Codec codec = new SLCodec();
    private Ontology ontologia = ClinicaOntology.getInstance();

    protected void setup() {
        // Registrar lenguaje y ontología
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontologia);

        System.out.println("Recepcionista " + getAID().getName() + " iniciada.");
        addBehaviour(new InterfazUsuarioBehaviour(this));
    }

    class InterfazUsuarioBehaviour extends SimpleBehaviour {
        private boolean finished = false;

        public InterfazUsuarioBehaviour(Agent a) {
            super(a);
        }

        public void action() {
            try {
                BufferedReader buff = new BufferedReader(new InputStreamReader(System.in));
                
                System.out.println("\n--- NUEVA CITA ---");
                System.out.println("Ingrese Nombre del Paciente:");
                String nombre = buff.readLine();
                
                System.out.println("Ingrese Hora de la cita:");
                String hora = buff.readLine();

                System.out.println("Ingrese Tipo de Terapia:");
                String tipo = buff.readLine();

                // Crear objeto Cita
                Cita c = new Cita();
                c.setNombrePaciente(nombre);
                c.setHora(hora);
                c.setTipoTerapia(tipo);

                // Crear Acción Agendar
                Agendar accion = new Agendar();
                accion.setCita(c);

                // Crear Mensaje ACL
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                // Asumimos que el fisioterapeuta se llama "Fisioterapeuta"
                msg.addReceiver(new AID("Fisioterapeuta", AID.ISLOCALNAME)); 
                msg.setLanguage(codec.getName());
                msg.setOntology(ontologia.getName());

                // Llenar contenido
                getContentManager().fillContent(msg, accion);
                send(msg);
                
                System.out.println("Solicitud enviada al Fisioterapeuta.");
                
                // Esperar confirmación brevemente (bloqueante simple para el ejemplo)
                ACLMessage respuesta = blockingReceive(MessageTemplate.MatchSender(new AID("Fisioterapeuta", AID.ISLOCALNAME)));
                if(respuesta != null) {
                    System.out.println("Respuesta del Fisioterapeuta: " + respuesta.getContent());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean done() {
            return false; // Ciclo infinito para seguir atendiendo
        }
    }
}
package Clinica;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class Ayudante extends Agent {

    protected void setup() {
        System.out.println("Ayudante listo para preparar salas.");
        
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("\n[AYUDANTE]: Orden recibida de " + msg.getSender().getLocalName());
                    System.out.println("Acción: " + msg.getContent());
                    System.out.println("... Sala preparada y desinfectada ...");
                } else {
                    block();
                }
            }
        });
    }
}

package Clinica;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Agente Ayudante - Se ejecuta en el servidor (Computadora A).
 * Prepara las salas y recopila datos medicos de los pacientes.
 */
public class Ayudante extends Agent {

    private Codec codec = new SLCodec();
    private Ontology ontologia = ClinicaOntology.getInstance();

    // Almacen de citas pendientes de datos medicos
    private Map<String, Cita> citasPendientes = new HashMap<>();

    @Override
    protected void setup() {
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontologia);

        System.out.println("[AYUDANTE] Listo para preparar salas y recopilar datos.");

        addBehaviour(new GestionarTareasBehaviour());
    }

    /**
     * Comportamiento principal para gestionar tareas del Ayudante.
     */
    class GestionarTareasBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage msg = receive();

            if (msg != null) {
                try {
                    procesarMensaje(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("[AYUDANTE] Error procesando mensaje: " + e.getMessage());
                }
            } else {
                block();
            }
        }

        private void procesarMensaje(ACLMessage msg) throws Exception {
            String sender = msg.getSender().getLocalName();
            int performative = msg.getPerformative();

            // Verificar si tiene contenido estructurado
            if (msg.getLanguage() != null && msg.getLanguage().equals(codec.getName())) {
                ContentElement ce = getContentManager().extractContent(msg);

                if (ce instanceof Agendar) {
                    // Recibe instruccion del Fisioterapeuta para preparar sala
                    procesarNuevaCita((Agendar) ce, msg);
                } else if (ce instanceof EnviarDatosMedicos) {
                    // Recibe datos medicos del Recepcionista
                    procesarDatosMedicos((EnviarDatosMedicos) ce, msg);
                }
            } else if (performative == ACLMessage.CANCEL) {
                // Cita cancelada
                System.out.println("[AYUDANTE] Cita cancelada - " + msg.getContent());
            } else {
                // Mensaje simple
                System.out.println("[AYUDANTE] Mensaje recibido de " + sender + ": " + msg.getContent());
            }
        }

        private void procesarNuevaCita(Agendar agendar, ACLMessage msg) {
            Cita cita = agendar.getCita();
            String idCita = cita.getId();

            System.out.println("\n[AYUDANTE] Preparando sala para nueva cita:");
            System.out.println("  - ID: " + idCita);
            System.out.println("  - Paciente: " + cita.getNombrePaciente());
            System.out.println("  - Terapia: " + cita.getTipoTerapia());

            // Simular preparacion de sala
            prepararSala(cita.getTipoTerapia());

            // Almacenar cita como pendiente de datos medicos
            citasPendientes.put(idCita, cita);

            // Solicitar datos medicos al Recepcionista
            solicitarDatosMedicos(idCita);

            System.out.println("[AYUDANTE] Solicitud de datos medicos enviada");
        }

        private void procesarDatosMedicos(EnviarDatosMedicos enviarDatos, ACLMessage msg) {
            Paciente paciente = enviarDatos.getPaciente();
            String idCita = enviarDatos.getIdCita();

            System.out.println("\n[AYUDANTE] Datos medicos recibidos para cita " + idCita);
            System.out.println("  - Altura: " + paciente.getAltura() + "m");
            System.out.println("  - Peso: " + paciente.getPeso() + "kg");
            System.out.println("  - IMC: " + String.format("%.1f", paciente.calcularIMC()));
            System.out.println("  - Diabetes: " + (paciente.isDiabetes() ? "Si" : "No"));
            System.out.println("  - Alergias: " + paciente.getAlergias());

            // Verificar IMC y dar advertencias si es necesario
            double imc = paciente.calcularIMC();
            if (imc > 30) {
                System.out.println("[AYUDANTE] NOTA: Paciente con obesidad (IMC > 30). Informar al Fisioterapeuta.");
            } else if (imc < 18.5) {
                System.out.println("[AYUDANTE] NOTA: Paciente con bajo peso (IMC < 18.5). Informar al Fisioterapeuta.");
            }

            // Actualizar la cita con los datos del paciente
            Cita cita = citasPendientes.get(idCita);
            if (cita != null) {
                paciente.setNombre(cita.getNombrePaciente());
                cita.setDatosPaciente(paciente);
            }

            // Enviar datos al Fisioterapeuta para que continue con la consulta
            notificarFisioterapeuta(idCita, paciente);

            // Remover de pendientes
            citasPendientes.remove(idCita);

            System.out.println("[AYUDANTE] Datos enviados al Fisioterapeuta. Expediente completo.");
        }
    }

    // Simular la preparacion de la sala segun el tipo de terapia
    private void prepararSala(String tipoTerapia) {
        System.out.println("[AYUDANTE] Iniciando preparacion de sala...");

        switch (tipoTerapia) {
            case "Masaje Terapeutico":
                System.out.println("  -> Preparando camilla de masajes");
                System.out.println("  -> Calentando aceites terapeuticos");
                System.out.println("  -> Ajustando iluminacion tenue");
                break;
            case "Rehabilitacion Fisica":
                System.out.println("  -> Preparando equipo de ejercicios");
                System.out.println("  -> Verificando bandas elasticas y pesas");
                System.out.println("  -> Despejando area de ejercicio");
                break;
            case "Electroterapia":
                System.out.println("  -> Encendiendo equipo TENS");
                System.out.println("  -> Preparando ultrasonido terapeutico");
                System.out.println("  -> Verificando electrodos y gel conductor");
                break;
            case "Hidroterapia":
                System.out.println("  -> Verificando temperatura de la piscina (34-36C)");
                System.out.println("  -> Preparando flotadores y accesorios");
                System.out.println("  -> Comprobando niveles de cloro");
                break;
            case "Terapia Manual":
                System.out.println("  -> Preparando camilla articulada");
                System.out.println("  -> Disponiendo toallas limpias");
                System.out.println("  -> Verificando espacio para movilizaciones");
                break;
            case "Ejercicios Terapeuticos":
                System.out.println("  -> Preparando colchonetas");
                System.out.println("  -> Disponiendo pelotas de estabilidad");
                System.out.println("  -> Verificando espacio libre para ejercicios");
                break;
            default:
                System.out.println("  -> Preparando sala general");
                System.out.println("  -> Desinfectando superficies");
        }

        System.out.println("  -> Desinfectando area de trabajo");
        System.out.println("  -> Sala lista para el paciente");
    }

    // Solicitar datos medicos al Recepcionista
    private void solicitarDatosMedicos(String idCita) {
        try {
            SolicitarDatos solicitar = new SolicitarDatos();
            solicitar.setIdCita(idCita);

            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(new AID("Recepcionista", AID.ISLOCALNAME));
            msg.setLanguage(codec.getName());
            msg.setOntology(ontologia.getName());

            getContentManager().fillContent(msg, solicitar);
            send(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Notificar al Fisioterapeuta que los datos estan completos
    private void notificarFisioterapeuta(String idCita, Paciente paciente) {
        try {
            // Enviar mensaje al Fisioterapeuta indicando que los datos estan listos
            EnviarDatosMedicos enviar = new EnviarDatosMedicos();
            enviar.setIdCita(idCita);
            enviar.setPaciente(paciente);

            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(new AID("Fisioterapeuta", AID.ISLOCALNAME));
            msg.setLanguage(codec.getName());
            msg.setOntology(ontologia.getName());

            getContentManager().fillContent(msg, enviar);
            send(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("[AYUDANTE] Terminando servicio.");
    }
}

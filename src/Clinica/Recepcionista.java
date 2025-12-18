package Clinica;

import Clinica.ui.ClinicaClienteApp;
import Clinica.ui.GUIBridge;
import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import javafx.application.Platform;

/**
 * Agente Recepcionista - Se ejecuta en el cliente (Computadora B).
 * Maneja la interfaz grafica y la comunicacion con el servidor.
 */
public class Recepcionista extends Agent {

    private Codec codec = new SLCodec();
    private Ontology ontologia = ClinicaOntology.getInstance();
    private GUIBridge guiBridge;
    private Cita citaActual;
    private boolean esperandoRespuesta = false;

    @Override
    protected void setup() {
        // Registrar lenguaje y ontologia
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontologia);

        System.out.println("Recepcionista " + getAID().getName() + " iniciada.");

        // Obtener referencia al bridge
        guiBridge = GUIBridge.getInstance();

        // Configurar callbacks para eventos de la GUI
        configurarCallbacks();

        // Iniciar la interfaz JavaFX en un hilo separado
        new Thread(() -> {
            ClinicaClienteApp.launch(ClinicaClienteApp.class);
        }).start();

        // Esperar a que la GUI este lista
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Agregar comportamiento para recibir mensajes
        addBehaviour(new RecibirMensajesBehaviour());
    }

    private void configurarCallbacks() {
        // Callback cuando el usuario crea una cita desde la GUI
        guiBridge.setOnCitaCreada(cita -> {
            citaActual = cita;
            enviarSolicitudCita(cita);
        });

        // Callback cuando el usuario cancela una cita
        guiBridge.setOnCitaCancelada(idCita -> {
            cancelarCita(idCita);
        });
    }

    // Enviar solicitud de cita al Fisioterapeuta
    private void enviarSolicitudCita(Cita cita) {
        try {
            guiBridge.actualizarEstado("Enviando solicitud...");

            // Crear accion Agendar
            Agendar accion = new Agendar();
            accion.setCita(cita);

            // Crear mensaje ACL
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(new AID("Fisioterapeuta", AID.ISLOCALNAME));
            msg.setLanguage(codec.getName());
            msg.setOntology(ontologia.getName());

            // Llenar contenido
            getContentManager().fillContent(msg, accion);
            send(msg);

            esperandoRespuesta = true;
            System.out.println("[RECEPCIONISTA] Solicitud de cita enviada al Fisioterapeuta");

        } catch (Exception e) {
            e.printStackTrace();
            guiBridge.agregarMensajeSistema("Error al enviar la solicitud: " + e.getMessage());
            guiBridge.actualizarEstado("Error");
        }
    }

    // Enviar datos medicos al Ayudante
    private void enviarDatosMedicos(Paciente paciente, String idCita) {
        try {
            EnviarDatosMedicos accion = new EnviarDatosMedicos();
            accion.setPaciente(paciente);
            accion.setIdCita(idCita);

            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(new AID("Ayudante", AID.ISLOCALNAME));
            msg.setLanguage(codec.getName());
            msg.setOntology(ontologia.getName());

            getContentManager().fillContent(msg, accion);
            send(msg);

            System.out.println("[RECEPCIONISTA] Datos medicos enviados al Ayudante");

        } catch (Exception e) {
            e.printStackTrace();
            guiBridge.agregarMensajeSistema("Error al enviar datos medicos: " + e.getMessage());
        }
    }

    // Enviar sintomas al Fisioterapeuta
    private void enviarSintomasAlFisioterapeuta(Consulta consulta, String idCita) {
        try {
            EnviarSintomas accion = new EnviarSintomas();
            accion.setConsulta(consulta);
            accion.setIdCita(idCita);

            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(new AID("Fisioterapeuta", AID.ISLOCALNAME));
            msg.setLanguage(codec.getName());
            msg.setOntology(ontologia.getName());

            getContentManager().fillContent(msg, accion);
            send(msg);

            guiBridge.actualizarEstado("Esperando diagnostico...");
            System.out.println("[RECEPCIONISTA] Sintomas enviados al Fisioterapeuta");

        } catch (Exception e) {
            e.printStackTrace();
            guiBridge.agregarMensajeSistema("Error al enviar sintomas: " + e.getMessage());
        }
    }

    // Cancelar cita
    private void cancelarCita(String idCita) {
        try {
            guiBridge.actualizarEstado("Cancelando cita...");

            CancelarCita accion = new CancelarCita();
            accion.setIdCita(idCita);
            accion.setMotivo("Cancelado por el usuario");

            ACLMessage msg = new ACLMessage(ACLMessage.CANCEL);
            msg.addReceiver(new AID("Fisioterapeuta", AID.ISLOCALNAME));
            msg.setLanguage(codec.getName());
            msg.setOntology(ontologia.getName());

            getContentManager().fillContent(msg, accion);
            send(msg);

            System.out.println("[RECEPCIONISTA] Solicitud de cancelacion enviada");

        } catch (Exception e) {
            e.printStackTrace();
            guiBridge.agregarMensajeSistema("Error al cancelar: " + e.getMessage());
        }
    }

    /**
     * Comportamiento para recibir y procesar mensajes de otros agentes.
     */
    class RecibirMensajesBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.or(
                MessageTemplate.MatchLanguage(codec.getName()),
                MessageTemplate.MatchPerformative(ACLMessage.INFORM)
            );

            ACLMessage msg = receive(mt);

            if (msg != null) {
                try {
                    procesarMensaje(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Intentar procesar como mensaje simple
                    procesarMensajeSimple(msg);
                }
            } else {
                block();
            }
        }

        private void procesarMensaje(ACLMessage msg) throws Exception {
            String sender = msg.getSender().getLocalName();

            // Intentar extraer contenido estructurado
            if (msg.getLanguage() != null && msg.getLanguage().equals(codec.getName())) {
                ContentElement ce = getContentManager().extractContent(msg);

                if (ce instanceof ConfirmarCita) {
                    procesarConfirmacionCita((ConfirmarCita) ce, sender);
                } else if (ce instanceof SolicitarDatos) {
                    procesarSolicitudDatos((SolicitarDatos) ce, sender);
                } else if (ce instanceof ConsultarSintomas) {
                    procesarConsultaSintomas((ConsultarSintomas) ce, sender);
                } else if (ce instanceof EnviarDiagnostico) {
                    procesarDiagnostico((EnviarDiagnostico) ce, sender);
                } else {
                    // Mensaje no reconocido, mostrar contenido
                    procesarMensajeSimple(msg);
                }
            } else {
                procesarMensajeSimple(msg);
            }
        }

        private void procesarMensajeSimple(ACLMessage msg) {
            String sender = msg.getSender().getLocalName();
            String content = msg.getContent();

            if (content != null && !content.isEmpty()) {
                // Determinar el tipo de agente y usar el metodo apropiado
                if (sender.toLowerCase().contains("fisioterapeuta")) {
                    guiBridge.agregarMensajeFisioterapeuta(content);
                } else if (sender.toLowerCase().contains("ayudante")) {
                    guiBridge.agregarMensajeAyudante(content);
                } else {
                    guiBridge.agregarMensajeSistema(content);
                }
            }
        }

        private void procesarConfirmacionCita(ConfirmarCita confirmacion, String sender) {
            if (confirmacion.isConfirmada()) {
                guiBridge.agregarMensajeFisioterapeuta(
                    "Cita confirmada: " + confirmacion.getMensaje());
                guiBridge.actualizarEstado("Cita confirmada");

                // La cita fue aceptada, esperar solicitud de datos del Ayudante
                guiBridge.agregarMensajeSistema(
                    "Por favor espere mientras el ayudante prepara su expediente...");
            } else {
                guiBridge.agregarMensajeFisioterapeuta(
                    "Cita rechazada: " + confirmacion.getMensaje());
                guiBridge.actualizarEstado("Cita rechazada");
                guiBridge.reiniciarInterfaz();
            }
        }

        private void procesarSolicitudDatos(SolicitarDatos solicitud, String sender) {
            guiBridge.agregarMensajeAyudante(
                "Necesito recopilar algunos datos medicos antes de su consulta. " +
                "Por favor complete el siguiente formulario.");
            guiBridge.actualizarEstado("Completar datos medicos");

            // Mostrar formulario de datos medicos
            guiBridge.solicitarDatosMedicos(solicitud.getIdCita());

            // Esperar datos en un hilo separado para no bloquear
            new Thread(() -> {
                Paciente paciente = guiBridge.esperarDatosMedicos();
                if (paciente != null && citaActual != null) {
                    paciente.setNombre(citaActual.getNombrePaciente());
                    citaActual.setDatosPaciente(paciente);
                    enviarDatosMedicos(paciente, solicitud.getIdCita());
                }
            }).start();
        }

        private void procesarConsultaSintomas(ConsultarSintomas consulta, String sender) {
            guiBridge.agregarMensajeFisioterapeuta(
                "He revisado sus datos medicos. Ahora necesito conocer sus sintomas. " +
                "Por favor describa su molestia.");
            guiBridge.actualizarEstado("Describir sintomas");

            // Mostrar formulario de sintomas
            guiBridge.solicitarSintomas(consulta.getIdCita(), consulta.getPaciente());

            // Esperar sintomas en un hilo separado
            new Thread(() -> {
                Consulta consultaData = guiBridge.esperarSintomas();
                if (consultaData != null) {
                    citaActual.setConsulta(consultaData);
                    enviarSintomasAlFisioterapeuta(consultaData, consulta.getIdCita());
                }
            }).start();
        }

        private void procesarDiagnostico(EnviarDiagnostico diagnostico, String sender) {
            Consulta consulta = diagnostico.getConsulta();

            guiBridge.agregarMensajeFisioterapeuta(
                "He analizado sus sintomas. A continuacion le presento mi diagnostico y recomendaciones:");

            // Construir mensaje detallado
            StringBuilder detalles = new StringBuilder();
            detalles.append("Procedimiento: ").append(consulta.getProcedimiento()).append("\n");
            detalles.append("Sesiones recomendadas: ").append(consulta.getSesionesRecomendadas()).append("\n");
            if (consulta.getMedicamentos() != null && !consulta.getMedicamentos().isEmpty()) {
                detalles.append("Medicamentos: ").append(consulta.getMedicamentos()).append("\n");
            }
            if (consulta.getEjercicios() != null && !consulta.getEjercicios().isEmpty()) {
                detalles.append("Ejercicios: ").append(consulta.getEjercicios());
            }

            guiBridge.agregarMensajeFisioterapeuta(detalles.toString());
            guiBridge.actualizarEstado("Consulta completada");

            // Mostrar panel de diagnostico
            guiBridge.mostrarDiagnostico(consulta);

            // Actualizar cita
            if (citaActual != null) {
                citaActual.setConsulta(consulta);
                citaActual.setEstado(Cita.ESTADO_COMPLETADA);
            }
        }

    }

    @Override
    protected void takeDown() {
        System.out.println("Recepcionista " + getAID().getName() + " terminando.");
        Platform.exit();
    }
}

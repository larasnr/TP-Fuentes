package ar.edu.utn.dds.k3003.model.mensajeria;

import ar.edu.utn.dds.k3003.app.Fachada;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MensajeriaManager {

    private final MensajeriaBroker broker;
    private final Fachada fachada;
    private final String topic;

    private volatile String consumerTag; // null = desactivado

    public MensajeriaManager(MensajeriaBroker broker,
                             Fachada fachada,
                             @Value("${mensajeria.topic:fuentes.hechos.nuevos}") String topic) {
        this.broker = broker;
        this.fachada = fachada;
        this.topic = topic;
    }

    /** ¿Hay AMQP real (no NoOp)? */
    public boolean isConfigurado() {
        return !(broker instanceof NoOpMensajeriaBroker);
    }

    public boolean isActivo() { return consumerTag != null; }
    public String getTopic() { return topic; }

    public synchronized void activar() {
        if (isActivo()) return;
        if (!isConfigurado()) throw new IllegalStateException("Mensajería no configurada");
        try {
            String tag = broker.suscribir(topic, payload -> {
                if (payload == null || payload.length == 0) {
                    System.out.println("[AMQP] Mensaje vacío, se ignora");
                    return;
                }
                fachada.onMessage(MensajeHecho.fromBytes(payload));
            });
            if (tag == null || tag.isBlank()) {
                throw new IllegalStateException("El broker no devolvió consumerTag");
            }
            this.consumerTag = tag;
            System.out.println("[AMQP] Activado con tag=" + consumerTag);
        } catch (Exception e) {
            this.consumerTag = null;
            throw new RuntimeException("No se pudo suscribir a " + topic, e);
        }
    }


    public synchronized void desactivar() {
        if (!isActivo()) return;
        try {
            broker.cancelar(consumerTag);
            System.out.println("[AMQP] Desuscripto tag=" + consumerTag);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cancelar suscripción " + consumerTag, e);
        } finally {
            consumerTag = null;
        }
    }

    /** Para publicar desde tu endpoint si querés usar el broker */
    public void publicar(MensajeHecho m) {
        if (!isConfigurado()) throw new IllegalStateException("Mensajería no configurada");
        try { broker.publicar(topic, m.toBytes()); }
        catch (Exception e) { throw new RuntimeException("No se pudo publicar", e); }
    }
}

package ar.edu.utn.dds.k3003.model.mensajeria;

import ar.edu.utn.dds.k3003.app.Fachada;

public class MensajeriaCanal {
    private final MensajeriaBroker bus;
    private final String topic;
    private final Fachada fuente;

    public MensajeriaCanal(MensajeriaBroker bus, String topic, Fachada fuente) {
        this.bus = bus; this.topic = topic; this.fuente = fuente;
    }

    public void suscribir() {
        try {
            bus.suscribir(topic, payload -> fuente.onMessage(MensajeHecho.fromBytes(payload)));
        } catch (Exception e) {
            throw new RuntimeException("No se pudo suscribir al topic " + topic, e);
        }
    }

    public void publicar(MensajeHecho msg) {
        try { bus.publicar(topic, msg.toBytes()); }
        catch (Exception e) { throw new RuntimeException("No se pudo publicar", e); }
    }
}

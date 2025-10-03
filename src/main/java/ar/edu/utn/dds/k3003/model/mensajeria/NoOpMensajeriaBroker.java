package ar.edu.utn.dds.k3003.model.mensajeria;

public class NoOpMensajeriaBroker implements MensajeriaBroker {
    @Override public void publicar(String topic, byte[] payload) {}
    @Override public String suscribir(String topic, MensajeriaHandler handler) {return null;}
    @Override public void cancelar(String consumerTag) {}
    @Override public String toString() { return "NoOpMensajeriaBroker"; }
}
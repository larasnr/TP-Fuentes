package ar.edu.utn.dds.k3003.model.mensajeria;

public interface  MensajeriaBroker {
    void publicar(String topic, byte[] payload) throws Exception;
    void suscribir(String topic, MensajeriaHandler handler) throws Exception;
}

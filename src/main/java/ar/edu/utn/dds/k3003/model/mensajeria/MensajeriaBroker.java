package ar.edu.utn.dds.k3003.model.mensajeria;

public interface  MensajeriaBroker {
    void publicar(String topic, byte[] payload) throws Exception;
    String suscribir(String topic, MensajeriaHandler handler) throws Exception;
    void cancelar(String consumerTag) throws Exception;
}

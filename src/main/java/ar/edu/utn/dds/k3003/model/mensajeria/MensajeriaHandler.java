package ar.edu.utn.dds.k3003.model.mensajeria;

public interface MensajeriaHandler {
    void handle(byte[] body) throws Exception;
}

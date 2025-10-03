package ar.edu.utn.dds.k3003.model.mensajeria;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Objects;

public class RabbitMqAdapter implements MensajeriaBroker {

    private final String uri;
    private final String exchangeName;
    private final String configuredQueue;

    private Connection connection;
    private Channel channel;

    public RabbitMqAdapter(String uri, String exchangeName) {
        this(uri, exchangeName, null);
    }

    public RabbitMqAdapter(String uri, String exchangeName, String queueName) {
        this.uri = Objects.requireNonNull(uri, "uri no puede ser null");
        this.exchangeName = Objects.requireNonNull(exchangeName, "exchange no puede ser null");
        this.configuredQueue = queueName; // puede ser null
    }

    private synchronized void ensureChannel() throws Exception {
        if (channel != null && channel.isOpen()) return;

        ConnectionFactory cf = new ConnectionFactory();
        cf.setUri(uri);
        cf.setAutomaticRecoveryEnabled(true);
        cf.setNetworkRecoveryInterval(5000);
        connection = cf.newConnection("fuentes-app");
        channel = connection.createChannel();

        try {
            channel.exchangeDeclarePassive(exchangeName);
            System.out.println("[AMQP] Exchange ya existe: " + exchangeName);
        } catch (IOException notFoundOrMismatch) {
            //Si NO existe, se declara como topic durable (sin args extra)
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC, true, false, null);
            System.out.println("[AMQP] Exchange declarado: " + exchangeName + " (topic, durable)");
        }
    }

    @Override
    public void publicar(String topic, byte[] payload) throws Exception {
        ensureChannel();
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .contentType("application/json")
                .deliveryMode(2) // persistente
                .build();
        channel.basicPublish(exchangeName, topic, props, payload);
        System.out.println("[AMQP] Publish exchange=" + exchangeName + " rk=" + topic +
                " bytes=" + (payload == null ? 0 : payload.length));
    }

    @Override
    public String suscribir(String topic, MensajeriaHandler handler) throws Exception {
        ensureChannel();

        // Si hay una cola configurada por properties, usarla. Si no, topic+".q"
        final String queue = (configuredQueue != null && !configuredQueue.isBlank())
                ? configuredQueue
                : topic + ".q";

        channel.queueDeclare(queue, true, false, false, null);
        channel.queueBind(queue, exchangeName, topic);
        System.out.println("[AMQP] Bind queue=" + queue + " -> " + exchangeName + " rk=" + topic);

        DeliverCallback deliver = (consumerTag, delivery) -> {
            try {
                byte[] body = delivery.getBody();
                if (body == null || body.length == 0) {
                    System.out.println("[AMQP] Mensaje vacío (tag=" + consumerTag + "), se ignora");
                    return;
                }
                handler.handle(body);
            } catch (Exception ex) {
                System.err.println("[AMQP] Error manejando mensaje: " + ex.getMessage());
                ex.printStackTrace();
            }
        };

        CancelCallback cancel = consumerTag ->
                System.out.println("[AMQP] cancel consumerTag=" + consumerTag + " queue=" + queue);

        String consumerTag = channel.basicConsume(queue, true, deliver, cancel);

        System.out.println("[AMQP] Consumiendo queue=" + queue);
        System.out.println("[AMQP] Suscripto topic=" + topic + " tag=" + consumerTag);

        return consumerTag; //así el MensajeriaManager marca como activa a la suscripción
    }

    @Override
    public void cancelar(String consumerTag) throws Exception {
        if (channel != null && channel.isOpen() && consumerTag != null && !consumerTag.isBlank()) {
            channel.basicCancel(consumerTag);
            System.out.println("[AMQP] Cancelado consumerTag=" + consumerTag);
        }
    }
}

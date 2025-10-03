package ar.edu.utn.dds.k3003.model.mensajeria;

import com.rabbitmq.client.*;

import java.io.IOException;

public class RabbitMqAdapter implements MensajeriaBroker {

    private final String uri;
    private final String exchangeName;

    private Connection connection;
    private Channel channel;

    public RabbitMqAdapter(String uri, String exchangeName) {
        this.uri = uri;
        this.exchangeName = exchangeName;
    }

    private void ensureChannel() throws Exception {
        if (channel != null && channel.isOpen()) return;

        ConnectionFactory cf = new ConnectionFactory();
        cf.setUri(uri);
        cf.setAutomaticRecoveryEnabled(true);
        cf.setNetworkRecoveryInterval(5000);
        connection = cf.newConnection("fuentes-app");
        channel = connection.createChannel();

        try {
            // Verifica si el exchange ya existe con mismos args
            channel.exchangeDeclarePassive(exchangeName);
            System.out.println("[AMQP] Exchange ya existe: " + exchangeName);
        } catch (IOException notFoundOrMismatch) {
            //Si NO existe, lo declaro como topic durable (sin args extra)
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC, true, false, null);
            System.out.println("[AMQP] Exchange declarado: " + exchangeName + " (topic, durable)");
        }
    }

    @Override
    public void publicar(String topic, byte[] payload) throws Exception {
        ensureChannel();
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .contentType("application/json")
                .deliveryMode(2)
                .build();
        channel.basicPublish(exchangeName, topic, props, payload);
        System.out.println("[AMQP] Publish rk=" + topic + " bytes=" + payload.length);
    }

    @Override
    public void suscribir(String topic, MensajeriaHandler handler) throws Exception {
        ensureChannel();

        String queue = topic + ".q";
        channel.queueDeclare(queue, true, false, false, null);
        channel.queueBind(queue, exchangeName, topic);
        System.out.println("[AMQP] Bind queue=" + queue + " -> " + exchangeName + " rk=" + topic);

        channel.basicQos(10);
        channel.basicConsume(queue, true,
                (tag, delivery) -> {
                    try { handler.handle(delivery.getBody()); }
                    catch (Exception e) { e.printStackTrace(); }
                },
                tag -> {});
        System.out.println("[AMQP] Consumiendo queue=" + queue);
    }
}

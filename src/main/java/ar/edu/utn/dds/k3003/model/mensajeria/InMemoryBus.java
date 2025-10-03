package ar.edu.utn.dds.k3003.model.mensajeria;

import java.util.*;
import java.util.concurrent.*;

public class InMemoryBus implements MensajeriaBroker {
    private final Map<String, List<MensajeriaHandler>> subs = new ConcurrentHashMap<>();

    public void publicar(String topic, byte[] payload) {
        subs.getOrDefault(topic, List.of()).forEach(h -> {
            try { h.handle(payload); } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public void suscribir(String topic, MensajeriaHandler handler) {
        subs.computeIfAbsent(topic, t -> new CopyOnWriteArrayList<>()).add(handler);
    }
}


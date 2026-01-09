package com.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseEmitterService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(String ownerId) {
        SseEmitter emitter = new SseEmitter(0L); // no timeout
        emitters.put(ownerId, emitter);
        emitter.onCompletion(() -> emitters.remove(ownerId));
        emitter.onTimeout(() -> emitters.remove(ownerId));
        return emitter;
    }

    public void sendEvent(String ownerId, Object data) {
        SseEmitter emitter = emitters.get(ownerId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("menuProcessed").data(data));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            } finally {
                emitters.remove(ownerId);
            }
        }
    }
}
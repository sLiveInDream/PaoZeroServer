package com.paozero.game.logic.entity;

import com.paozero.game.logic.component.AbstractActorComponent;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ActorEntity {
    private String entityId;
    private Map<Class<?>, AbstractActorComponent> componentMap = new HashMap<>();


    public <T> T addComponent(Class<T> componentClazz) {
        AbstractActorComponent component = componentMap.get(componentClazz);
        if (component != null) {
            log.warn("Entity:{}, addComponent , component already exist:{}",
                    entityId, componentClazz.getName());
            return null;
        }

        try {
            component = (AbstractActorComponent) componentClazz.newInstance();
        } catch (Exception e) {
            log.error("Entity:{}, addComponent exception:{}, stackTrace:{}",
                    entityId, e.getMessage(), e.getStackTrace());
            return null;
        }

        componentMap.put(componentClazz, component);
        return (T) component;
    }

    public <T> T getComponentData(Class<T> componentClazz) {
        AbstractActorComponent component = componentMap.get(componentClazz);
        if (!component.isActive()) {
            return null;
        }

        return (T) component;
    }

    public boolean removeComponent(Class<?> componentClazz) {
        AbstractActorComponent component = componentMap.get(componentClazz);
        if (component == null) {
            return false;
        }
        component.setActive(false);
        return true;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityId() {
        return  entityId;
    }
}

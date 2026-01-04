package lenicorp.admin.workflowengine.engine.adapter;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SpringObjectAdapterRegistry implements ObjectAdapterRegistry {
    private final Map<Class<?>, ObjectAdapter<?>> byType = new ConcurrentHashMap<>();

    public SpringObjectAdapterRegistry(List<ObjectAdapter<?>> adapters) {
        if (adapters != null) {
            for (ObjectAdapter<?> a : adapters) {
                byType.put(a.targetType(), a);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ObjectAdapter<T> adapterFor(Class<T> type) {
        ObjectAdapter<?> found = byType.get(type);
        if (found != null) return (ObjectAdapter<T>) found;
        for (var e : byType.entrySet()) {
            if (e.getKey().isAssignableFrom(type)) {
                return (ObjectAdapter<T>) e.getValue();
            }
        }
        throw new IllegalArgumentException("No ObjectAdapter registered for type " + type.getName());
    }

    @Override
    public ObjectAdapter<?> adapterFor(Object instance) {
        if (instance == null) throw new IllegalArgumentException("instance is null");
        return adapterFor(instance.getClass());
    }

    @Override
    public ObjectAdapter<?> adapterFor(String targetTypeCode) {
        if (targetTypeCode == null) throw new IllegalArgumentException("targetTypeCode is null");
        for (ObjectAdapter<?> adapter : byType.values()) {
            if (targetTypeCode.equalsIgnoreCase(adapter.targetType().getSimpleName())) {
                return adapter;
            }
        }
        throw new IllegalArgumentException("No ObjectAdapter registered for target type code " + targetTypeCode);
    }

    @Override
    public List<String> getAvailableTargetTypes() {
        return byType.keySet().stream()
                .map(Class::getSimpleName)
                .toList();
    }
}

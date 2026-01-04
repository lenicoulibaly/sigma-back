package lenicorp.admin.workflowengine.engine.adapter;

public interface ObjectAdapterRegistry {
    <T> ObjectAdapter<T> adapterFor(Class<T> type);
    ObjectAdapter<?> adapterFor(Object instance);
    ObjectAdapter<?> adapterFor(String targetTypeCode);
    java.util.List<String> getAvailableTargetTypes();
}

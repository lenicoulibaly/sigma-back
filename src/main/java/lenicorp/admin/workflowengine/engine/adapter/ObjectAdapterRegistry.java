package lenicorp.admin.workflowengine.engine.adapter;

public interface ObjectAdapterRegistry {
    <T> ObjectAdapter<T> adapterFor(Class<T> type);
    ObjectAdapter<?> adapterFor(Object instance);
}

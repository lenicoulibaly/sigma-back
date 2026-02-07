package lenicorp.admin.workflowengine.engine.adapter;

import lenicorp.admin.workflowengine.model.dtos.InfoFieldDTO;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface ObjectAdapter<T> {
    Class<T> targetType();

    String getCurrentStatus(T obj);

    void setStatus(T obj, String newStatus);

    Map<String, Object> toRuleMap(T obj);

    T load(String id);

    String getId(T obj);

    default void save(T obj) { /* optional no-op */ }

    default boolean supports(Object o) { return o != null && targetType().isInstance(o); }

    default List<InfoFieldDTO> getGeneralInfo(T obj) {
        return Collections.emptyList();
    }
}

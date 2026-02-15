package lenicorp.admin.workflowengine.outbox.exec;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ExpressionResolverTest {

    private final ExpressionResolver resolver = new ExpressionResolver();

    @Test
    void shouldResolveSimplePath() {
        Map<String, Object> vars = Map.of(
            "facts", Map.of("nomDemandeur", "John Doe")
        );
        // Using SpEL syntax (#facts['...'])
        Object result = resolver.eval("${#facts['nomDemandeur']}", vars);
        assertEquals("John Doe", result);
        
        // Using backward compatibility dotted path (facts.nomDemandeur)
        result = resolver.eval("${facts.nomDemandeur}", vars);
        assertEquals("John Doe", result);
    }

    @Test
    void shouldResolveTernaryExpression() {
        Map<String, Object> vars = Map.of(
            "event", Map.of("toStatus", "ATTENTE_PAIE_DRT_ADH")
        );
        String expr = "${#event['toStatus'] == 'ATTENTE_PAIE_DRT_ADH' ? 'path/to/template1' : 'path/to/template2'}";
        Object result = resolver.eval(expr, vars);
        assertEquals("path/to/template1", result);

        vars = Map.of(
            "event", Map.of("toStatus", "VALIDE")
        );
        result = resolver.eval(expr, vars);
        assertEquals("path/to/template2", result);
    }

    @Test
    void shouldResolveMultiplePlaceholders() {
        Map<String, Object> vars = Map.of(
            "facts", Map.of("firstName", "John", "lastName", "Doe")
        );
        Object result = resolver.eval("Hello ${#facts['firstName']} ${#facts['lastName']}!", vars);
        assertEquals("Hello John Doe!", result);
    }

    @Test
    void shouldReturnOriginalIfNotString() {
        Object result = resolver.eval(123, Map.of());
        assertEquals(123, result);
    }

    @Test
    void shouldReturnOriginalIfNoPlaceholder() {
        Object result = resolver.eval("No placeholder", Map.of());
        assertEquals("No placeholder", result);
    }

    @Test
    void shouldHandleNull() {
        assertNull(resolver.eval(null, Map.of()));
    }
}

package ebi.monarch.eqwriter;

import org.semanticweb.owlapi.model.OWLClassExpression;

public interface ClassExpressionToStringTransformer {
    String createDefinition(OWLClassExpression ce);
}

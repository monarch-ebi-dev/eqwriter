package ebi.monarch.eqwriter;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;

import java.util.HashSet;
import java.util.Set;

public interface ClassExpressionToStringTransformer extends Transformer {
    String createDefinition(OWLClassExpression ce);


    default Set<OWLAnnotationAssertionAxiom> rewriteDefinitions(OWLEntity c)
    {
        return new HashSet<>();
    }
}

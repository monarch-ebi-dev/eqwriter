package ebi.monarch.eqwriter;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;

import java.util.Set;

public interface Transformer {
    String createDefinition(OWLClassExpression ce);
    Set<OWLAnnotationAssertionAxiom> rewriteDefinitions(OWLEntity c);
}

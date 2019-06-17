package ebi.monarch.eqwriter;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public class Entities {
    private static final OWLDataFactory df = OWLManager.getOWLDataFactory();
    public static final OWLObjectProperty op_inheresinpartof = df.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/RO_0002314"));
    public static final OWLObjectProperty op_inheresin = df.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/RO_0000052"));

    public static final OWLObjectProperty op_partof = df.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/BFO_0000050"));
    public static final OWLObjectProperty op_towards = df.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/RO_0002503"));
    public static final OWLObjectProperty op_has_modifier = df.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/RO_0002573"));
    public static final OWLObjectProperty op_haspart = df.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/BFO_0000051"));

    public static final OWLClass c_quality=df.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/PATO_0000001"));
    public static final OWLClass upheno = df.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/UPHENO_0001002"));

}

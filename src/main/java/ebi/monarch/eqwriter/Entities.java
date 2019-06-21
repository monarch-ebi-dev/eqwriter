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

    public static final OWLObjectProperty has_function_in = df.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/fbbt#has_function_in"));
    public static final OWLObjectProperty releases_neurotransmitter = df.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/fbbt#releases_neurotransmitter"));
    //public static final OWLObjectProperty dendrite_innervates = df.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/BFO_0000051"));
    //public static final OWLObjectProperty axon_innervates = df.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/BFO_0000051"));
    public static final OWLObjectProperty innervates = df.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/RO_0002134"));
    public static final OWLObjectProperty electrically_synapsed_to = df.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/RO_0002003"));
    public static final OWLObjectProperty develops_from = df.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/RO_0002202"));
    public static final OWLObjectProperty develops_directly_from = df.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/RO_0002207"));
    public static final OWLObjectProperty connected_to = df.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/RO_0002150"));
    public static final OWLObjectProperty fasciculates_with = df.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/RO_0002101"));

    public static final OWLObjectProperty expresses = df.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/RO_0002292"));


}

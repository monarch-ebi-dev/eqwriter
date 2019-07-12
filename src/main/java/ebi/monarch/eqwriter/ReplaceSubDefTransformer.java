package ebi.monarch.eqwriter;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplaceSubDefTransformer implements DefinitionRewriterTransformer  {

    private OWLDataFactory df = OWLManager.getOWLDataFactory();
    private RenderManager ren = RenderManager.getInstance();
    private boolean addSourceXref;
    private OWLOntology o;

    ReplaceSubDefTransformer(Map<OWLEntity, String> newLabels, OWLOntology o, boolean addSourceXref) {
        ren.updateLabels(newLabels);
        this.o = o;
        this.addSourceXref = addSourceXref;
    }

    @Override
    public Set<OWLAnnotationAssertionAxiom> rewriteDefinitions(OWLEntity c) {
        Set<OWLAnnotationAssertionAxiom> axs=  new HashSet<>();
        for (OWLAnnotationAssertionAxiom axiom : o.getAnnotationAssertionAxioms(c.getIRI())) {
            if(axiom.getProperty().equals(Entities.ap_definition)) {
                if (axiom.getValue().asLiteral().isPresent()) {
                    String olddef = axiom.getValue().asLiteral().get().getLiteral();
                    Set<OWLAnnotation> axiomAnnotations = new HashSet<>(axiom.getAnnotations());
                    //System.out.println(olddef);
                    try {
                        OWLClass e = OntologyUtils.extractClassFromSubPattern(olddef);
                        for(OWLAnnotationAssertionAxiom dbxref_entity : o.getAnnotationAssertionAxioms(e.getIRI())) {
                            if(dbxref_entity.getProperty().equals(Entities.ap_definition)) {
                                OWLAnnotationValue value = dbxref_entity.getValue();
                                if (value instanceof OWLLiteral) {
                                    String val = ((OWLLiteral) value).getLiteral();
                                    String def = olddef.replaceAll("\\$sub[_][a-zA-Z]+[:][0-9]+", val);
                                    if(!def.endsWith(".") && !def.endsWith(".'") &&  !def.endsWith(".\"")) {
                                        def = def +".";
                                    }
                                    if(addSourceXref) {
                                        axiomAnnotations.addAll(dbxref_entity.getAnnotations(Entities.ap_dbxref));
                                    }
                                    OWLAnnotationAssertionAxiom ax_out = df.getOWLAnnotationAssertionAxiom(Entities.ap_definition, c.getIRI(), df.getOWLLiteral(def), axiomAnnotations);
                                    axs.add(ax_out);
                                    break; // We only ever want 1 replacement definitions!
                                }

                            }
                        }

                    } catch(IllegalArgumentException e) {
                        System.err.println(olddef+ " did not contain valid sub pattern, ignoring..");
                    }
                }
            }
        }

        return axs;
    }

}

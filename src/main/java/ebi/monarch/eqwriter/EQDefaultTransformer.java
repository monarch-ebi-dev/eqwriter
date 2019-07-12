package ebi.monarch.eqwriter;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EQDefaultTransformer implements ClassExpressionToStringTransformer {

    OWLDataFactory df = OWLManager.getOWLDataFactory();
    RenderManager ren = RenderManager.getInstance();

    EQDefaultTransformer(Map<OWLEntity, String> newLabels, OWLOntology o) {
        ren.updateLabels(newLabels);
    }

    @Override
    public String createDefinition(OWLClassExpression ce) {
        if (ce instanceof OWLObjectSomeValuesFrom) {
            OWLObjectSomeValuesFrom cephen = ((OWLObjectSomeValuesFrom) ce);
            if (!cephen.getProperty().isAnonymous()) {
                if (cephen.getProperty().asOWLObjectProperty().equals(Entities.op_haspart)) {
                    if (cephen.getFiller() instanceof OWLObjectIntersectionOf)
                        return createExpression((OWLObjectIntersectionOf)cephen.getFiller());
                }
            }
        }
        String def = ren.renderHumanReadable(ce);
        if(!def.endsWith(".") && !def.endsWith(".'") &&  !def.endsWith(".\"")) {
            def = def +".";
        }
        return def;
    }


    /*
    CURRENTLY DEPRECTAED
     */
    private String createExpression(OWLObjectIntersectionOf cephen) {

        String bearer = null;
        String modifier = "";
        String relational = null;
        String quality = null;

        for (OWLClassExpression ce : cephen.getOperands()) {
            if (ce instanceof OWLObjectSomeValuesFrom) {
                OWLObjectSomeValuesFrom ois = (OWLObjectSomeValuesFrom) ce;
                if (!ois.getProperty().isAnonymous()) {
                    System.out.println("A");
                    OWLObjectProperty op_in = ois.getProperty().asOWLObjectProperty();
                    if (op_in.equals(Entities.op_inheresin)||op_in.equals(Entities.op_inheresinpartof)) {
                        bearer = formatBearer(ois.getFiller());
                    } else if (op_in.equals(Entities.op_towards)) {
                        relational = formatBearer(ois.getFiller());
                    } else if (op_in.equals(Entities.op_has_modifier)) {
                        modifier = ren.renderHumanReadable(ois.getFiller());
                    }
                }
            } else if(ce.isClassExpressionLiteral()) {
                if(ce.asOWLClass().getIRI().toString().startsWith("http://purl.obolibrary.org/obo/PATO_")) {
                    quality = ren.getLabel(ce.asOWLClass());
                }
            }
        }
        if(bearer!=null && quality!=null) {
            String rs = "The " + bearer;
            if(relational==null) {
                if(quality.endsWith("ed")||quality.endsWith("ic")) {
                    rs += " is "+quality;
                    if(!modifier.isEmpty()) {
                        rs += " (" + modifier + ")";
                    }
                } else {
                    rs += " has a";
                    if (modifier.startsWith("a") || modifier.startsWith("e") || modifier.startsWith("i") || modifier.startsWith("o") || modifier.startsWith("u")) {
                        rs += "n";
                    }
                    rs += " " + modifier + " " + quality;
                }
            } else {
                rs += " is "+quality +" "+ relational;
                if(!modifier.isEmpty()) {
                    rs += " (" + modifier + ")";
                }
            }
            return rs;
        }
        return ren.renderHumanReadable(cephen);
    }

    private String formatBearer(OWLClassExpression filler) {
        System.out.println("B");
        if(true) {
            return ren.renderHumanReadable(filler);
        }
        if (filler instanceof OWLObjectSomeValuesFrom) {
            System.out.println("C");
            OWLObjectSomeValuesFrom poexp = (OWLObjectSomeValuesFrom) filler;
            if (!poexp.getProperty().isAnonymous()) {
                System.out.println("D");
                OWLObjectProperty po = poexp.getProperty().asOWLObjectProperty();
                if (po.equals(Entities.op_partof)) {
                    System.out.println("E");
                }
            }

        } else if (filler instanceof OWLObjectIntersectionOf) {
            OWLObjectIntersectionOf oio = (OWLObjectIntersectionOf)filler;
            Set<OWLClassExpression> inheresIntersection = new HashSet<>();
            for(OWLClassExpression ceoio:oio.getOperands()) {
                if(ceoio instanceof OWLObjectSomeValuesFrom) {
                    System.out.println("C");
                    OWLObjectSomeValuesFrom poexp = (OWLObjectSomeValuesFrom) filler;
                    if (!poexp.getProperty().isAnonymous()) {
                        System.out.println("D");
                        OWLObjectProperty po = poexp.getProperty().asOWLObjectProperty();
                        if (po.equals(Entities.op_partof)) {
                            System.out.println("E");
                        } else {
                            inheresIntersection.add(ceoio);
                        }
                    } else {
                        inheresIntersection.add(ceoio);
                    }
                } else {
                    inheresIntersection.add(ceoio);
                }
            }
        }
        return ren.renderHumanReadable(filler);
    }


}

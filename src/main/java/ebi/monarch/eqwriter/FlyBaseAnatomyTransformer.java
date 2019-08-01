package ebi.monarch.eqwriter;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class FlyBaseAnatomyTransformer implements ClassExpressionToStringTransformer {

    OWLDataFactory df = OWLManager.getOWLDataFactory();
    RenderManager ren = RenderManager.getInstance();

    FlyBaseAnatomyTransformer(Map<OWLEntity, String> labels, OWLOntology o) {
        prepareLabels(labels);
    }

    @Override
    public String createDefinition(OWLClassExpression ce) {
        String def = ren.renderHumanReadable(ce);
        //System.out.println(def);
        def = def.replaceFirst(" and "," that ");

        /*
        This complicated hack is trying to avoid the problem that sometimes an expression contains a term whose label is contained
        in another term; causing the CURIE to appear in the middle of some other, potentially unrelated label.
         */
        List<String> labels = new ArrayList<>();
        Map<String,OWLClass> mapLabelClass = new HashMap<>();
        Map<String,String> mapStringEncoded = new HashMap<>();
        ce.getClassesInSignature().forEach(c->{labels.add(ren.getLabel(c));mapLabelClass.put(ren.getLabel(c),c);});
        labels.sort((s1, s2) -> s1.length() - s2.length());
        Collections.reverse(labels);

        for(String clabel:labels) {
            OWLEntity c = mapLabelClass.get(clabel);
            String replacement = clabel+" ("+c.getIRI().getShortForm().replaceAll("_",":")+")";
            String id = c.getIRI().toString();
            mapStringEncoded.put(id,replacement);
            def = def.replace(clabel,id);
        }

        for(String id:mapStringEncoded.keySet()) {
            def = def.replace(id,mapStringEncoded.get(id));
        }
        def = ren.dropSomeFromDefinition(def,ce.getObjectPropertiesInSignature());
        def = def.replaceAll("biological_process","biological process").replaceAll("capable_of","capable of");
        if(!def.endsWith(".") && !def.endsWith(".'") &&  !def.endsWith(".\"")) {
            def = def +".";
        }

        return "Any "+def;
    }

    private void prepareLabels(Map<OWLEntity, String> labels) {
        ren.updateLabel(Entities.has_function_in,"functions in");
        //ren.updateLabel(Entities.dendrite_innervates,"has a dendrite that innervates some");
        //ren.updateLabel(Entities.axon_innervates,"has an axon that innervates some");
        ren.updateLabel(Entities.innervates,"innervates");
        ren.updateLabel(Entities.op_partof,"is part of");
        ren.updateLabel(Entities.electrically_synapsed_to,"electrically synapses to");
        ren.updateLabel(Entities.develops_from,"develops from");
        ren.updateLabel(Entities.develops_directly_from,"develops directly from");
        ren.updateLabel(Entities.connected_to,"is connected to");
        ren.updateLabel(Entities.fasciculates_with,"fasciculates with");
        ren.updateLabel(Entities.releases_neurotransmitter,"releases as a neurotransmitter");
        ren.updateLabels(labels);
        ren.dropSomeForEntity(Entities.expresses);
    }
}

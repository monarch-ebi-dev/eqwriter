package ebi.monarch.eqwriter;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.Map;
import java.util.Set;

public class FlyBaseAnatomyTransformer implements ClassExpressionToStringTransformer {

    OWLDataFactory df = OWLManager.getOWLDataFactory();
    RenderManager ren = RenderManager.getInstance();

    FlyBaseAnatomyTransformer(Map<OWLEntity,String> labels) {
        prepareLabels(labels);
    }

    @Override
    public String createDefinition(OWLClassExpression ce) {
        String def = ren.renderHumanReadable(ce);
        def = def.replaceFirst(" and "," that ");
        for(OWLClass c:ce.getClassesInSignature()) {
            String clabel = ren.getLabel(c);
            def = def.replace(clabel,clabel+" ("+c.getIRI().getShortForm().replaceAll("_",":")+")");
        }
        def = ren.dropSomeFromDefinition(def,ce.getObjectPropertiesInSignature());
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

package ebi.monarch.eqwriter;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultTransformer implements ClassExpressionToStringTransformer {

    RenderManager ren = RenderManager.getInstance();

    DefaultTransformer(Map<OWLEntity, String> newLabels) {
        ren.updateLabels(newLabels);
    }


    @Override
    public String createDefinition(OWLClassExpression ce) {
        return ren.renderHumanReadable(ce);
    }
}

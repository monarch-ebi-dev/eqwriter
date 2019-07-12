package ebi.monarch.eqwriter;

import org.semanticweb.owlapi.model.*;

import java.util.HashSet;
import java.util.Set;

public interface DefinitionRewriterTransformer extends Transformer {
    default String createDefinition(OWLClassExpression ce) {
        String def = RenderManager.getInstance().renderHumanReadable(ce);
        if(!def.endsWith(".") && !def.endsWith(".'") &&  !def.endsWith(".\"")) {
            def = def +".";
        }
        return def;
    }
}

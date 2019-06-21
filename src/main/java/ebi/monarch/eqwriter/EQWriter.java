package ebi.monarch.eqwriter;

import org.apache.commons.io.FileUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EQWriter {

    OWLOntologyManager man = OWLManager.createOWLOntologyManager();
    OWLDataFactory df = man.getOWLDataFactory();
    Map<OWLEntity,String> newLabels = new HashMap<>();
    OWLOntology o;

    public EQWriter(String ontology_in, File label_mappings) {
        try {
            o = man.loadOntologyFromOntologyDocument(new File(ontology_in));
            RenderManager.getInstance().addLabel(o);
            int i = 0;

            if(label_mappings.exists()) {
                List<String> mappings = FileUtils.readLines(label_mappings,"utf-8");
                for(String s:mappings) {
                    if(s.trim().startsWith("http") && s.chars().filter(ch -> ch == ',').count() >= 1) {
                        String[] vals = s.split(",");
                        String iri = vals[0].trim();
                        String label = vals[1].trim();
                        for(OWLEntity e:o.getSignature()) {
                            if(e.getIRI().toString().equals(iri)) {
                                newLabels.put(e,label);
                                if(vals.length>2) {
                                    if(e instanceof OWLObjectProperty) {
                                        boolean drop_some = vals[2].trim().equals("drop-some");
                                        if (drop_some) {
                                            RenderManager.getInstance().dropSomeForEntity((OWLObjectProperty)e);
                                        }
                                    }
                                }
                                i++;
                            }
                        }

                    }
                }
            }
            System.out.println("EQ Writer created, "+i+" custom labels updated.");
        } catch (OWLOntologyCreationException e) {
            throw new IllegalStateException("Ontology could not be loaded!",e);
        } catch (IOException e) {
            throw new IllegalStateException("Mappings file cannot be parsed!",e);
        }
    }

    public static void main(String[] args) throws IOException {

        args = new String[5];
        args[0] = "/data/fbbt.owl";
        args[1] = "/ws/drosophila-anatomy-developmental-ontology/src/ontology/auto_defined_classes.txt";
        args[2] = "flybase";
        args[3] = "/data/test_template.tsv";
        args[4] = "/data/label_mappingss.txt";

        String ontology_in = args[0];
        File entities_list = new File(args[1]);
        String style = args[2];
        File robot_template = new File(args[3]);
        File label_mappings = new File(args[4]);

        EQWriter eq = new EQWriter(ontology_in, label_mappings);
        eq.rewrite(entities_list, style, robot_template);
    }

    private void rewrite(File entities_list, String style, File robot_template) throws IOException {
        List<String> entities_iris = new ArrayList<>(FileUtils.readLines(entities_list,"utf-8"));
        List<OWLClass> entities = new ArrayList<>();
        entities_iris.forEach(e->entities.add(df.getOWLClass(IRI.create(e))));
        Map<OWLClass,Set<OWLClassExpression>> map_log_def = getMapOfLogicalDefinitions(o,entities);
        Map<OWLClass,Set<String>> map_definitions = createDefinitions(style,map_log_def);
        export(map_definitions,robot_template);
    }

    private void export(Map<OWLClass, Set<String>> map_definitions, File robot_template) throws IOException {
        List<String> out = new ArrayList<>();
        out.add("ID\tDefinition");
        out.add("ID\tA obo:IAO_0000115");
        for(OWLClass key : map_definitions.keySet()) {
            for(String def:map_definitions.get(key)) {
                out.add(key.getIRI().toString() + "\t" + def);
            }
        }
        FileUtils.writeLines(robot_template,"utf-8",out);

    }

    private Map<OWLClass, Set<String>> createDefinitions(String style, Map<OWLClass, Set<OWLClassExpression>> map_log_def) {

        ClassExpressionToStringTransformer transformer = getTransformer(style);
        Map<OWLClass, Set<String>> definitions = new HashMap<>();

        for(OWLClass key:map_log_def.keySet()) {
            for(OWLClassExpression ce:map_log_def.get(key)) {
                if(!definitions.containsKey(key)) {
                    definitions.put(key,new HashSet<>());
                }
                definitions.get(key).add(transformer.createDefinition(ce));
            }
        }
        return definitions;
    }

    private ClassExpressionToStringTransformer getTransformer(String style) {
        ClassExpressionToStringTransformer transformer;

        switch(style) {
            case "eq_default":
                transformer = new EQDefaultTransformer(newLabels);
                break;
            case "flybase":
                transformer = new FlyBaseAnatomyTransformer(newLabels);
                break;
            default:
                transformer = new DefaultTransformer(newLabels);
        }
        return transformer;
    }

    private HashMap<OWLClass, Set<OWLClassExpression>> getMapOfLogicalDefinitions(OWLOntology o, List<OWLClass> entities) {

        HashMap<OWLClass, Set<OWLClassExpression>> defs = new HashMap<>();

        for (OWLLogicalAxiom axa : o.getLogicalAxioms()) {
            if (axa instanceof OWLEquivalentClassesAxiom) {
                OWLEquivalentClassesAxiom ax = (OWLEquivalentClassesAxiom)axa;
                Set<OWLClass> classes = new HashSet<>();
                Set<OWLClassExpression> ces = new HashSet<>();
                for (OWLClassExpression ce : ax.getClassExpressionsAsList()) {
                    if (ce.isClassExpressionLiteral()) {
                        if(entities.contains(ce)) {
                            classes.add(ce.asOWLClass());
                        }
                    } else if (!getPropertiesInSignature(ce).isEmpty()) {
                        ces.add(ce);
                    }
                }
                for (OWLClass c : classes) {
                    for (OWLClassExpression ce : ces) {
                        if(!defs.containsKey(c)) {
                            defs.put(c,new HashSet<>());
                        }
                        defs.get(c).add(ce);
                    }
                }
            }
        }
        return defs;
    }

    private Set<OWLEntity> getPropertiesInSignature(OWLClassExpression ce) {
        Set<OWLEntity> props = new HashSet<>();
        props.addAll(ce.getObjectPropertiesInSignature());
        props.addAll(ce.getDataPropertiesInSignature());
        return props;
    }
}

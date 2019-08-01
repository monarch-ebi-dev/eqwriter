package ebi.monarch.eqwriter;

import org.apache.commons.io.FileUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class EQWriter {

    private OWLOntologyManager man = OWLManager.createOWLOntologyManager();
    private OWLDataFactory df = man.getOWLDataFactory();
    private Map<OWLEntity,String> newLabels = new HashMap<>();
    private OWLOntology o;

    private EQWriter(String ontology_in, File label_mappings) {
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

/*
        args = new String[6];
        args[0] = "/ws/drosophila-anatomy-developmental-ontology/src/ontology/tmp/merged-source-pre.owl";
        args[1] = "/ws/drosophila-anatomy-developmental-ontology/src/ontology/auto_generated_definitions_seed_dot.txt";
        args[2] = "flybase";
        args[3] = "/data/test_template.owl";
        args[4] = "na";
        args[5] = "add_dot_refs";

*/

        String ontology_in = args[0];
        File entities_list = new File(args[1]);
        String style = args[2];
        File robot_template = new File(args[3]);
        File label_mappings = new File(args[4]);
        String addSourceAnnotation = args.length>5? args[5] : "NA" ;

        EQWriter eq = new EQWriter(ontology_in, label_mappings);
        eq.rewrite(entities_list, style, robot_template,addSourceAnnotation);
    }

    private void rewrite(File entities_list, String style, File robot_template, String addSourceAnnotation) throws IOException {
        List<OWLClass> entities = getOwlClassesForRewritingDefs(entities_list);
        Transformer transformer = getTransformer(style, addSourceAnnotation);
        Set<OWLAnnotationAssertionAxiom> rewrittenDefinitions = new HashSet<>();

        for (OWLClass c : entities) {
            if (transformer instanceof DefinitionRewriterTransformer) {
                rewrittenDefinitions.addAll(transformer.rewriteDefinitions(c));
            } else if (transformer instanceof ClassExpressionToStringTransformer) {
                Set<OWLAnnotation> dotannotations = new HashSet<>();
                if(addSourceAnnotation.contains("add_dot_refs")) {
                    dotannotations.addAll(getDotDefinitionAnnotations(o.getAnnotationAssertionAxioms(c.getIRI())));
                }
                for (OWLAxiom axa : o.getAxioms(c,Imports.INCLUDED)) {
                    if (axa instanceof OWLEquivalentClassesAxiom) {
                        OWLEquivalentClassesAxiom ax = (OWLEquivalentClassesAxiom)axa;
                        for (OWLClassExpression ce : ax.getClassExpressionsAsList()) {
                            if (!getPropertiesInSignature(ce).isEmpty()) {
                                //System.out.println(c);
                                String def = transformer.createDefinition(ce);
                                OWLAnnotationAssertionAxiom ax_out = df.getOWLAnnotationAssertionAxiom(Entities.ap_definition, c.getIRI(), df.getOWLLiteral(def), dotannotations);
                                rewrittenDefinitions.add(ax_out);
                            }
                        }
                    }
                }
            }
        }
        exportOWL(robot_template, rewrittenDefinitions);
    }

    private Set<OWLAnnotation> getDotDefinitionAnnotations(Set<OWLAnnotationAssertionAxiom> annotationAssertionAxioms) {
        Set<OWLAnnotation> dotannotations = new HashSet<>();
        for(OWLAnnotationAssertionAxiom axiom:annotationAssertionAxioms) {
            if(axiom.getProperty().equals(Entities.ap_definition)) {
                if (axiom.getValue().asLiteral().isPresent()) {
                    String olddef = axiom.getValue().asLiteral().get().getLiteral();
                    if (olddef.equals(".")) {
                        dotannotations.addAll(axiom.getAnnotations());
                    }
                }
            }
        }
        return dotannotations;
    }

    private void exportOWL(File robot_template, Set<OWLAnnotationAssertionAxiom> rewrittenDefinitions) throws FileNotFoundException {
        try {
            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLOntology o_new = createOntology(man);
            man.addAxioms(o_new,rewrittenDefinitions);
            man.saveOntology(o_new, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(robot_template));
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
    }

    private OWLOntology createOntology(OWLOntologyManager man) {
        OWLOntology o_new = null;
        try {
            o_new = man.createOntology();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        return o_new;
    }

    private List<OWLClass> getOwlClassesForRewritingDefs(File entities_list) throws IOException {
        List<String> entities_iris = new ArrayList<>();
        if(entities_list.isFile()&&entities_list.getName().endsWith(".txt")) {
            entities_iris.addAll(FileUtils.readLines(entities_list,"utf-8"));
        } else {
            System.out.println("File does not exist or does not have .txt extension, using all classes!");
            o.getClassesInSignature(Imports.INCLUDED).forEach(e->entities_iris.add(e.getIRI().toString()));
        }
        List<OWLClass> entities = new ArrayList<>();
        entities_iris.forEach(e->entities.add(df.getOWLClass(IRI.create(e))));
        return entities;
    }

    private Transformer getTransformer(String style, String addSourceAnnotation) {
        Transformer transformer;

        switch(style) {
            case "eq_default":
                transformer = new EQDefaultTransformer(newLabels,o);
                break;
            case "flybase":
                transformer = new FlyBaseAnatomyTransformer(newLabels,o);
                break;
            case "sub_external":
                transformer = new ReplaceSubDefTransformer(newLabels,o,addSourceAnnotation.equals("source_xref"));
                break;
            default:
                transformer = new DefaultTransformer(newLabels,o);
        }
        return transformer;
    }

    private Set<OWLEntity> getPropertiesInSignature(OWLClassExpression ce) {
        Set<OWLEntity> props = new HashSet<>();
        props.addAll(ce.getObjectPropertiesInSignature());
        props.addAll(ce.getDataPropertiesInSignature());
        return props;
    }
}

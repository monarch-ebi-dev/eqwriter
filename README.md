# EQ writer

## Example usages:

java -jar eq-writer.jar input.owl list_of_classes.txt flybase output.owl NA add_dot_refs

java -jar eq-writer.jar input.owl list_of_classes.txt sub_external output.owl NA source_xref

For flybase:
- Logical definitions should be of the form A and (R some B) and (R some C). The current writer does not deal with nesting (A and (R some (R some C))), nor very well with multiple primary classes (A and B and (R some C)).

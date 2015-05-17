> 

# Inclusion #

A template is included by

```
  {{>template_b}}
```

from `template_a` in the same dir, or

```
  {{>subdir/template_b}}
```

in a sub directory of the directory containing `template_a`.

The include is an "external" section. The data dictionary must enable the include by defining one or more sections for one or more iterations of the include.  See also [Language#Include](Language#Include.md) and the next section, [Example#Repetition](Example#Repetition.md).

Note that all template files are named `"*.xtm"`, and the loader excludes `".."` relative directories as a security precaution.

# Repetition #

The trick with ctemplate style programming is the use of data dictionaries to drive output rather than the template.  The template language has no looping but the data dictionary can cause repetitive output by using the template section syntax.

For the Hapax2 template expression

```
  {{#imports}}
  import {{=import_spec}};{{/imports}}
```

Repetitive sections may be defined as
```
   TemplateDataDictionary top = new TemplateDictionary();
   for (String importSpec: ...){
     top.addSection("imports").setVariable("import_spec",importSpec);
   }
```

# Imports with template #

The previous example applied with a template illustrates sections and their evaluation in Hapax.

This example is available from http://hapax2.googlecode.com/svn/examples/imports/

```
   TemplateDataDictionary top = TemplateDictionary.create();
   TemplateDataDictionary imports = top.addSection("imports");
   imports.addSection("import").setVariable("import_spec", "java.lang.Long");
   imports.addSection("import").setVariable("import_spec", "java.lang.String");
   imports.addSection("import").setVariable("import_spec", "java.lang.Double");
```

The root template is `src/foo.xtm`
```
   {{>imports}}
```
and the included template is `src/imports.xtm`
```
   {{#import}}import {{=import_spec}};
   {{/import}}
```

The data dictionary graph is traversed across hierarchical section boundaries, so the "imports" external section needs a node in the graph as does the "import" internal section.  Within a section, many fields are differentiated by name -- as in an object.

# Advanced #

A nice, real world example may be found in Gap Data, although this code base has it's own implementation of Hapax2 (which could be called Hapax3).

See the driver [GAP ODL Main](http://code.google.com/p/gap-data/source/browse/odlc/src/gap/odl/Main.java) and [OD](http://code.google.com/p/gap-data/source/browse/trunk/src/gap/service/OD.java) + [Classes](http://code.google.com/p/gap-data/source/browse/types/src/gap/service/Classes.java), for the template [Java XTM](http://code.google.com/p/gap-data/source/browse/odlc/xtm/BeanData.java.xtm), and data model [Person ODL](http://code.google.com/p/gap-data/source/browse/trunk/odl/oso/data/Person.odl).

In this example, a data model is parsed from a text file and applied to a Hapax2 template.
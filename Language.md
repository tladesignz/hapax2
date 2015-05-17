> 

> # H2/CTemplate #
```
 Hello, {{=world}}.
```

> [CTemplate](http://code.google.com/p/google-ctemplate) is a "simple as possible for performance" template language.

> Any objective may be met through the application of a combination of the following features.  And when met this way, the result will have a maximal runtime performance.

> Hapax2 implements a minimal variant on [Google CTemplate](http://code.google.com/p/google-ctemplate).  Most notably, the use of the `"{{=...}}"` syntax for variable dereference rather than configuration.

> ## Variable ##

> A value pulled from the [data dictionary](http://code.google.com/p/hapax2/source/browse/trunk/src/hapax/TemplateDictionary.java) and emitted.
```
 {{var_name}}
```
> or (equivalently)
```
 {{=var_name}}
```

> ## Comment ##

> A multiline ctemplate comment is formed in the following syntax.
```
 {{!...}}
```

> ## Section ##

> A section is iterated zero or more times according to the [data dictionary](http://code.google.com/p/hapax2/source/browse/trunk/src/hapax/TemplateDictionary.java).
```
 {{#sectionName}}
   ...
 {{/sectionName}}
```
> The iteration of a section (or include) is controlled by the sections programmatically added into the [data dictionary](http://code.google.com/p/hapax2/source/browse/trunk/src/hapax/TemplateDictionary.java) as child data dictionaries in a section list.

> Sections are defined in one or more instances in the data dictionary, as in the following example.
```
  {{#menu}}
    {{#submenu}}
      {{#menu}}{{/menu}}
    {{/submenu}}
  {{/menu}}
```
> This template will work for both
```
  TemplateDataDictionary doc = new TemplateDictionary();
  TemplateDataDictionary menu = doc.addSection("menu");
  TemplateDataDictionary sm = menu.addSection("submenu");
  TemplateDataDictionary ssm = sm.addSection("menu");
```
> and
```
  TemplateDataDictionary doc = new TemplateDictionary();
  TemplateDataDictionary menu = doc.addSection("menu");
  TemplateDataDictionary sm = menu.addSection("submenu");
```

> In the second case, the section "menu" is referenced from the parent of "submenu", and in the first case this section is found within "submenu".

> See also [Example#Repetition](Example#Repetition.md).

> ## Include ##

> The include is an "external" section.  The [data dictionary](http://code.google.com/p/hapax2/source/browse/trunk/src/hapax/TemplateDictionary.java) must enable the include by defining one or more sections for one or more iterations of the include.
```
 {{>name}}
```
> The include name may be redirected by a variable defined in the [data dictionary](http://code.google.com/p/hapax2/source/browse/trunk/src/hapax/TemplateDictionary.java).  A positive value for a variable of the same name will be employed as the template file base name.  Under variable redirection, the include section name remains the name defined in the template.

> The template file is resolved from the template name (or variable value) by prepending the context directory path, and appending the dot "xtm" filename extension.

> The resolved template file may be a URL.

> See also [Example#Inclusion](Example#Inclusion.md).

> ## Special iteration sections ##

> Within a section or include are defined five special sections as aids to iteration.  These are named for the section or include name string, with a special suffix string appended.
```
 {{#field}}{{#field_it_NotFirst}},{{/field_it_NotFirst}} {{=field_name}}{{/field}}
```
> These special section names are enumerated in the following patterns.

> <dl></li></ul>

<blockquote><dt><tt>name_it_NotFirst</tt>
<dd> Sections in scope having a name in this pattern are visible when not in the first iteration.</blockquote>

<blockquote><dt><tt>name_it_First</tt>
<dd> Sections in scope having a name in this pattern are visible in the first iteration.</blockquote>

<blockquote><dt><tt>name_it_NotLast</tt>
<dd> Sections in scope having a name in this pattern are visible when not in the last iteration.</blockquote>

<blockquote><dt><tt>name_it_Last</tt>
<dd> Sections in scope having a name in this pattern are visible in the last iteration.</blockquote>

<blockquote><dt><tt>name_it_Exclusive</tt>
<dd> Sections in scope having a name in this pattern are visible when not the first and not the last.</blockquote>

> # Recursive Include #

> A template file that includes itself will cause a stack overflow exception.

> For example, a template file named
```
  a.xtm
```
> that defines the include
```
 {{>a}}
```
> to include itself will cause the following stack overflow exception as seen in  [Issue #1](http://code.google.com/p/hapax2/issues/detail?id=1) [(src)](http://hapax2.googlecode.com/svn/issues/1)
```
	at hapax.TemplateDictionary.getVariable(TemplateDictionary.java:148)
	at hapax.TemplateDictionary.getVariable(TemplateDictionary.java:148)
	at hapax.TemplateDictionary.getVariable(TemplateDictionary.java:148)
	at hapax.TemplateDictionary.getVariable(TemplateDictionary.java:148)
	at hapax.TemplateDictionary.getVariable(TemplateDictionary.java:148)
	at hapax.TemplateDictionary.getVariable(TemplateDictionary.java:148)
```

> ## Cause ##

> The call stack runs out in "getVariable" within "renderSection", because each time the template is included into itself, the variable lookup with inheritance is performed to check that the include name, "template", is not renamed by a variable assignment.  In a sense, this "getVariable" call explores or probes the future of the call stack in the execution of the recursion.

> The dictionary tree depth at the point of inclusion is replicated or multiplied by each inclusion.  For a tree depth of X and a recursion depth of Y, the traversal from leaf to root is X `*` Y -- the length of the call path taken by "getVariable" at each recursion point to check that the include name, "template".

> So the recursive include finally blows the call stack in "getVariable".

> ## Alternatives ##

> It may be possible, although tricky, to accomplish a similar objective through a few similar templates controlled via template renaming.  A template include name (e.g. "a") may be renamed by setting a variable to map it to another name (e.g. from "a" to "b").  An empty template file (e.g. "nil") would be employed to redirect or retarget the inclusion in the leaves of the data dictionary tree.
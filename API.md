> 

> # Files #

> Files are named with "xtm" filename extension, and are loaded from a directory defined for the [template loader](http://code.google.com/p/hapax2/source/browse/trunk/src/hapax/TemplateCache.java).

> # Variables #

> Variables defined in the [data dictionary](http://code.google.com/p/hapax2/source/browse/trunk/src/hapax/TemplateDictionary.java) may be included into output, or will redirect includes.

> # Sections #

> CTemplate sections are repeated once for each [data dictionary](http://code.google.com/p/hapax2/source/browse/trunk/src/hapax/TemplateDictionary.java) found
> in their name.

> # Scopes #

> A CTemplate section or include enters the scope of a defined [data dictionary](http://code.google.com/p/hapax2/source/browse/trunk/src/hapax/TemplateDictionary.java).  This scope inherits and overrides its parent scope.  Variables defined in the child scope of a section are clearly differentiated from other siblings and cousins.

> # Multi-Threading #

> The [template cache](http://code.google.com/p/hapax2/source/browse/trunk/src/hapax/TemplateCache.java) has a very light multi-thread protection based on a synchronized put to a plain Hash Map. This is intended for coherency without contention.

> A race condition would need be protected externally using a lock.
package hapax;

/**
 * Implementors of TemplateLoader are responsible for returning a Template
 * object for the given identifier.
 *
 * The use of the term "filename" is merely convention; it can be any arbitrary
 * identifier supported by the underlying storage system.
 *
 * See {@link TemplateCache} for an example TemplateLoader.
 *
 * @author dcoker
 */
public interface TemplateLoader {

    public Template getTemplate(String filename)
        throws TemplateException;

}

package hapax;

/**
 * Implementors of TemplateLoader are responsible for returning a Template
 * object for the given identifier.
 *
 * The terms "directory" and "file" are conventional to collection and
 * item identifiers as may be supported by the loader.
 *
 * See {@link TemplateCache} for an example TemplateLoader.
 *
 * @author dcoker
 * @author jdp
 */
public interface TemplateLoader {

    public class Context
        extends Object
        implements TemplateLoader
    {

        private final TemplateLoader loader;
        private final String template_directory;


        public Context(TemplateLoader loader, String template_directory){
            super();
            this.loader = loader;
            this.template_directory = template_directory;
        }


        public String getTemplateDirectory() {
            return template_directory;
        }
        public Template getTemplate(String filename)
            throws TemplateException
        {
            return this.loader.getTemplate(this,filename);
        }
        public Template getTemplate(TemplateLoader context, String filename)
            throws TemplateException
        {
            return this.loader.getTemplate(context,filename);
        }
    }

    public String getTemplateDirectory();

    public Template getTemplate(String filename)
        throws TemplateException;

    public Template getTemplate(TemplateLoader context, String filename)
        throws TemplateException;

}

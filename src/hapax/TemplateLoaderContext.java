package hapax;

/**
 * The template loader context is created in Template getTemplate for
 * the template file.  Includes in the template will use this path to
 * resolve their files.
 *
 * @author dcoker 
 * @author jdp
 */
public interface TemplateLoaderContext {

    public class Simple
        extends Object
        implements TemplateLoaderContext
    {

        private final TemplateLoader loader;
        private final String template_directory;


        public Simple(TemplateLoader loader, String template_directory){
            super();
            this.loader = loader;
            this.template_directory = template_directory;
        }


        public TemplateLoader getLoader() {
            return loader;
        }
        public String getTemplateDirectory() {
            return template_directory;
        }
    }


    public TemplateLoader getLoader();

    public String getTemplateDirectory();

}

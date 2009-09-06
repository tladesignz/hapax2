package hapax;

/**
 * The template loader context is created in Template getTemplate for
 * the template file.  Includes in the template will use this path to
 * resolve their files.
 *
 * @author dcoker 
 * @author jdp
 */
public class TemplateLoaderContext {

    private final TemplateLoader loader;
    private final String template_directory;
    private final boolean isfile;


    public TemplateLoaderContext(TemplateLoader loader, String template_directory){
        super();
        this.loader = loader;
        this.template_directory = template_directory;
        this.isfile = true;
    }
    public TemplateLoaderContext(TemplateLoader loader, String template_directory, boolean isf){
        super();
        this.loader = loader;
        this.template_directory = template_directory;
        this.isfile = isf;
    }


    public boolean isFile(){
        return this.isfile;
    }
    public TemplateLoader getLoader() {
        return loader;
    }
    public String getTemplateDirectory() {
        return template_directory;
    }
}

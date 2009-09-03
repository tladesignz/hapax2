package hapax;

/**
 * A dummy loader that throws an exception any time it is used.  This is used
 * by Templates that do not have a Loader associated with them.
 *
 * @author dcoker
 * @author jdp
 */
class NullTemplateLoader
    extends Object
    implements TemplateLoader
{

    NullTemplateLoader(){
        super();
    }

    public Template getTemplate(String filename)
        throws TemplateException
    {
        throw new TemplateException("You must configure the Template with setLoader() prior to including hapax.");
    }
}

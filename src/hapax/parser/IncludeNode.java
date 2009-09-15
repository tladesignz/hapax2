package hapax.parser;

import hapax.Iterator;
import hapax.Modifiers;
import hapax.Path;
import hapax.Template;
import hapax.TemplateDictionary;
import hapax.TemplateException;
import hapax.TemplateLoaderContext;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.List;

/**
 * Represents an {{> include token.
 *
 * @author dcoker
 * @author jdp
 */
public final class IncludeNode
    extends TemplateNode
    implements TemplateNode.Section
{

    private final String name;

    final List<Modifiers.FLAGS> modifiers;


    IncludeNode(int lno, String spec) {
        super(lno);
        String split[] = spec.split(":");
        this.name = split[0];
        this.modifiers = Modifiers.parseModifiers(split);
    }


    public String getSectionName(){
        return this.name;
    }
    @Override
    public final void evaluate(TemplateDictionary dict, TemplateLoaderContext context, PrintWriter out)
        throws TemplateException
    {
        String sectionName = this.name;

        List<TemplateDictionary> section = dict.getSection(sectionName);

        if (null != section){

            String filename = this.resolveName(dict,context);

            if (this.acceptFile(dict,filename)){
                /*
                 * Load template from resolved name
                 */
                Template template = context.getLoader().getTemplate(filename);

                /*
                 * Modified rendering
                 */
                PrintWriter previous_printwriter = null;
                StringWriter sw = null;
                if (!this.modifiers.isEmpty()) {
                    previous_printwriter = out;
                    sw = new StringWriter();
                    out = new PrintWriter(sw);
                }

                if (section.size() == 0) {

                    Iterator.Define(dict,sectionName,0,1);
                    /*
                     * Once
                     */
                    template.render(dict, out);
                }
                else {
                    /*
                     * Repeat
                     */
                    for (int cc = 0, count = section.size(); cc < count; cc++){

                        TemplateDictionary child = section.get(cc);

                        Iterator.Define(child,sectionName,cc,count);

                        template.render(child, out);
                    }
                }

                /*
                 */
                if (previous_printwriter != null) {
                    String results = sw.toString();
                    out = previous_printwriter;
                    out.write(Modifiers.applyModifiers(results, this.modifiers));
                }
            }
        }
    }

    protected boolean acceptFile(TemplateDictionary dict, String filename)
        throws TemplateException
    {
        /*
         * Detect cycles according to the scopes in the data dictionary tree
         */
        String warning_flag = "__already__included__" + filename;
        if (dict.containsVariable(warning_flag)) {
            String msg = MessageFormat.format("loop detected in \"{0}\" for \"{1}\".", this.name, filename);
            throw new CyclicIncludeException(msg);
        }
        else {
            dict.putVariable(warning_flag, "");
            return true;
        }
    }

    protected final String resolveName(TemplateDictionary dict, TemplateLoaderContext context)
        throws TemplateException
    {
        String name = this.name;
        /*
         * When it's quoted, it's protected from a redirect via the
         * variable map.
         */
        String basename = TrimQuotes(name);

        if (name == basename){
            /*
             * If it's not quoted, look it up for a redirect
             */
            String redirect = dict.getVariable(name);

            if (null != redirect && 0 != redirect.length())
                basename = redirect;
        }

        return Path.toFile(context.getTemplateDirectory(), basename);
    }

    protected final static String TrimQuotes(String string){

        if ('"' == string.charAt(0)) {
            int stringLen = string.length();
            if ('"' == string.charAt(stringLen-1))
                string = string.substring(1,stringLen-2);
            else
                string = string.substring(1);
        } 
        return string;
    }
}

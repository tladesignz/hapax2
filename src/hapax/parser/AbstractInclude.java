package hapax.parser;

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
 * Node for the [include] tag.
 *
 * @author dcoker
 */
public abstract class AbstractInclude
    extends TemplateNode
{

    protected final String name;

    protected final List<Modifiers.FLAGS> modifiers;


    protected AbstractInclude(String spec) {
        super();
        String split[] = spec.split(":");
        this.name = split[0];
        this.modifiers = Modifiers.parseModifiers(split);
    }


    @Override
    public final void evaluate(TemplateDictionary dict, TemplateLoaderContext context, PrintWriter collector)
        throws TemplateException
    {
        String filename = this.resolveName(dict,context);

        if (this.acceptFile(dict,filename)){
            /*
             * Load template
             */
            Template template = context.getLoader().getTemplate(filename);

            /*
             * Modified rendering
             */
            PrintWriter previous_printwriter = null;
            StringWriter sw = null;
            if (this.modifiers.size() > 0) {
                previous_printwriter = collector;
                sw = new StringWriter();
                collector = new PrintWriter(sw);
            }

            List<TemplateDictionary> child_dicts = dict.getChildDicts(filename);

            if (child_dicts.size() == 0) {
                /*
                 * Once
                 */
                template.render(dict, collector);
            }
            else {
                /*
                 * Repeat
                 */
                for (TemplateDictionary subdict : child_dicts) {
                    template.render(subdict, collector);
                }
            }

            if (previous_printwriter != null) {
                String results = sw.toString();
                collector = previous_printwriter;
                collector.write(Modifiers.applyModifiers(results, this.modifiers));
            }
        }
    }

    protected boolean acceptFile(TemplateDictionary dict, String filename)
        throws TemplateException
    {
        /*
         * Detect cycles
         */
        String warning_flag = "__already__included__" + filename;
        if (dict.contains(warning_flag)) {
            String msg = MessageFormat.format("loop detected in {0} for {1}", this.name, filename);
            throw new CyclicIncludeException(msg);
        }
        else {
            dict.put(warning_flag, "");
            return true;
        }
    }

    protected final String resolveName(TemplateDictionary dict, TemplateLoaderContext context)
        throws TemplateException
    {
        String name = this.name;
        int nameLen = name.length();

        String filename;
        if ('"' == name.charAt(0)) {

            if ('"' == name.charAt(nameLen-1))
                filename = name.substring(1,nameLen-2);
            else
                filename = name.substring(1);
        } 
        else {
            filename = dict.get(name);
            if (null == filename || 0 == filename.length())//(permit case to proceed)
                filename = name;
        }

        /*
         * With leading '/', ignore the path of the current template.
         */
        if (0 < filename.length() && '/' == filename.charAt(0)) {
            do {
                filename = filename.substring(1);
            }
            while (0 < filename.length() && '/' == filename.charAt(0));

            return filename;
        }
        else {
            String contextDir = context.getTemplateDirectory();
            return Path.toFile(contextDir, filename);
        }
    }

}

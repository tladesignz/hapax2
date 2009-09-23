package hapax;

import hapax.parser.CTemplateParser;
import hapax.parser.SectionNode;
import hapax.parser.TemplateNode;
import static hapax.parser.TemplateNode.TemplateType.*;
import hapax.parser.TemplateParser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.List;

/**
 * Template executes the program defined by the tmpl_ list.  The list itself is
 * constructed by implementations of {@link TemplateParser}.
 *
 * Instead of constructing a Template directly, use an implementation of {@link
 * TemplateLoader} such as {@link TemplateCache}.
 *
 * @author dcoker
 * @author jdp
 */
public final class Template 
    extends Object
{
    /**
     * Parse and render a C template.
     */
    public final static String Eval(TemplateLoaderContext context, TemplateDictionary dict, String source)
        throws TemplateException
    {
        Template template = new Template(CTemplateParser.Instance,source,context);
        return template.renderToString(dict);
    }


    private final long lastModified;
    private final List<TemplateNode> template;
    private TemplateLoader loader;
    private TemplateLoaderContext context;


    public Template(String template, TemplateLoaderContext context)
        throws TemplateException
    {
        this(0L,template,context);
    }
    public Template(long lastModified, String template, TemplateLoaderContext context)
        throws TemplateException
    {
        this(lastModified, CTemplateParser.Instance, template, context);
    }
    public Template(TemplateParser parser, String template, TemplateLoaderContext context)
        throws TemplateException
    {
        this(0L, parser, template, context);
    }
    public Template(long lastModified, TemplateParser parser, String template, TemplateLoaderContext context)
        throws TemplateException
    {
        this(lastModified, parser.parse(context,template), context);
    }
    private Template(long lastModified, List<TemplateNode> tmpl, TemplateLoaderContext context) {
        super();
        this.lastModified = lastModified;
        this.template = tmpl;
        this.context = context;
        this.loader = context.getLoader();
    }


    public boolean hasLastModified(){
        return (0L < this.lastModified);
    }
    public long getLastModified(){
        return this.lastModified;
    }

    public void render(TemplateDictionary dict, PrintWriter writer)
        throws TemplateException
    {
        this.render(Top, this.template, dict, writer);
    }
    public String renderToString(TemplateDictionary dict)
        throws TemplateException
    {
        StringWriter buffer = new StringWriter();

        this.render(Top, this.template, dict, (new PrintWriter(buffer)));

        return buffer.toString();
    }

    private void render(int offset, List<TemplateNode> template, TemplateDictionary dict, PrintWriter writer)
        throws TemplateException
    {
        try {
            for (int position = 0; position < template.size(); position++) {

                TemplateNode node = template.get(position);

                switch (node.getTemplateType()){

                case TemplateTypeSection:

                    position = this.renderSectionNode(offset, template, dict, position, ((SectionNode)node), writer);
                    break;

                default:
                    node.evaluate(dict, this.context, writer);
                    break;
                }
            }
        }
        finally {
            dict.destroy();
        }
    }
    private int renderSectionNode(int offset, List<TemplateNode> template, TemplateDictionary dict, int open,
                                  SectionNode section, PrintWriter writer)
        throws TemplateException
    {
        int next = (open + 1);
        int close = (open + section.getIndexOfCloseRelative());

        if (close >= next && close < template.size()){

            String sectionName = section.getSectionName();

            List<TemplateDictionary> data = dict.getSection(sectionName);

            if (null != data){

                if (data.size() == 0) {

                    Iterator.Define(dict,sectionName,0,1);
                    /*
                     * Once
                     */
                    this.render(next, template.subList(next, close), dict, writer);
                }
                else {
                    /*
                     * Repeat
                     */
                    for (int cc = 0, count = data.size(); cc < count; cc++){

                        TemplateDictionary child = data.get(cc);

                        Iterator.Define(child,sectionName,cc,count);

                        this.render(next, template.subList(next, close), child, writer);
                    }
                }
            }
            return close;
        }
        else
            throw new TemplateException("Missing close tag for section '" + section.getSectionName()+"' at line "+section.lineNumber+".");
    }

    private final static int Top = 0;
}

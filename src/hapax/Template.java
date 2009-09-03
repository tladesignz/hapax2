package hapax;

import hapax.parser.CTemplateParser;
import hapax.parser.EztConditionalNode;
import hapax.parser.EztDefineNode;
import hapax.parser.EztParser;
import hapax.parser.Range;
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
    public final static String EvalCt(TemplateLoaderContext context, TemplateDictionary dict, String source)
        throws TemplateException
    {
        Template template = new Template(CTemplateParser.Instance,source,context);
        return template.renderToString(dict);
    }
    /**
     * Parse and render an EZT template.
     */
    public final static String EvalEzt(TemplateLoaderContext context, TemplateDictionary dict, String source)
        throws TemplateException
    {
        Template template = new Template(EztParser.Instance,source,context);
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
        this.render(this.template, dict, writer);
    }
    public String renderToString(final TemplateDictionary dict)
        throws TemplateException
    {
        StringWriter buffer = new StringWriter();

        this.render(this.template, dict, (new PrintWriter(buffer)));

        return buffer.toString();
    }
    /**
     * Main render driver
     */
    private void render(final List<TemplateNode> template,
                        final TemplateDictionary dict,
                        final PrintWriter writer)
        throws TemplateException
    {
        for (int i = 0; i < template.size(); i++) {

            TemplateNode node = template.get(i);

            switch (node.getTemplateType()){

            case TemplateTypeSection:

                i = this.renderSectionNode(template, dict, i, writer);
                break;

            case TemplateTypeEztDefine:

                i = this.renderEztDefineNode(template, dict, i);
                break;

            case TemplateTypeEztConditional:

                i = this.renderEztConditionalNode(template, dict, i, writer);
                break;

            default:
                node.evaluate(dict, this.context, writer);
                break;
            }
        }
    }
    /**
     * Render case
     */
    private int renderSectionNode(final List<TemplateNode> template,
                                  final TemplateDictionary dict,
                                  final int open,
                                  final PrintWriter writer)
        throws TemplateException
    {
        SectionNode section = (SectionNode) template.get(open);
        int next = (open + 1);
        int close = indexOfClose(template, next, section);

        if (close == template.size())
            throw new TemplateException("missing close tag for " + section.getSectionName());
        else {
            /* If this section is hidden, we don't render the intermediate
             * nodes.
             */
            if (dict.isSectionHidden(section.getSectionName()))

                return close;

            else {
                List<TemplateDictionary> subdicts = dict.getChildDicts(section.getSectionName());

                if (subdicts.size() == 0) {
                    /*
                     * Once
                     */
                    this.render(template.subList(next, close), dict, writer);
                }
                else {
                    /*
                     * Repeat
                     */
                    for (TemplateDictionary subdict : subdicts) {

                        this.render(template.subList(next, close), subdict, writer);
                    }
                }
                return close;
            }
        }
    }
    /**
     * Render case
     */
    private int renderEztDefineNode(List<TemplateNode> template,
                                    TemplateDictionary dict, int node)
        throws TemplateException
    {
        EztDefineNode edn = (EztDefineNode) template.get(node);

        String var_name = edn.getVariableName();

        Range range = edn.advise(template, node);

        List<TemplateNode> view = template.subList(range.getStart(), range.getStop());
        {
            StringWriter buffer = new StringWriter();

            this.render(view, dict, new PrintWriter(buffer));

            String result = buffer.toString();

            dict.put(var_name, result);
        }
        return range.getSkipTo();
    }
    /**
     * Render case
     */
    private int renderEztConditionalNode(List<TemplateNode> template,
                                         TemplateDictionary dict,
                                         int node, PrintWriter writer)
        throws TemplateException
    {
        EztConditionalNode ecn = (EztConditionalNode) template.get(node);

        Range range = ecn.advise(template, node, dict);

        List<TemplateNode> view = template.subList(range.getStart(), range.getStop());

        this.render(view, dict, writer);

        return range.getSkipTo();
    }

    private final static int indexOfClose(List<TemplateNode> template, int ofs, SectionNode node)
        throws TemplateException
    {
        ofs += 1;
        for ( int stack = 0; ofs < template.size(); ++ofs) {

            TemplateNode tp = template.get(ofs);

            if (TemplateTypeSection == tp.getTemplateType()) {

                SectionNode section = (SectionNode) tp;

                if (section.isOpenSectionTag()) {

                    stack++;
                }
                else if (section.isCloseSectionTag()) {

                    if (section.getSectionName().equals(node.getSectionName()))

                        return ofs;

                    else {
                        if (stack == 0) {
                            String msg = MessageFormat.format("mismatched close tag: expecting a close tag for {0}, " +
                                                              "but got close tag for {1}", node.getSectionName(),
                                                              section.getSectionName());

                            throw new TemplateException(msg);
                        }
                        else {
                            stack--;
                        }
                    }
                }
            }
        }
        return -1;
    }
}

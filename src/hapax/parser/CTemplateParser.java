package hapax.parser;

import hapax.Template;
import hapax.TemplateDictionary;
import hapax.TemplateException;
import hapax.TemplateLoaderContext;
import static hapax.parser.TemplateNode.TemplateType.*;

import java.text.MessageFormat;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This parser turns strings containing the contents of a template into a list
 * of TemplateNodes.
 * 
 * Expressions include ctemplate language, with {@link EztParser EZT
 * language} embedded within <code>"{{{"</code> and
 * <code>"}}}"</code>.  Note that <code>"{{["</code> and
 * <code>"]}}"</code> operate equivalently.
 * 
 *
 * @author dcoker
 * @author jdp
 */
public final class CTemplateParser
    extends Object
    implements TemplateParser
{
    /**
     * Shared stateless C template parser.
     */
    public final static TemplateParser Instance = new CTemplateParser();
    /**
     * Parse and render a C template.
     */
    public final static String Eval(TemplateLoaderContext context, TemplateDictionary dict, String sourcecode)
        throws TemplateException
    {
        Template template = new Template(Instance,sourcecode,context);
        return template.renderToString(dict);
    }


    final static List<TemplateNode> Parse(TemplateLoaderContext context, String template)
        throws TemplateParserException
    {
        return Instance.parse(context,template);
    }

    private enum NODE_TYPE {
        OPEN_SECTION, CLOSE_SECTION, VARIABLE, TEXT_NODE, INCLUDE_SECTION, EZT_BLOCK, END_INPUT ;
    }


    private final static int RE_FLAGS =
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE;

    private final static String OPEN_SQUIGGLE = Pattern.quote("{{");

    private final static String CLOSE_SQUIGGLE = Pattern.quote("}}");

    private final static String VARIABLE_RE = "([a-zA-Z_]+(:[a-zA-Z]+)*)*";

    private final static Pattern RE_OPEN_SECTION =
        Pattern
        .compile(OPEN_SQUIGGLE + "#([a-zA-Z_]+)" + CLOSE_SQUIGGLE, RE_FLAGS);

    private final static Pattern RE_CLOSE_SECTION =
        Pattern
        .compile(OPEN_SQUIGGLE + "/([a-zA-Z_]+)" + CLOSE_SQUIGGLE, RE_FLAGS);

    private final static Pattern RE_VARIABLE =
        Pattern.compile(OPEN_SQUIGGLE + VARIABLE_RE + CLOSE_SQUIGGLE, RE_FLAGS);

    private final static Pattern RE_INCLUDE =
        Pattern
        .compile(OPEN_SQUIGGLE + ">" + VARIABLE_RE + CLOSE_SQUIGGLE,
                 RE_FLAGS);



    public CTemplateParser() {
        super();
    }


    private static NODE_TYPE next(StringBuilder input) {

        int inlen = input.length();
        switch (inlen){
        case 0:
            return NODE_TYPE.END_INPUT;
        case 1:
        case 2:
        case 3:
        case 4:
            return NODE_TYPE.TEXT_NODE;
        default:
            if ('{' == input.charAt(0) && '{' == input.charAt(1)){

                switch (input.charAt(2)){
                case '#':
                    return NODE_TYPE.OPEN_SECTION;
                case '/':
                    return NODE_TYPE.CLOSE_SECTION;
                case '>':
                    return NODE_TYPE.INCLUDE_SECTION;
                case '{':
                case '[':
                    return NODE_TYPE.EZT_BLOCK;
                default:
                    return NODE_TYPE.VARIABLE;
                }
            }
            return NODE_TYPE.TEXT_NODE;
        }
    }

    public List<TemplateNode> parse(TemplateLoaderContext context, String template)
        throws TemplateParserException
    {
        List<TemplateNode> list = new LinkedList<TemplateNode>();
        StringBuilder input = new StringBuilder(template);
        TemplateNode node = null;
        while (true) {
            switch (next(input)) {
            case OPEN_SECTION:
                node = parseOpenSection(input);
                break;
            case CLOSE_SECTION:
                node = parseCloseSection(input);
                break;
            case VARIABLE:
                node = parseVariable(input);
                break;
            case TEXT_NODE:
                node = parseTextNode(input);
                break;
            case INCLUDE_SECTION:
                node = parseInclude(input);
                break;
            case EZT_BLOCK:{
                node = null;
                List<TemplateNode> block = parseEztBlock(context,input);
                if (null != block && 0 < block.size())
                    list.addAll(block);
            }
                break;
            case END_INPUT:
                return this.close(list);

            default:
                throw new RuntimeException("Internal error parsing template.");
            }
            if (null != node)
                list.add(node);
        }
    }

    /**
     * Terminal scan
     */
    private static List<TemplateNode> close(List<TemplateNode> template)
        throws TemplateParserException
    {

        for (int cc = 0, count = template.size(); cc < count; cc++){

            TemplateNode node = template.get(cc);

            switch (node.getTemplateType()){

            case TemplateTypeSection:

                SectionNode section = (SectionNode)node;

                section.indexOfClose = IndexOfClose(template,cc,section);

                break;
            default:
                break;
            }
        }

        return template;
    }

    private static TemplateNode parseTextNode(StringBuilder input) {
        int next_braces = input.indexOf("{{");
        if (next_braces == -1) { // no more parser syntax
            String text = input.toString();
            input.setLength(0);
            input.trimToSize();
            return (new TextNode(text));
        }
        else {
            String text = input.substring(0, next_braces);
            input.delete(0, next_braces);
            if (text.length() > 0)
                return (new TextNode(text));
            else
                return null;
        }
    }

    private static TemplateNode parseInclude(StringBuilder input)
        throws TemplateParserException
    {
        String consumed = consume(input, RE_INCLUDE);
        return (new IncludeNode(consumed));
    }
    private static TemplateNode parseVariable(StringBuilder input)
        throws TemplateParserException
    {
        String consumed = consume(input, RE_VARIABLE);
        return (new VariableNode(consumed));
    }
    private static TemplateNode parseCloseSection(StringBuilder input)
        throws TemplateParserException
    {
        String consumed = consume(input, RE_CLOSE_SECTION);
        return (SectionNode.Close(consumed));
    }
    private static TemplateNode parseOpenSection(StringBuilder input)
        throws TemplateParserException
    {
        String consumed = consume(input, RE_OPEN_SECTION);
        return (SectionNode.Open(consumed));
    }

    private static List<TemplateNode> parseEztBlock(TemplateLoaderContext context, StringBuilder input)
        throws TemplateParserException
    {
        int end = input.indexOf("}}}");
        if (0 > end)
            end = input.indexOf("]}}");
        if (0 < end){
            String source = input.substring(3,end);
            end += 3;
            input.delete(0,end);
            return EztParser.Instance.parse(context,source);
        }
        else 
            throw new TemplateParserException("Missing matching '}}}' for opening '{{{' in '"+input+"'.");
    }

    private static String consume(StringBuilder input, Pattern p)
        throws TemplateParserException
    {
        Matcher m = p.matcher(input);
        if (m.lookingAt()) {
            String string_to_return = m.group(1);
            input.delete(0, m.end());
            return string_to_return;
        }
        throw new TemplateParserException("Unexpected or malformed input: " + input);
    }

    private final static int IndexOfClose(List<TemplateNode> template, int ofs, SectionNode node)
        throws TemplateParserException
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

                            throw new TemplateParserException(msg);
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

package hapax.parser;

import hapax.Template;
import hapax.TemplateDictionary;
import hapax.TemplateException;
import hapax.TemplateLoaderContext;

import java.util.LinkedList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser for EZT-style templates.  This differs from the collab.net
 * implementation in a variety of ways.
 *
 * Features supported:
 *
 * Includes: [include "filename"] [include variable]
 *
 * Inserts, which are identical to includes: [insertfile "filename"] [insertfile
 * variable]
 *
 * Variable replacement: [varable]
 *
 * Existence: [if-any variable]x[else]y[end]
 *
 * Comparisons: [is x "y"][else][end]
 *
 * Nesting for [if-any], [is], and [define] is supported.
 *
 * No escaping is performed, so [format raw] is a no-op.
 *
 * @author dcoker
 */
public final class EztParser 
    extends Object
    implements TemplateParser
{
    /**
     * Shared stateless EZT template parser.
     */
    public final static TemplateParser Instance = new EztParser();
    /**
     * Parse and render an EZT template.
     */
    public final static String Eval(TemplateLoaderContext context, TemplateDictionary dict, String sourcecode)
        throws TemplateException
    {
        Template template = new Template(0L,Instance,sourcecode,context);
        return template.renderToString(dict);
    }


    final static List<TemplateNode> Parse(TemplateLoaderContext context, String template)
        throws TemplateParserException
    {
        return Instance.parse(context,template);
    }
    /**
     * string literal
     */
    private static final String TOK_LITERAL = "([^\\[]+)";
    /**
     * variable name
     */
    private static final String TOK_IDENT = "[a-z][a-z0-9_.-]*";
    /**
     * if-any, include, define, etc
     */
    private static final String TOK_DIRECTIVE = "[a-z_-]+";
    /**
     * args list of the STMT_DIRECTIVE
     */
    private static final String TOK_DIRECTIVE_ARGS =
        "((" + TOK_IDENT + ")|(\\\"[^\"]*\\\")|(" + TOK_IDENT +
        " \\\"[^\"]*\\\"))";
    /**
     * variable [dereference]
     */
    private static final String STMT_VARIABLE_DEREF = "(\\[" + TOK_IDENT + "\\])";
    /**
     *  [directive arg], [directive arg arg], [directive "arg" "arg"]
     */
    private static final String STMT_DIRECTIVE =
        "(\\[(" + TOK_DIRECTIVE + ")\\s+" + TOK_DIRECTIVE_ARGS + "\\s*\\])";
    /**
     * map [[] to "["
     */
    private static final String STMT_BRACKET = "(\\[\\[\\])";
    /**
     * [#comment]
     */
    private static final String STMT_COMMENT = "(\\[#[^\\]]*\\])";
    /**
     * Group of patterns
     */
    private static final Pattern PATTERN = Pattern
        .compile(TOK_LITERAL + "|" + STMT_BRACKET + "|" + STMT_COMMENT + "|" +
                 STMT_VARIABLE_DEREF + "|" + STMT_DIRECTIVE,
                 Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);


    public EztParser() {
        super();
    }


    public List<TemplateNode> parse(TemplateLoaderContext context, String input)
        throws TemplateParserException
    {
        final List<TemplateNode> node_list = new LinkedList<TemplateNode>();
        if (input == null || input.length() == 0)
            return node_list;
        else {
            final Matcher matcher = PATTERN.matcher(input);
            while (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    if (matcher.group(i) != null) {
                        switch (i) {
                        case 1: /* literal string
                                 */
                            node_list.add(new TextNode(matcher.group(i)));
                            break;
                        case 2: /* left bracket ([[])
                                 */
                            node_list.add(new TextNode("["));
                            break;
                        case 3: /* a [# comment]
                                 */
                            break;
                        case 4: /* a [variable]
                                 */
                            parseVariable(node_list, matcher);
                            break;
                        case 5: /* some kind of directive
                                 */
                            parseDirective(node_list, matcher);
                            break;
                        default:
                            break;
                        }
                    }
                }
            }
            return node_list;
        }
    }

    private static void parseVariable(List<TemplateNode> node_list, Matcher matcher)
        throws TemplateParserException
    {
        String text = matcher.group(4);

        if (text.matches("\\[(is|if-any|define|include|insertfile|format)\\]")) {
            throw new TemplateParserException("Dereferencing variable named after reserved word: " +
                                              text);
        }
        else if (text.equals("[end]")) {
            node_list.add(new EztEndNode());
        }
        else if (text.equals("[else]")) {
            node_list.add(new EztElseNode());
        }
        else {
            node_list.add(new VariableNode(matcher.group(4).replaceAll("(^\\[)|(\\]$)", "")));
        }
    }

    private static void parseDirective(List<TemplateNode> node_list, Matcher matcher)
        throws TemplateParserException
    {
        final String directive = matcher.group(6);
        final String one_parameter = matcher.group(8);
        final String quoted_include_parameter = matcher.group(9);
        final String two_parameters = matcher.group(10);

        if (directive.equals("include") || directive.equals("insertfile")) {
            if (one_parameter != null) {
                node_list.add(new EztIncludeNode(one_parameter));
            } else {
                node_list.add(new EztIncludeNode(quoted_include_parameter));
            }
        }
        else if (directive.equals("define")) {
            node_list.add(new EztDefineNode(one_parameter));
        }
        else if (directive.equals("is")) {
            if (two_parameters == null)
                throw new TemplateParserException("[is] requires two parameters.");
            else {
                String[] tokenized = two_parameters.split("\\s+");
                String varname = tokenized[0];
                String value = tokenized[1].replace("\"", "");

                node_list.add(EztConditionalNode.Is(varname, value));
            }
        }
        else if (directive.equals("if-any")) {
            if (one_parameter == null)
                throw new TemplateParserException("[if-any] requires one parameter.");
            else
                node_list.add(EztConditionalNode.IfAny(one_parameter));
        }
        else if (directive.equals("format")) {
            /*
             * encountered a [format] directive; ignoring.
             */
            return;
        }
    }
}

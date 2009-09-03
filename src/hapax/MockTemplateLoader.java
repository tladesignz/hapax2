package hapax;

import hapax.parser.CTemplateParser;
import hapax.parser.TemplateParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements a mock TemplateLoader that can be populated from unit tests.
 *
 * @author dcoker
 * @author jdp
 */
class MockTemplateLoader
    extends Object
    implements TemplateLoader
{

    private final Map<String, String> mock_templates =
        new HashMap<String, String>();


    public MockTemplateLoader() {
        super();
    }


    public void put(String name, String template) {
        this.mock_templates.put(name, template);
    }
    public Template getTemplate(String filename) 
        throws TemplateException
    {
        Template template = new Template(0L, this.mock_templates.get(filename), (new TemplateLoaderContext(this,"mock:")));

        return template;
    }
}

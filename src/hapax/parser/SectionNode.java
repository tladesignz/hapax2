package hapax.parser;

import hapax.TemplateDictionary;
import hapax.TemplateLoaderContext;

import java.io.PrintWriter;

/**
 * Implementation of a {{#SECTION_NODE}} and the paired {{/SECTION_NODE}}.
 *
 * @author dcoker
 */
public final class SectionNode 
    extends TemplateNode
{

    static SectionNode Open(String nodeName) {
        return new SectionNode(nodeName, TYPE.OPEN);
    }
    static SectionNode Close(String nodeName) {
        return new SectionNode(nodeName, TYPE.CLOSE);
    }

    enum TYPE {
        OPEN, CLOSE;
    }


    private final String sectionName_;
    private final TYPE type_;

    volatile int indexOfClose;


    private SectionNode(String nodeName, TYPE node_type) {
        super();
        this.sectionName_ = nodeName;
        this.type_ = node_type;
    }


    public final TemplateType getTemplateType(){
        return TemplateType.TemplateTypeSection;
    }

    public String getSectionName() {
        return sectionName_;
    }

    @Override
    public void evaluate(TemplateDictionary dict, TemplateLoaderContext context,
                         PrintWriter collector)
    {
        // do nothing
    }

    public boolean isOpenSectionTag() {
        return type_ == TYPE.OPEN;
    }

    public boolean isCloseSectionTag() {
        return type_ == TYPE.CLOSE;
    }

    public int getIndexOfClose(){
        return this.indexOfClose;
    }
}

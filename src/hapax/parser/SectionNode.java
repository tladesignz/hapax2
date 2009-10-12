package hapax.parser;

import hapax.TemplateDictionary;
import hapax.TemplateLoader;

import java.io.PrintWriter;

/**
 * Implementation of a {{#SECTION_NODE}} and the paired {{/SECTION_NODE}}.
 *
 * @author dcoker
 */
public final class SectionNode 
    extends TemplateNode
    implements TemplateNode.Section
{

    static SectionNode Open(int lno, String nodeName) {
        return new SectionNode(lno, nodeName, TYPE.OPEN);
    }
    static SectionNode Close(int lno, String nodeName) {
        return new SectionNode(lno, nodeName, TYPE.CLOSE);
    }

    enum TYPE {
        OPEN, CLOSE;
    }


    private final String sectionName_;
    private final TYPE type_;

    volatile int indexOfClose = -1;


    private SectionNode(int lno, String nodeName, TYPE node_type) {
        super(lno);
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
    public void evaluate(TemplateDictionary dict, TemplateLoader context,
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
    public int getIndexOfCloseRelative(){
        int close = this.indexOfClose;
        int open = this.ofs;
        return (close-open);
    }
}

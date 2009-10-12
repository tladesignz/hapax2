package hapax.parser;

import hapax.TemplateDictionary;
import hapax.TemplateException;
import hapax.TemplateLoader;

import java.io.PrintWriter;

/**
 * All tokens in the template language are represented by instances of a
 * TemplateNode.
 *
 * @author dcoker
 * @author jdp
 */
public abstract class TemplateNode {

    public interface Section {

        public String getSectionName();
    }

    /**
     * Primary rendering types.
     * @see hapax.Template#render
     */
    public enum TemplateType {
        TemplateTypeSection,
        TemplateTypeNode
    }
 

    public final int lineNumber;

    volatile int ofs = -1;


    TemplateNode(int lno){
        super();
        this.lineNumber = lno;
    }


    public TemplateType getTemplateType(){
        return TemplateType.TemplateTypeNode;
    }
    public void evaluate(TemplateDictionary dict, TemplateLoader context,
                         PrintWriter collector) 
        throws TemplateException 
    {
    }

}

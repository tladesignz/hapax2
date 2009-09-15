package hapax;

import java.util.List;
import java.util.HashMap;

/**
 * The data dictionary contains the definition of variables, and
 * controls the interpretation of includes and sections.
 * 
 * An include or section that is visible (defined) enters the scope of
 * a child dictionary.  
 * 
 * The child scope of an include or section inherits and overrides the
 * data definitions of variables and sections from its ancestors.
 * 
 * @author dcoker
 * @author jdp
 */
public final class TemplateDictionary
    extends Object
    implements java.lang.Cloneable
{

    /**
     * Creates a top-level TemplateDictionary.
     *
     * @return a new TemplateDictionary
     */
    public static TemplateDictionary create() {
        return new TemplateDictionary();
    }


    private HashMap<String, String> variables = new HashMap<String, String>();

    private HashMap<String, List<TemplateDictionary>> sections = new HashMap<String, List<TemplateDictionary>>();

    private TemplateDictionary parent;


    public TemplateDictionary() {
        super();
    }
    private TemplateDictionary(TemplateDictionary parent) {
        super();
        this.parent = parent;
    }


    /**
     * Permits cloning a variable table, but prohibits cloning a
     * dictionary with any more state.  This is intended as an aid to
     * application configuration that raises exceptions on incorrect
     * data flow.
     */
    public TemplateDictionary clone(){
        if (!this.sections.isEmpty())
            throw new IllegalStateException("Unintended operation, dictionary has sections");
        else if (null != this.parent)
            throw new IllegalStateException("Unintended operation, dictionary is child");
        else {
            try {
                TemplateDictionary clone = (TemplateDictionary)super.clone();
                clone.variables = (HashMap<String, String>)this.variables.clone();
                clone.sections = new HashMap<String, List<TemplateDictionary>>();
                return clone;
            }
            catch (java.lang.CloneNotSupportedException exc){
                throw new java.lang.Error(exc);
            }
        }
    }

    /*
     * Variable API
     */

    public boolean containsVariable(String varName) {

        if (this.variables.containsKey(varName))
            return true;
        else if (this.parent != null)
            return this.parent.containsVariable(varName);
        else
            return false;
    }

    public String getVariable(String varName) {

        String value = this.variables.get(varName);

        if (null != value)

            return value;

        else if (this.parent != null) 

            return this.parent.getVariable(varName);
        else 
            return "";
    }
    public void putVariable(String varName, String val) {

        this.variables.put(varName, val);
    }
    public void putVariable(String varName, int val) {

        this.putVariable(varName, String.valueOf(val));
    }
    public void setVariable(String varName, String val) {

        this.variables.put(varName, val);
    }
    public void setVariable(String varName, int val) {

        this.putVariable(varName, String.valueOf(val));
    }

    /*
     * Section API
     */

    public boolean isSectionHidden(String sectionName) {

        return (!this.sections.containsKey(sectionName));
    }
    public boolean isSectionVisible(String sectionName) {

        return (this.sections.containsKey(sectionName));
    }
    public boolean hasSection(String sectionName){

        return (this.sections.containsKey(sectionName));
    }
    /**
     * @return a list of TemplateDictionaries that iterate the
     * section, or null for a section not visible.
     */
    public List<TemplateDictionary> getSection(String sectionName) {

        List<TemplateDictionary> list = this.sections.get(sectionName);
        if (null != list)
            return list;
        else {
            TemplateDictionary parent = this.parent;
            if (null != parent)
                /*
                 * Permits {{>name01}} 
                 *   containing {{#name01}} <section> {{/name01}}
                 * 
                 * to work when the section data is not
                 * 
                 *    (dictionary name01[1])/(dictionary name01[N]),
                 * 
                 * but only
                 * 
                 *    (dictionary name01[1]).
                 * 
                 * As for variables.
                 */
                return parent.getSection(sectionName);
            else
                return null;
        }
    }
    /**
     * An aid to usage
     * @param from Embedded section or include name
     * @param to Target template name
     */
    public List<TemplateDictionary> getSection(String from, String to) {
        return this.getSection(from);
    }
    public TemplateDictionary addSection(String sectionName) {

        TemplateDictionary add = new TemplateDictionary(this);

        List<TemplateDictionary> section = this.sections.get(sectionName);
        if (null == section){
            section = new java.util.ArrayList<TemplateDictionary>();
            this.sections.put(sectionName, section);
        }

        section.add(add);
        return add;
    }
    /**
     * An aid to usage
     * @param from Embedded section or include name
     * @param to Target template name
     */
    public TemplateDictionary addSection(String from, String to) {
        this.setVariable(from,to);
        return this.addSection(from);
    }
    /**
     * @return A section data list having at least one section
     * iteration data dictionary.
     */
    public List<TemplateDictionary> showSection(String sectionName) {

        List<TemplateDictionary> section = this.sections.get(sectionName);
        if (null == section){
            section = new java.util.ArrayList<TemplateDictionary>();
            TemplateDictionary show = new TemplateDictionary(this);
            section.add(show);
            this.sections.put(sectionName, section);
        }
        return section;
    }
    /**
     * An aid to usage
     * @param from Embedded section or include name
     * @param to Target template name
     */
    public List<TemplateDictionary> showSection(String from, String to){

        return this.showSection(from);
    }
    public void hideSection(String sectionName) {

        this.sections.remove(sectionName);
    }
    /**
     * An aid to usage
     * @param from Embedded section or include name
     * @param to Target template name
     */
    public void hideSection(String from, String to){

        this.sections.remove(from);
    }
}

package hapax;

import java.util.List;
import java.util.HashMap;

/**
 * Stateful eval scope in a nested dictionary.  
 * 
 * To initialize the eval state, put name - value mappings for
 * template variables, filenames from included names, and a list of
 * sections that have been explicitly shown into a new dictionary.
 * 
 * Enable sections with {@link showSection}, or subsequently disable
 * shown sections with {@link hideSection}.
 *
 * Create section- repeating child dictionaries with {@link
 * #addSection} for each repeated section.
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

    public boolean containsVariable(String varName) {
        varName = varName.toLowerCase();

        if (this.variables.containsKey(varName))
            return true;
        else if (this.parent != null)
            return this.parent.containsVariable(varName);
        else
            return false;
    }

    public String getVariable(String varName) {
        varName = varName.toLowerCase();

        if (this.variables.containsKey(varName))

            return this.variables.get(varName);

        else if (this.parent != null) 

            return this.parent.getVariable(varName);
        else 
            return "";
    }
    public void putVariable(String varName, String val) {
        this.variables.put(varName.toLowerCase(), val);
    }
    public void putVariable(String varName, int val) {
        this.putVariable(varName, String.valueOf(val));
    }
    public boolean ifAny(String name){
        String value = this.getVariable(name);
        return (null != value && 0 != value.length());
    }
    public boolean isEq(String name, String value){
        String var = this.getVariable(name);
        if (null != var && 0 != var.length())
            return var.equals(value);
        else
            return false;
    }

    public boolean isSectionHidden(String sectionName) {
        sectionName = sectionName.toLowerCase();

        return (!this.sections.containsKey(sectionName));
    }
    public boolean isSectionVisible(String sectionName) {
        sectionName = sectionName.toLowerCase();

        return (this.sections.containsKey(sectionName));
    }
    public boolean hasSection(String sectionName){
        sectionName = sectionName.toLowerCase();

        return (this.sections.containsKey(sectionName));
    }
    /**
     * @return a list of TemplateDictionaries that iterate the
     * section, or null for a section not visible.
     */
    public List<TemplateDictionary> getSection(String sectionName) {
        sectionName = sectionName.toLowerCase();

        List<TemplateDictionary> list = this.sections.get(sectionName);
        if (null != list)
            return list;
        else 
            return null;
    }
    public TemplateDictionary addSection(String sectionName) {
        sectionName = sectionName.toLowerCase();

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
     * @return A section data list having at least one section
     * iteration data dictionary.
     */
    public List<TemplateDictionary> showSection(String sectionName) {
        sectionName = sectionName.toLowerCase();

        List<TemplateDictionary> section = this.sections.get(sectionName);
        if (null == section){
            section = new java.util.ArrayList<TemplateDictionary>();
            TemplateDictionary show = new TemplateDictionary(this);
            section.add(show);
            this.sections.put(sectionName, section);
        }
        return section;
    }
    public void hideSection(String sectionName) {
        sectionName = sectionName.toLowerCase();

        this.sections.remove(sectionName);
    }

}

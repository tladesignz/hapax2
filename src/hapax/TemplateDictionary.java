package hapax;

import java.util.*;

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
 * #addChildDict} for each named section and repeated value set.
 * 
 * @author dcoker
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


    private HashMap<String, String> table = new HashMap<String, String>();

    private HashMap<String, List<TemplateDictionary>> children = new HashMap<String, List<TemplateDictionary>>();

    private HashMap<String,Boolean> shown = new HashMap<String,Boolean>();

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
        if (!this.children.isEmpty())
            throw new IllegalStateException("Unintended operation, dictionary has children");
        else if (!this.shown.isEmpty())
            throw new IllegalStateException("Unintended operation, dictionary has sections");
        else if (null != this.parent)
            throw new IllegalStateException("Unintended operation, dictionary is child");
        else {
            try {
                TemplateDictionary clone = (TemplateDictionary)super.clone();
                clone.table = (HashMap<String, String>)this.table.clone();
                clone.children = new HashMap<String, List<TemplateDictionary>>();
                clone.shown = new HashMap<String,Boolean>();
                return clone;
            }
            catch (java.lang.CloneNotSupportedException exc){
                throw new java.lang.Error(exc);
            }
        }
    }
    /**
     * Puts a String value into the dictionary.
     *
     * @param key The key for this value
     * @param val The value
     */
    public void put(String key, String val) {
        this.table.put(key.toUpperCase(), val);
    }

    /**
     * Puts an integer value into the dictionary.  All values are normalized to
     * Strings.
     *
     * @param key The key for this value.
     * @param val The value
     */
    public void put(String key, int val) {
        this.put(key, String.valueOf(val));
    }

    /**
     * Returns true if the Dictionary (or any parent dictionaries) contains the
     * requested key.
     *
     * @param key The key to look for
     *
     * @return True if the dictionary (or any parent dictionary) contains the key,
     *         false otherwise.
     */
    public boolean contains(String key) {
        key = key.toUpperCase();

        if (table.containsKey(key))
            return true;
        else if (this.parent != null)
            return this.parent.contains(key);
        else
            return false;
    }

    /**
     * Gets the value of a given dictionary key.
     *
     * @param key The name of the dictionary item to return.
     *
     * @return The value of the requested dictionary item, or empty string.
     */
    public String get(String key) {
        key = key.toUpperCase();

        if (table.containsKey(key))

            return table.get(key);

        else if (this.parent != null) 

            return this.parent.get(key);
        else 
            return "";
    }

    /**
     * Gets a list of the child dictionaries with a given name.
     *
     * @param key The name of the child dictionaries to retreive.
     *
     * @return a list of TemplateDictionaries that are children to this
     *         dictionary
     */
    public List<TemplateDictionary> getChildDicts(String key) {
        key = key.toUpperCase();

        if (children.containsKey(key))
            return children.get(key);
        else 
            return Collections.emptyList();
    }

    /**
     * Adds a dictionary with a given name.  The name should correspond to the
     * name of an included template or to the name of a section.
     *
     * If there are multiple dictionaries with the same name present AND
     * showSection() has been called, the section will be repeated once for each
     * dictionary.
     *
     * @param key The name of the child dictionary to create.
     *
     * @return a new TemplateDictionary
     */
    public TemplateDictionary addChildDict(String key) {
        key = key.toUpperCase();
        TemplateDictionary td = new TemplateDictionary(this);

        if (!children.containsKey(key)) {
            List<TemplateDictionary> dicts = new LinkedList<TemplateDictionary>();
            dicts.add(td);
            children.put(key, dicts);
        }
        else 
            children.get(key).add(td);

        return td;
    }

    /**
     * Creates a child dictionary and shows the section.  This is equivalent to
     * calling addChildDict() and showSection() separately.
     *
     * @param section_name The name of the dictionary and section to show
     *
     * @return The child dictionary
     */
    public TemplateDictionary addChildDictAndShowSection(String section_name) {
        this.showSection(section_name);
        return this.addChildDict(section_name);
    }

    /**
     * Hides a section from being visible.  Sections are hidden by default.
     *
     * @param section The section to hide.
     */
    public void hideSection(String section) {
        this.shown.put(section,Boolean.FALSE);
    }
    /**
     * Shows a section.  Sections are shown by default.  When this method is
     * called, the section will be displayed once for each child dictionary of the
     * same name or once if there are no child dictionaries of the same name.
     *
     * @param section The section to show.
     */
    public void showSection(String section) {
        this.shown.put(section,Boolean.TRUE);
    }
    /**
     * Called from {@link Template} render section node.
     */
    public boolean isSectionHidden(String sectionName) {
        Boolean value = this.shown.get(sectionName);
        if (null == value)
            return false;
        else
            return (!value.booleanValue());
    }
    public boolean isSectionVisible(String sectionName) {
        Boolean value = this.shown.get(sectionName);
        if (null == value)
            return true;
        else
            return value.booleanValue();
    }

}

package hapax;

/**
 * Section and include iterations (one or many) are evaluated with
 * special sections shown and hidden during their interpretation, as
 * defined here.
 * 
 * <dl>
 * 
 * <dt> <i>name</i><code>_it_First</code> </dt> <dd> A section only
 * visible in the first iteration. </dd>
 * 
 * <dt> <i>name</i><code>_it_NotFirst</code> </dt> <dd> A section only
 * visible after the first iteration. </dd>
 * 
 * <dt> <i>name</i><code>_it_Last</code> </dt> <dd> A section only
 * visible in the last iteration. </dd>
 * 
 * <dt> <i>name</i><code>_it_NotLast</code> </dt> <dd> A section only
 * visible before the first iteration. </dd>
 * 
 * <dt> <i>name</i><code>_it_Exclusive</code> </dt> <dd> A section only
 * visible after the first iteration and before the last
 * iteration. </dd>
 * 
 * </dl>
 * 
 * 
 * @author jdp
 */
public class Iterator 
    extends Object
{
    public final static class Suffix {
        public final static String First     = "_it_First";
        public final static String NotFirst  = "_it_NotFirst";
        public final static String Last      = "_it_Last";
        public final static String NotLast   = "_it_NotLast";
        public final static String Exclusive = "_it_Exclusive";
    }

    public final static void Define(TemplateDictionary dict, String sectionName, int cc, int count){
        if (0 == cc){
            dict.showSection(sectionName+Suffix.First);
            dict.hideSection(sectionName+Suffix.NotFirst);
            dict.hideSection(sectionName+Suffix.Last);
            dict.showSection(sectionName+Suffix.NotLast);
            dict.hideSection(sectionName+Suffix.Exclusive);
        }
        else if (cc == (count-1)){
            dict.hideSection(sectionName+Suffix.First);
            dict.showSection(sectionName+Suffix.NotFirst);
            dict.showSection(sectionName+Suffix.Last);
            dict.hideSection(sectionName+Suffix.NotLast);
            dict.hideSection(sectionName+Suffix.Exclusive);
        }
        else {
            dict.hideSection(sectionName+Suffix.First);
            dict.showSection(sectionName+Suffix.NotFirst);
            dict.hideSection(sectionName+Suffix.Last);
            dict.showSection(sectionName+Suffix.NotLast);
            dict.showSection(sectionName+Suffix.Exclusive);
        }
    }

}

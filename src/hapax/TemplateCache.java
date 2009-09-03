package hapax;

import hapax.parser.TemplateParser;
import hapax.parser.CTemplateParser;
import hapax.parser.EztParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * An in-memory cache of parsed {@link Templates} intended to be
 * shared across threads.
 *
 * @author dcoker
 * @author jdp
 */
public class TemplateCache
    extends Object
    implements TemplateLoader
{

    /**
     * Creates a TemplateLoader for the default CTemplate language
     * with embedded EZT language.
     */
    public static TemplateLoader create(String base_path)
    {
        return new TemplateCache(base_path);
    }
    /**
     * Creates a TemplateLoader using the EZT parser exclusively.
     */
    public static TemplateLoader createForEzt(String base_path)
    {
        return new TemplateCache(base_path, EztParser.Instance);
    }
    /**
     * Creates a TemplateLoader using the argument parser.
     */
    public static TemplateLoader createForParser(String base_path, TemplateParser parser)
    {
        return new TemplateCache(base_path, parser);
    }


    private final Map<String, Template> cache = new HashMap<String, Template>();

    private final String baseDir;

    private final TemplateParser parser;


    public TemplateCache(String baseDir){
        super();
        this.baseDir = baseDir;
        this.parser = null;
    }
    public TemplateCache(String baseDir, TemplateParser parser){
        super();
        this.baseDir = baseDir;
        this.parser = parser;
    }


    /**
     * Parses and fetches a template from disk.
     *
     * @param filename The path to the template, relative to the templateDirectory
     *                 passed to the ctor of TemplateCache.
     */
    public final Template getTemplate(String filename)
        throws TemplateException
    {
        filename = Path.toFile(this.baseDir, filename);

        File file = new File(filename);
        /*
         * stat fs once on cache hit
         */
        long fileLast = file.lastModified();

        Template template = this.hitCache(filename, fileLast);

        if (null != template)

            return template;

        else {
            String contents = null;
            FileReader reader = null;
            try {
                reader = new FileReader(file);

                contents = this.readToString(reader);
            }
            catch (IOException e) {
                throw new TemplateException(e);
            }
            finally {
                if (null != reader){
                    try {
                        reader.close();
                    }
                    catch (IOException ignore){
                    }
                }
            }
            TemplateLoaderContext context = new TemplateLoaderContext(this, file.getParent());
            TemplateParser parser = this.parser;
            if (null == parser)
                template = new Template(fileLast, contents, context);
            else
                template = new Template(fileLast, parser, contents, context);

            synchronized(this.cache){
                this.cache.put(filename,template);
            }
            return template;
        }
    }

    private final static String readToString(Reader in)
        throws IOException
    {
        StringBuilder string = new StringBuilder();
        char[] buf = new char[0x200];
        int read;
        while (0 < (read = in.read(buf,0,0x200))){
            string.append(buf,0,read);
        }
        return string.toString();
    }

    private final Template hitCache(String filename, long fileLast)
    {
        Template template = this.cache.get(filename);
        if (null != template){
            long templateLast = template.getLastModified();
            if (templateLast >= fileLast)
                return template;
        }
        return null;
    }
}

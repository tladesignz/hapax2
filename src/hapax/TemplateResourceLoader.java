/*
 * Hapax2 Resource Loader
 * Copyright (c) 2007 Doug Coker
 * Copyright (c) 2009 John Pritchard
 * Copyright (c) 2010 Alan Stewart
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hapax;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import hapax.parser.TemplateParser;

/**
 * <p>
 * An in-memory cache of parsed {@link Template}s intended to be shared across
 * threads.
 * </p>
 * Templates are loaded from the classpath.
 * </p>
 *
 * @author Alan Stewart (alankstewart@gmail.com)
 */
public class TemplateResourceLoader implements TemplateLoader {

    private static final Map<String, Template> mCache = new LinkedHashMap<String, Template>();

    protected final String mBaseDir;

    protected final TemplateParser mParser;

    /**
     * Creates a TemplateLoader for CTemplate language
     *
     * @param basePath
     * @return
     */
    public static TemplateLoader create(String basePath) {
        return new TemplateResourceLoader(basePath);
    }

    /**
     * Creates a TemplateLoader using the argument parser.
     *
     * @param basePath
     * @param parser
     * @return
     */
    public static TemplateLoader createForParser(String basePath, TemplateParser parser) {
        return new TemplateResourceLoader(basePath, parser);
    }

    /**
     * @param baseDir
     */
    public TemplateResourceLoader(String baseDir) {
        this(baseDir, null);
    }

    public TemplateResourceLoader() {
        this("", null);
    }

    /**
     * @param baseDir
     * @param parser
     */
    public TemplateResourceLoader(String baseDir, TemplateParser parser) {
        mBaseDir = baseDir;
        mParser = parser;
    }

    /**
     * @return the root template directory.
     */
    @Override
    public String getTemplateDirectory() {
        return mBaseDir;
    }

    /**
     * Loads a template with the given name in the
     * {@link #getTemplateDirectory()} using a {@link ClassLoader} and the
     * filename as cache key.
     *
     * @param resource
     *            The filename of the template without the "xtm" extension.
     * @return the {@link Template}.
     * @throws TemplateException
     */
    @Override
    public Template getTemplate(String resource) throws TemplateException {
        return getTemplate(new TemplateLoader.Context(this, mBaseDir), resource);
    }

    /**
     * Loads a template with the given name in the
     * {@link #getTemplateDirectory()} using a {@link ClassLoader} and the
     * filename as cache key.
     *
     * @param context
     * @param resource
     *            The filename of the template without the "xtm" extension.
     * @return the {@link Template}.
     * @throws TemplateException
     */
    @Override
    public Template getTemplate(TemplateLoader context, String resource) throws TemplateException {
        if (!resource.endsWith(".xtm")) resource += ".xtm";

        String templatePath = mBaseDir + resource;

        if (mCache.containsKey(templatePath)) return mCache.get(templatePath);

        InputStream is = getClass().getClassLoader().getResourceAsStream(templatePath);
        if (is == null) {
            is = getClass().getClassLoader().getResourceAsStream(resource);
            if (is == null)
                throw new TemplateException("Template " + templatePath + " could not be found");
        }

        return getTemplate(context, templatePath, is);
    }

    /**
     * Loads a template from a given {@link InputStream} using the given name as
     * cache key.
     *
     * @param context
     * @param name
     *            The template name used as cache key.
     * @param is
     *            The {@link InputStream} to read from.
     * @return the {@link Template}.
     * @throws TemplateException
     */
    public Template getTemplate(TemplateLoader context, String name, InputStream is)
        throws TemplateException {
        String contents = null;

        try {
            contents = copyToString(new InputStreamReader(is));
        } catch (IOException e) {
            throw new TemplateException(e);
        }

        Template template = mParser == null ? new Template(contents, context)
            : new Template(mParser, contents, context);

        template.hashCode();

        synchronized (mCache) {
            mCache.put(name, template);
        }

        return template;
    }

    /**
     * Loads a template from a given {@link InputStream} using the given name as
     * cache key.
     *
     * @param name
     *            The template name used as cache key.
     * @param is
     *            The {@link InputStream} to read from.
     * @return the {@link Template}.
     * @throws TemplateException
     */
    public Template getTemplate(String name, InputStream is) throws TemplateException {
        return getTemplate(new TemplateLoader.Context(this, mBaseDir), name, is);
    }

    /**
     * @param in
     * @return
     * @throws IOException
     */
    private String copyToString(Reader in) throws IOException {
        StringWriter out = new StringWriter();
        try {
            char[] buffer = new char[4096];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } finally {
            try {
                in.close();
            } catch (IOException ignore) {
            }
            try {
                out.close();
            } catch (IOException ignore) {
            }
        }
        return out.toString();
    }
}

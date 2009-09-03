package hapax.parser;

import hapax.TemplateException;

/**
 * Thrown when templates include templates that include previously rendered
 * templates.
 *
 * @author dcoker
 */
public class CyclicIncludeException extends TemplateException {

    public CyclicIncludeException(String message) {
        super(message);
    }
}

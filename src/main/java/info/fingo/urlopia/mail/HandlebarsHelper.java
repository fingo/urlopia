package info.fingo.urlopia.mail;

import com.github.jknack.handlebars.Options;

import java.io.IOException;

/**
 * @author Tomasz Urbas
 */
public class HandlebarsHelper {
    public String ifeq(int val, int val2, Options options) throws IOException {
        if (val == val2) {
            return options.fn().toString().trim();
        }
        return "";
    }
}

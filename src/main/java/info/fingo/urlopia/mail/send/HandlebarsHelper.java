package info.fingo.urlopia.mail.send;

import com.github.jknack.handlebars.Options;

import java.io.IOException;

public class HandlebarsHelper {
    public String ifeq(Object val, String val2, Options options) throws IOException {
        if (val.equals(val2)) {
            return options.fn().toString().trim();
        }
        return "";
    }
}

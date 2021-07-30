package info.fingo.urlopia.config.mail.send;

import com.github.jknack.handlebars.Options;

import java.io.IOException;

public class HandlebarsHelper {
    public String ifeq(Object val, String val2, Options options) throws IOException {
        return val.equals(val2) 
                ? options.fn().toString().trim() 
                : "";
    }
}

package info.fingo.urlopia.ad;

import org.mockito.Mockito;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;

/**
 * Factory for MockNamingEnumeration
 * Created by Jakub Licznerski on 21.10.2016.
 */
public class MockInitialDirContextFactory implements InitialContextFactory {
    private static DirContext mockContext = null;

    /**
     * Returns the last DirContext (which is a Mockito mock) retrieved from this factory.
     */
    public static DirContext getLatestMockContext() {
        return mockContext;
    }

    public Context getInitialContext(Hashtable environment) throws NamingException {
        synchronized (MockInitialDirContextFactory.class) {
            mockContext = (DirContext) Mockito.mock(DirContext.class);
        }
        return mockContext;
    }
}
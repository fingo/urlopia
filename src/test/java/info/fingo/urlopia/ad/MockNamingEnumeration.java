package info.fingo.urlopia.ad;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;
import java.util.LinkedList;

/**
 * Mock for LDAP resources enumeration
 * Created by Jakub Licznerski on 21.10.2016.
 */
public class MockNamingEnumeration implements NamingEnumeration {

    private LinkedList<SearchResult> results;


    public MockNamingEnumeration() {
        super();
        setup();
    }

    private void setup() {
        results = new LinkedList<>();

        BasicAttributes attr = new BasicAttributes(true);
        attr.put("mail", "example@com");
        attr.put("userPrincipalName", "Sample Name");
        attr.put("sn", "Sample Name");
        attr.put("memberOf", "CN=SampleGroup,OU=Users,DC=example,DC=com");
        attr.put("givenname", "Sample Name");
        attr.put("distinguishedName", "CN=Sample Team");

        results.push(new SearchResult("Mock User 1", null, attr));
        results.push(new SearchResult("Mock User 2", null, attr));
        results.push(new SearchResult("Mock User 3", null, attr));
        results.push(new SearchResult("Mock User 4", null, attr));
        results.push(new SearchResult("Mock User 5", null, attr));
    }

    public void close() throws NamingException {
        setup();
    }

    public boolean hasMore() throws NamingException {
        return hasMoreElements();
    }

    public Object next() throws NamingException {
        return nextElement();
    }

    public boolean hasMoreElements() {
        return !results.isEmpty();
    }

    public Object nextElement() {
        return results.pop();
    }
}

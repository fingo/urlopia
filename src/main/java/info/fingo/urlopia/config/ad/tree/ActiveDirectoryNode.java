package info.fingo.urlopia.config.ad.tree;

import info.fingo.urlopia.config.ad.ActiveDirectoryUtils;
import info.fingo.urlopia.config.ad.Attribute;

import javax.naming.directory.SearchResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ActiveDirectoryNode {

    private final String relativeDN;
    private final SearchResult object;
    private final Map<String, ActiveDirectoryNode> children;

    protected ActiveDirectoryNode(SearchResult object) {
        this.relativeDN = getRDN(object);
        this.object = object;
        this.children = new HashMap<>();
    }

    protected ActiveDirectoryNode(String distinguishedName) {
        this.relativeDN = getRDN(distinguishedName);
        this.object = null;
        this.children = new HashMap<>();
    }

    public void add(ActiveDirectoryNode child) {
        children.put(child.relativeDN, child);
    }

    public Optional<ActiveDirectoryNode> getChild(String childRelativeDistinguishedName) {
        return Optional.ofNullable(children.get(childRelativeDistinguishedName));
    }

    private static String getRDN(SearchResult object) {
        var distinguishedName = ActiveDirectoryUtils.pickAttribute(object, Attribute.DISTINGUISHED_NAME);
        return getRDN(distinguishedName);
    }

    private static String getRDN(String distinguishedName) {
        return distinguishedName.split(",", 2)[0];
    }

    public List<SearchResult> getDirectChildrenObjects() {
        return children.values().stream()
                .map(child -> child.object)
                .toList();
    }

    public SearchResult getObject() {
        return object;
    }

}

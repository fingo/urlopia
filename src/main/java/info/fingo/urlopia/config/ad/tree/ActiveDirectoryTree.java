package info.fingo.urlopia.config.ad.tree;

import info.fingo.urlopia.config.ad.ActiveDirectoryUtils;
import info.fingo.urlopia.config.ad.Attribute;

import javax.naming.directory.SearchResult;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ActiveDirectoryTree {

    private final String mainContainerDn;
    private final ActiveDirectoryNode root;

    public ActiveDirectoryTree(String mainContainerDn) {
        this.mainContainerDn = mainContainerDn;
        this.root = new ActiveDirectoryNode(mainContainerDn);
    }

    public void put(SearchResult object) {
        var childDn = ActiveDirectoryUtils.pickAttribute(object, Attribute.DISTINGUISHED_NAME);
        var parentDn = ActiveDirectoryUtils.getParentDN(childDn);
        var parentNode = searchNode(parentDn);
        parentNode.ifPresentOrElse(
                pNode -> {
                    var childNode = new ActiveDirectoryNode(object);
                    pNode.add(childNode);
                },
                () -> {
                    throw ActiveDirectoryTreeException.missingParent(childDn);
                }
        );
    }

    public List<SearchResult> searchDirectChildrenObjectsOf(String distinguishedName) {
        return searchNode(distinguishedName)
                .map(ActiveDirectoryNode::getDirectChildrenObjects)
                .orElse(List.of());
    }

    public Optional<SearchResult> search(String distinguishedName) {
        return searchNode(distinguishedName).map(ActiveDirectoryNode::getObject);
    }

    private Optional<ActiveDirectoryNode> searchNode(String distinguishedName) {
        var relativeDn = ActiveDirectoryUtils.getRelativeDN(distinguishedName, mainContainerDn);
        if (relativeDn.isBlank()) {
            return Optional.of(root);
        }
        var commasIgnoringEscapedRegex = "(?<!\\\\),";
        var dnParts = Arrays.stream(relativeDn.split(commasIgnoringEscapedRegex)).toList();
        return searchNode(root, dnParts);
    }

    private Optional<ActiveDirectoryNode> searchNode(ActiveDirectoryNode node,
                                                     List<String> dnParts) {
        if (dnParts.isEmpty()) {
            return Optional.of(node);
        }
        var topDnPartIdx = dnParts.size() - 1;
        var topDnPart = dnParts.get(topDnPartIdx);
        var topNode = node.getChild(topDnPart);
        var bottomDnParts = dnParts.stream()
                .limit(topDnPartIdx)
                .toList();
        return topNode.flatMap(n -> searchNode(n, bottomDnParts));
    }

}

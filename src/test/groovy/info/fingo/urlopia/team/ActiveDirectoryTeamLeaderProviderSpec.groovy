package info.fingo.urlopia.team

import info.fingo.urlopia.config.ad.tree.ActiveDirectoryTree
import info.fingo.urlopia.user.User
import info.fingo.urlopia.user.UserRepository
import spock.lang.Specification

import javax.naming.directory.Attribute
import javax.naming.directory.Attributes
import javax.naming.directory.SearchResult

class ActiveDirectoryTeamLeaderProviderSpec extends Specification {

    def userRepository = Mock(UserRepository)
    def activeDirectoryTeamLeaderProvider = new ActiveDirectoryTeamLeaderProvider(userRepository)

    def "getTeamLeader WHEN ad team has direct leader SHOULD return it"() {
        given:
        def base = "OU=Teams,DC=fingo,DC=info"

        and:
        def parentTeam = Mock(SearchResult)
        def parentTeamAttributes = Mock(Attributes)

        def parentTeamDN = ": OU=ParentTeam,OU=Teams,DC=fingo,DC=info"
        def parentTeamDNObj = Mock(Attribute)
        parentTeamDNObj.toString() >> parentTeamDN

        parentTeamAttributes.get(info.fingo.urlopia.config.ad.Attribute.DISTINGUISHED_NAME.getKey()) >> parentTeamDNObj
        parentTeam.getAttributes() >> parentTeamAttributes

        and:
        def childTeam = Mock(SearchResult)
        def childTeamAttributes = Mock(Attributes)

        def childTeamDN = ": OU=ChildTeam,OU=ParentTeam,OU=Teams,DC=fingo,DC=info"
        def childTeamDNObj = Mock(Attribute)
        childTeamDNObj.toString() >> childTeamDN

        def managedBy = ": CN=SomeUser,OU=ChildTeam,OU=ParentTeam,OU=Teams,DC=fingo,DC=info"
        def managedByObj = Mock(Attribute)
        managedByObj.toString() >> managedBy

        childTeamAttributes.get(info.fingo.urlopia.config.ad.Attribute.DISTINGUISHED_NAME.getKey()) >> childTeamDNObj
        childTeamAttributes.get(info.fingo.urlopia.config.ad.Attribute.MANAGED_BY.getKey()) >> managedByObj
        childTeam.getAttributes() >> childTeamAttributes

        and:
        def adTeamsTree = new ActiveDirectoryTree(base)
        adTeamsTree.put(parentTeam)
        adTeamsTree.put(childTeam)

        and:
        def user = Mock(User)
        userRepository.findFirstByAdName(managedBy.substring(managedBy.indexOf(':') + 2)) >> Optional.of(user)

        when:
        var teamDN = childTeamDN.substring(childTeamDN.indexOf(':') + 2)
        def result = activeDirectoryTeamLeaderProvider.getTeamLeader(teamDN, adTeamsTree)

        then:
        result.get() == user
    }

    def "getTeamLeader WHEN ad team has no direct leader SHOULD return leader from upper team"() {
        given:
        def base = "OU=Teams,DC=fingo,DC=info"

        and:
        def parentTeam = Mock(SearchResult)
        def parentTeamAttributes = Mock(Attributes)

        def parentTeamDN = ": OU=ParentTeam,OU=Teams,DC=fingo,DC=info"
        def parentTeamDNObj = Mock(Attribute)
        parentTeamDNObj.toString() >> parentTeamDN

        def managedBy = ": CN=SomeUser,OU=ParentTeam,OU=Teams,DC=fingo,DC=info"
        def managedByObj = Mock(Attribute)
        managedByObj.toString() >> managedBy

        parentTeamAttributes.get(info.fingo.urlopia.config.ad.Attribute.DISTINGUISHED_NAME.getKey()) >> parentTeamDNObj
        parentTeamAttributes.get(info.fingo.urlopia.config.ad.Attribute.MANAGED_BY.getKey()) >> managedByObj
        parentTeam.getAttributes() >> parentTeamAttributes

        and:
        def childTeam = Mock(SearchResult)
        def childTeamAttributes = Mock(Attributes)

        def childTeamDN = ": OU=ChildTeam,OU=ParentTeam,OU=Teams,DC=fingo,DC=info"
        def childTeamDNObj = Mock(Attribute)
        childTeamDNObj.toString() >> childTeamDN

        childTeamAttributes.get(info.fingo.urlopia.config.ad.Attribute.DISTINGUISHED_NAME.getKey()) >> childTeamDNObj
        childTeam.getAttributes() >> childTeamAttributes

        and:
        def adTeamsTree = new ActiveDirectoryTree(base)
        adTeamsTree.put(parentTeam)
        adTeamsTree.put(childTeam)

        and:
        def user = Mock(User)
        userRepository.findFirstByAdName(managedBy.substring(managedBy.indexOf(':') + 2)) >> Optional.of(user)

        when:
        var teamDN = childTeamDN.substring(childTeamDN.indexOf(':') + 2)
        def result = activeDirectoryTeamLeaderProvider.getTeamLeader(teamDN, adTeamsTree)

        then:
        result.get() == user
    }

    def "getTeamLeader WHEN there is no leader in structure SHOULD return empty"() {
        given:
        def base = "OU=Teams,DC=fingo,DC=info"

        and:
        def parentTeam = Mock(SearchResult)
        def parentTeamAttributes = Mock(Attributes)

        def parentTeamDN = ": OU=ParentTeam,OU=Teams,DC=fingo,DC=info"
        def parentTeamDNObj = Mock(Attribute)
        parentTeamDNObj.toString() >> parentTeamDN

        parentTeamAttributes.get(info.fingo.urlopia.config.ad.Attribute.DISTINGUISHED_NAME.getKey()) >> parentTeamDNObj
        parentTeam.getAttributes() >> parentTeamAttributes

        and:
        def childTeam = Mock(SearchResult)
        def childTeamAttributes = Mock(Attributes)

        def childTeamDN = ": OU=ChildTeam,OU=ParentTeam,OU=Teams,DC=fingo,DC=info"
        def childTeamDNObj = Mock(Attribute)
        childTeamDNObj.toString() >> childTeamDN

        childTeamAttributes.get(info.fingo.urlopia.config.ad.Attribute.DISTINGUISHED_NAME.getKey()) >> childTeamDNObj
        childTeam.getAttributes() >> childTeamAttributes

        and:
        def adTeamsTree = new ActiveDirectoryTree(base)
        adTeamsTree.put(parentTeam)
        adTeamsTree.put(childTeam)

        and:
        def user = Mock(User)
        userRepository.findFirstByAdName(_ as String) >> Optional.of(user)

        when:
        var teamDN = childTeamDN.substring(childTeamDN.indexOf(':') + 2)
        def result = activeDirectoryTeamLeaderProvider.getTeamLeader(teamDN, adTeamsTree)

        then:
        result.isEmpty()
    }
}

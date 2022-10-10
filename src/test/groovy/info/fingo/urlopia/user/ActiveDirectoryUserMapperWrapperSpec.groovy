package info.fingo.urlopia.user

import info.fingo.urlopia.api.v2.history.DetailsChangeEventInput
import info.fingo.urlopia.history.HistoryLogService
import spock.lang.Specification

import javax.naming.directory.SearchResult

class ActiveDirectoryUserMapperWrapperSpec extends Specification {

    def historyLogService = Mock(HistoryLogService)
    def activeDirectoryUserMapper = Mock(ActiveDirectoryUserMapper)
    def ADUserMapper = new ActiveDirectoryUserMapperWrapper(historyLogService, activeDirectoryUserMapper)

    def "updateUser WHEN user become ec SHOULD save event about it"() {
        given:
        def mockedUser = Mock(User) {
            getEc() >> false
            getB2b() >> false
            isActive() >> false
        }
        def mockedUpdatedUser = Mock(User) {
            getEc() >> true
            getB2b() >> false
            isActive() >> false
        }
        def searchResult = Mock(SearchResult)
        activeDirectoryUserMapper.mapToUser(searchResult, _ as User) >> mockedUpdatedUser
        historyLogService.addNewDetailsChangeEvent(_ as DetailsChangeEventInput) >>
                { DetailsChangeEventInput detailsChangeEventInput -> detailsChangeEventInput }


        when:
        ADUserMapper.updateUser(searchResult, mockedUser)

        then:
        1 * historyLogService.addNewDetailsChangeEvent(_ as DetailsChangeEventInput)
    }

    def "updateUser WHEN user become b2b SHOULD save event about it"() {
        given:
        def mockedUser = Mock(User) {
            getEc() >> false
            getB2b() >> false
            isActive() >> false
        }
        def mockedUpdatedUser = Mock(User) {
            getEc() >> false
            getB2b() >> true
            isActive() >> false
        }
        def searchResult = Mock(SearchResult)
        activeDirectoryUserMapper.mapToUser(searchResult, _ as User) >> mockedUpdatedUser
        historyLogService.addNewDetailsChangeEvent(_ as DetailsChangeEventInput) >>
                { DetailsChangeEventInput detailsChangeEventInput -> detailsChangeEventInput }


        when:
        ADUserMapper.updateUser(searchResult, mockedUser)

        then:
        1 * historyLogService.addNewDetailsChangeEvent(_ as DetailsChangeEventInput)
    }

    def "updateUser WHEN user become active SHOULD save event about it"() {
        given:
        def mockedUser = Mock(User) {
            getEc() >> false
            getB2b() >> false
            isActive() >> false
        }
        def mockedUpdatedUser = Mock(User) {
            getEc() >> true
            getB2b() >> false
            isActive() >> false
        }
        def searchResult = Mock(SearchResult)
        activeDirectoryUserMapper.mapToUser(searchResult, _ as User) >> mockedUpdatedUser
        historyLogService.addNewDetailsChangeEvent(_ as DetailsChangeEventInput) >>
                { DetailsChangeEventInput detailsChangeEventInput -> detailsChangeEventInput }


        when:
        ADUserMapper.updateUser(searchResult, mockedUser)

        then:
        1 * historyLogService.addNewDetailsChangeEvent(_ as DetailsChangeEventInput)
    }

    def "updateUser WHEN user become inactive SHOULD save event about it"() {
        given:
        def mockedUser = Mock(User) {
            getEc() >> false
            getB2b() >> false
            isActive() >> true
        }
        def mockedUpdatedUser = Mock(User) {
            getEc() >> false
            getB2b() >> false
            isActive() >> false
        }
        def searchResult = Mock(SearchResult)
        activeDirectoryUserMapper.mapToUser(searchResult, _ as User) >> mockedUpdatedUser
        historyLogService.addNewDetailsChangeEvent(_ as DetailsChangeEventInput) >>
                { DetailsChangeEventInput detailsChangeEventInput -> detailsChangeEventInput }


        when:
        ADUserMapper.updateUser(searchResult, mockedUser)

        then:
        1 * historyLogService.addNewDetailsChangeEvent(_ as DetailsChangeEventInput)
    }

    def "addInitUserEvents WHEN user is active SHOULD save event about it"() {
        given:
        def mockedUser = Mock(User) {
            getEc() >> false
            getB2b() >> false
            isActive() >> true
        }
        def mockedUpdatedUser = Mock(User) {
            getEc() >> false
            getB2b() >> false
            isActive() >> false
        }
        def searchResult = Mock(SearchResult)
        activeDirectoryUserMapper.mapToUser(searchResult, _ as User) >> mockedUpdatedUser
        historyLogService.addNewDetailsChangeEvent(_ as DetailsChangeEventInput) >>
                { DetailsChangeEventInput detailsChangeEventInput -> detailsChangeEventInput }


        when:
        ADUserMapper.updateUser(searchResult, mockedUser)

        then:
        1 * historyLogService.addNewDetailsChangeEvent(_ as DetailsChangeEventInput)
    }

    def "addInitUserEvents WHEN user is ec and active SHOULD save 2 event about it"() {
        given:
        def mockedUser = Mock(User) {
            getEc() >> true
            getB2b() >> false
            isActive() >> true
        }
        historyLogService.addNewDetailsChangeEvent(_ as DetailsChangeEventInput) >>
                { DetailsChangeEventInput detailsChangeEventInput -> detailsChangeEventInput }


        when:
        ADUserMapper.addInitUserEvents(mockedUser)

        then:
        2 * historyLogService.addNewDetailsChangeEvent(_ as DetailsChangeEventInput)
    }

    def "addInitUserEvents WHEN user is b2b and active SHOULD save 2 event about it"() {
        given:
        def mockedUser = Mock(User){
            getEc() >> false
            getB2b() >> true
            isActive() >> true
        }
        historyLogService.addNewDetailsChangeEvent(_ as DetailsChangeEventInput) >>
                { DetailsChangeEventInput detailsChangeEventInput -> detailsChangeEventInput}


        when:
        ADUserMapper.addInitUserEvents(mockedUser)

        then:
        2 * historyLogService.addNewDetailsChangeEvent(_ as DetailsChangeEventInput)
    }
}

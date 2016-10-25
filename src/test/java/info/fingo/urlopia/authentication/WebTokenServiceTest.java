package info.fingo.urlopia.authentication;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Jakub Licznerski
 *         Created on 08.08.2016.
 */
public class WebTokenServiceTest {

    private WebTokenService webTokenService;

    @Before
    public void setup() {
        webTokenService = new WebTokenService();
    }

    @Test
    public void payrollFromCredentialsIdTest() throws Exception{
        ReflectionTestUtils.setField(webTokenService, "SECRET_KEY", "DefaultSecret");

        List<String> roles = new ArrayList<>();
        roles.add("ROLES_ADMIN");
        roles.add("ROLES_LEADER");
        String webToken = webTokenService.generateWebToken(244123511L, roles);

        webTokenService.authorize(webToken);

        Assert.assertEquals(244123511L, webTokenService.getUserId());
    }

    @Test
    public void payrollFromCredentialsRolesTest() throws Exception{
        ReflectionTestUtils.setField(webTokenService, "SECRET_KEY", "DefaultSecret");

        List<String> roles = new ArrayList<>();
        roles.add("ROLES_ADMIN");
        roles.add("ROLES_LEADER");

        String webToken = webTokenService.generateWebToken(244123511L, roles);

        webTokenService.authorize(webToken);

        Assert.assertTrue(roles.containsAll(webTokenService.getRoles()));
    }

}
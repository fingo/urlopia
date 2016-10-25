package info.fingo.urlopia.authentication;

import info.fingo.urlopia.user.UserDTO;
import info.fingo.urlopia.user.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * @author Jakub Licznerski
 *         Created on 27.07.2016.
 */

@RestController
public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class);

    @Autowired
    private UserService service;

    @Autowired
    private WebTokenService webTokenService;

    @Autowired
    private LDAPConnectionService ldapConnectionService;

    @RequestMapping(value = "/api/login", method = RequestMethod.POST)
    public ResponseEntity<UserData> login(HttpServletRequest request) {
        Credentials credentials = new Credentials(request.getHeader("Authorization"));
        HttpStatus status;
        UserData user = null;

        if (ldapConnectionService.authenticate(credentials)) {

            UserDTO userDTO = service.getUser(credentials.getMail());
            if (userDTO != null) {
                user = new UserData(userDTO, webTokenService.generateWebToken(userDTO.getId(), userDTO.getRoles().stream()
                        .map(UserDTO.Role::toString)
                        .collect(Collectors.toList())));
                status = HttpStatus.OK;
            } else {
                status = HttpStatus.OK;
                LOGGER.info("No user data found in database");
            }
        } else {
            status = HttpStatus.OK;
            LOGGER.info("Invalid credentials");
        }
        return new ResponseEntity<>(user, status);
    }
}

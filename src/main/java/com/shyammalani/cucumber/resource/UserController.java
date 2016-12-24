package com.shyammalani.cucumber.resource;

import com.google.common.collect.Maps;
import com.shyammalani.cucumber.model.User;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Api(basePath = "/users", value = "users", description = "Endpoint for user management", produces = "application/json")
@RestController()
@RequestMapping("/users")
public class UserController {

    /**
     * (email, User)
     */
    private Map<String, User> userMap = Maps.newConcurrentMap();
    private final static Logger log = Logger.getLogger(UserController.class);

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "create new User", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Fields are with validation errors"),
            @ApiResponse(code = 201, message = "User created properly")})
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.debug("Create user: {}");

        if (userMap.containsKey(user.getEmail())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        userMap.put(user.getEmail(), user);


        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Fetches User", httpMethod = "GET")
    @RequestMapping(method = RequestMethod.GET, value = "/{email:.+}")
    public ResponseEntity<User> getUser(@PathVariable String email) {
        log.debug("Get user by email: {}");

        final User user = userMap.get(email);

        if (null == user) {
            log.warn("User not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Delete User", httpMethod = "DELETE")
    @RequestMapping(method = RequestMethod.DELETE, value = "/{email:.+}")
    public ResponseEntity<User> deleteUser(@PathVariable String email) {
        log.debug("Delete user with email: {}");

        final User user = userMap.remove(email);

        if (null == user) {
            log.warn("User not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Updates User", httpMethod = "PUT")
    @RequestMapping(method = RequestMethod.PUT, value = "/{email:.+}")
    public ResponseEntity<User> updateUser(@PathVariable String email, @RequestBody User user) {
        log.debug("Update user with email: {}");

        final User currentUser = userMap.get(email);

        if (null == currentUser) {
            log.warn("User not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userMap.put(email, user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}

package com.helenbake.helenbake.controller;

import com.helenbake.helenbake.command.CategoryCommand;
import com.helenbake.helenbake.command.UserCommand;
import com.helenbake.helenbake.domain.Category;
import com.helenbake.helenbake.domain.User;
import com.helenbake.helenbake.dto.ChangePassword;
import com.helenbake.helenbake.repo.RoleRepository;
import com.helenbake.helenbake.repo.UserRepository;
import com.helenbake.helenbake.repo.predicate.CustomPredicateBuilder;
import com.helenbake.helenbake.repo.predicate.Operation;
import com.helenbake.helenbake.security.ProfileDetails;
import com.helenbake.helenbake.services.UserService;
import com.helenbake.helenbake.util.JsonConverter;
import com.helenbake.helenbake.util.PageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping(value = "/me")
    public ResponseEntity<User> getUserInSession(@AuthenticationPrincipal ProfileDetails profileDetails) {
        return ResponseEntity.ok(profileDetails.toUser());
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("createuser")
    public ResponseEntity<UserCommand> createUser(@RequestBody @Valid UserCommand userCommand, BindingResult bindingResult,
                                                      @AuthenticationPrincipal ProfileDetails profileDetails) {

        if (bindingResult.hasErrors() || userCommand == null) {
            return ResponseEntity.badRequest().build();
        }
        User user2 = profileDetails.toUser();
        if (user2 == null) {
            return ResponseEntity.notFound().build();
        }
        if(!roleRepository.findByName(userCommand.getRoleType()).isPresent())
        {
            return ResponseEntity.notFound().build();
        }
        Optional<User> user = userRepository.findByPhoneNumber(userCommand.getPhoneNumber());
        if (user.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }


        UserCommand user1 = userService.createUser(userCommand, user2.getId());
        logger.info("New User created at  " + LocalDateTime.now() + " " + JsonConverter.getJsonRecursive(user1));
        return ResponseEntity.ok(user1);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("editUser")
    public ResponseEntity<UserCommand> editCompany(@RequestBody @Valid UserCommand userCommand, BindingResult bindingResult,
                                                       @AuthenticationPrincipal ProfileDetails profileDetails) {

        if (bindingResult.hasErrors() || userCommand == null) {
            return ResponseEntity.badRequest().build();
        }
        User user2 = profileDetails.toUser();
        if (user2 == null) {
            return ResponseEntity.notFound().build();
        }
        if(!roleRepository.findByName(userCommand.getRoleType()).isPresent())
        {
            return ResponseEntity.notFound().build();
        }
        Optional<User> user = userRepository.findById(userCommand.getId());
        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Optional<User> user1 = userRepository.findByPhoneNumber(userCommand.getPhoneNumber());
        if (user1.isPresent()) {

            if(user1.get().getId() != user.get().getId()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }


        UserCommand userCommand1 = userService.editUser(user.get(), userCommand, profileDetails.toUser());
        logger.info(" User Edited at  " + LocalDateTime.now() + " from " + JsonConverter.getJsonRecursive(user.get()) + "  to  " + JsonConverter.getJsonRecursive(userCommand1));
        return ResponseEntity.ok(userCommand1);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserCommand>> listUsers(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                              @RequestParam(value = "firstName", required = false) String firstName,
                                                              @RequestParam(value = "lastName", required = false) String lastName,
                                                              @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                                                              @RequestParam(value = "asc", required = false) Boolean asc) {
        CustomPredicateBuilder builder = getUserBuilder(firstName, lastName,phoneNumber);
        Pageable pageRequest =
                PageUtil.createPageRequest(page, pageSize, Sort.by(Sort.Order.asc("firstName"), Sort.Order.asc("lastName")));
        Page<UserCommand> userCommands = userService.listAllUsers(builder.build(), pageRequest);
        return ResponseEntity.ok(userCommands);
    }

    private CustomPredicateBuilder getUserBuilder(String firstName, String lastName,String phoneNumber) {
        CustomPredicateBuilder builder = new CustomPredicateBuilder<>("user", User.class)
                .with("firstName", Operation.LIKE, firstName)
                .with("lastName", Operation.LIKE, lastName)
                .with("phoneNumber", Operation.LIKE, phoneNumber);
        return builder;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("setStatus/{id}")
    public ResponseEntity<UserCommand> setUserStatus(@PathVariable("id") Long id,
                                                            @AuthenticationPrincipal ProfileDetails profileDetails) throws MessagingException {



        Optional<User> user = userRepository.findById(id);
        User user2 = profileDetails.toUser();
        if (user2 == null) {
            return ResponseEntity.notFound().build();
        }

        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }


        User user1 = userService.enableDisableUsers(user.get(), user2);
        logger.info(" User status updated  at  " + LocalDateTime.now() + " from " + JsonConverter.getJsonRecursive(user.get()) + "  to  " + JsonConverter.getJsonRecursive(user1));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password")
    public ResponseEntity<UserCommand> changePassword(@RequestBody @Valid ChangePassword passwordDto, BindingResult bindingResult,
                                                      @AuthenticationPrincipal ProfileDetails profileDetails) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            User user = profileDetails.toUser();
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            UserCommand userCommand = new UserCommand();
            System.out.println(passwordDto.getPreviousPassword());
            if (passwordEncoder.matches(passwordDto.getPreviousPassword(), user.getPassword())) {
                userCommand = userService.changePassword(user, passwordDto);
                logger.info("Password Changed created at: " + LocalDateTime.now() + "  ===>" + JsonConverter.getJsonRecursive(userCommand));
                return ResponseEntity.ok(userCommand);

            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/password/reset/{id}")
    public ResponseEntity<UserCommand> resetPassword(@PathVariable("id") Long id,
                                                     @AuthenticationPrincipal ProfileDetails profileDetails) throws MessagingException {



        Optional<User> user = userRepository.findById(id);
        User user2 = profileDetails.toUser();
        if (user2 == null) {
            return ResponseEntity.notFound().build();
        }

        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }


        UserCommand user1 = userService.resetPassword(user.get(), user2.getId());
        logger.info(" User password reset  at  " + LocalDateTime.now() + " from " + JsonConverter.getJsonRecursive(user.get()) + "  to  " + JsonConverter.getJsonRecursive(user1));
        return ResponseEntity.ok().build();
    }

}

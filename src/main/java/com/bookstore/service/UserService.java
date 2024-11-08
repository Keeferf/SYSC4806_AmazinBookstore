package com.bookstore.service;

import com.bookstore.exception.UserExistsException;
import com.bookstore.model.LoginBody;
import com.bookstore.model.RegistrationBody;
import com.bookstore.model.User;
import com.bookstore.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    private EncryptionService encryptionService;

    private JWTService jwtService;

    public UserService(UserRepository userRepository, EncryptionService encryptionService, JWTService jwtService) {
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
    }

    public User registerUser(RegistrationBody registrationBody) throws UserExistsException {
        if(userRepository.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent() ||
                userRepository.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent())  {
            throw new UserExistsException();
        }

        User user = new User();
        user.setUsername(registrationBody.getUsername());
        user.setEmail(registrationBody.getEmail());
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));

        return userRepository.save(user);

    };

    public String loginUser(LoginBody loginBody) {
        Optional<User> user = userRepository.findByUsernameIgnoreCase(loginBody.getUsername());
        if(user.isPresent()) {
            User u = user.get();
            if(encryptionService.checkPassword(loginBody.getPassword(), u.getPassword())) {
                return jwtService.generateToken(u);
            }
        }
        return null;
    }
}

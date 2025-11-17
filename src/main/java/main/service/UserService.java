package main.service;

import main.model.User;
import main.repository.UserRepository;
import main.web.dto.CreateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(UUID id) {
        return userRepository.findByUserId(id).orElseThrow(() -> new RuntimeException("User does not exist!"));
    }

    public User addUser(CreateUserRequest createUserRequest) {
        Optional<User> findUser = userRepository.findByUserId(createUserRequest.getId());

        if (findUser.isEmpty()) {
            User user = new User();
            user.setUserId(createUserRequest.getId());

            return userRepository.save(user);
        }

        return findUser.get();
    }
}

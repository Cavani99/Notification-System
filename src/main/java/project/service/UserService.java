package project.service;

import project.exception.UnknownElementException;
import project.model.User;
import project.repository.UserRepository;
import project.event.payloads.CreateUserRequest;
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
        return userRepository.findByUserId(id).orElseThrow(() -> new UnknownElementException("User does not exist!"));
    }

    public boolean userExists(UUID userId) {
        return userRepository.findByUserId(userId).isPresent();
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

    public User saveUser(UUID userId) {
        User user = new User();
        user.setUserId(userId);

        return userRepository.save(user);
    }
}

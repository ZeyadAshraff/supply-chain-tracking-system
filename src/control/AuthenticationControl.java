package control;

import entity.User;
import database.UserRepository;

public class AuthenticationControl {
    private final UserRepository userRepository;
    private User currentUser;

    public AuthenticationControl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password);
        currentUser = user;
        return user;
    }

    public void logout() {
        currentUser = null;
    }

    public User findUserByUsername(String email) {
        if (currentUser != null && currentUser.getEmail().equalsIgnoreCase(email)) {
            return currentUser;
        }
        return null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}

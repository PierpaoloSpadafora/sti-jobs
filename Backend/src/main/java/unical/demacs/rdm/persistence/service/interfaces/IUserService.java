package unical.demacs.rdm.persistence.service.interfaces;

import jakarta.transaction.Transactional;
import unical.demacs.rdm.persistence.entities.User;

import java.util.Optional;

public interface IUserService {
    @Transactional
    User createUser(String email);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserById(String id);
    void deleteUserById(String id);
}

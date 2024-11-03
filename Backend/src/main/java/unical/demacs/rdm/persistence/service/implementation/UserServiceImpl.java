package unical.demacs.rdm.persistence.service.implementation;

import com.google.common.util.concurrent.RateLimiter;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import unical.demacs.rdm.config.exception.NoUserFoundException;
import unical.demacs.rdm.config.exception.TooManyRequestsException;
import unical.demacs.rdm.config.exception.UserException;
import unical.demacs.rdm.persistence.entities.User;
import unical.demacs.rdm.persistence.repository.UserRepository;
import unical.demacs.rdm.persistence.service.interfaces.IUserService;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {

    public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserRepository userRepository;
    private final RateLimiter rateLimiter;

    @Override
    public User createUser(String email) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Attempting to create user with email: {}", email);
        try {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                logger.info("User with id {} already exists", email);
                return user;
            } else {
                user = User.buildUser()
                        .email(email)
                        .build();
                userRepository.save(user);
                logger.info("User with email {} created successfully", email);
                return user;
            }
        } catch (Exception e) {
            logger.error("Error creating user with email: {}", email, e);
            throw new UserException("Error creating user", e);
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Attempting to get user by email: {}", email);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getUserByEmail");
                throw new TooManyRequestsException();
            }

            return Optional.ofNullable(userRepository.findByEmail(email)
                    .map(user -> {
                        logger.info("User found: {}", user);
                        return user;
                    })
                    .orElseThrow(() -> {
                        logger.warn("User not found for email: {}", email);
                        return new NoUserFoundException("No user found");
                    }));
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public Optional<User> getUserById(String id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Attempting to get user by email: {}", id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for getUserByEmail");
                throw new TooManyRequestsException();
            }

            return Optional.ofNullable(userRepository.findById(id)
                    .map(user -> {
                        logger.info("User found: {}", user);
                        return user;
                    })
                    .orElseThrow(() -> {
                        logger.warn("User not found for id: {}", id);
                        return new NoUserFoundException("No user found");
                    }));
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }

    @Override
    public void deleteUserById(String id) {
        logger.info("++++++START REQUEST++++++");
        logger.info("Attempting to delete user by id: {}", id);
        try {
            if (!rateLimiter.tryAcquire()) {
                logger.warn("Rate limit exceeded for deleteUserById");
                throw new TooManyRequestsException();
            }

            userRepository.findById(id)
                    .ifPresentOrElse(user -> {
                        userRepository.delete(user);
                        logger.info("User with id {} deleted successfully", id);
                    }, () -> {
                        logger.warn("User not found for id: {}", id);
                        throw new NoUserFoundException("No user found");
                    });
        } finally {
            logger.info("++++++END REQUEST++++++");
        }
    }
}

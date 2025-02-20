package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.models.UserDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Convertor convertor;

    @Autowired
    public UserServiceImpl(UserDao userRepository, PasswordEncoder passwordEncoder,
                           Convertor convertor)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.convertor = convertor;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void saveUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()) == null) {
            userRepository.save(new User(
                    userDTO.getEmail(), passwordEncoder.encode(userDTO.getPassword()),
                    userDTO.getName(), userDTO.getLastname(),
                    userDTO.getAge(), userDTO.getEmail(),
                    convertor.stringToSet(userDTO.getRoles())
            ));
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateUser(UserDTO userDTO) {
        User userToUpdate = userRepository.findByEmail(userDTO.getEmail());

        userToUpdate.setId(userDTO.getId());
        userToUpdate.setName(userDTO.getName());
        userToUpdate.setLastname(userDTO.getLastname());
        userToUpdate.setAge(userDTO.getAge());
        userToUpdate.setEmail(userDTO.getEmail());
        userToUpdate.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userToUpdate.setRoles(convertor.stringToSet(userDTO.getRoles()));

        userRepository.save(userToUpdate);
    }
}


package com.user.app.server.service;

import com.user.app.server.model.Role;
import com.user.app.server.model.User;
import com.user.app.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        List<User> all = userRepository.findAll().
                stream()
                .filter(user1 -> Objects.equals(user.getUsername(), user1.getUsername()))
                .collect(Collectors.toList());

        if (all.size() != 0) {
            return user;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Role.USER);
        return userRepository.save(user);
    }

    @Override
    public User getUser(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("User Not Found!");
                });

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        Role role = user.getRoles();

        authorities.add(new SimpleGrantedAuthority(role.name()));

        return new org.springframework.security.core
                .userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}

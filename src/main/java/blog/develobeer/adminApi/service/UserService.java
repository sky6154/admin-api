package blog.develobeer.adminApi.service;

import blog.develobeer.adminApi.dao.admin.role.UserRoleRepository;
import blog.develobeer.adminApi.dao.admin.user.UserRepository;
import blog.develobeer.adminApi.domain.admin.role.UserRole;
import blog.develobeer.adminApi.domain.admin.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService, Serializable {
    private static final long serialVersionUID = 1L;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findById(username);

        optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found."));

        return optionalUser
                .map(user -> new UserDetails() {
                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        List<UserRole> userRoles = userRoleRepository.getUserRolesByUserId( user.getId() ).orElse( Collections.emptyList() );

                        return userRoles
                                .stream()
                                .map(userRole -> new SimpleGrantedAuthority("ROLE_" + userRole.getRole()))
                                .collect(Collectors.toList());
                    }

                    @Override
                    public String getPassword() {
                        return user.getPwd();
                    }

                    @Override
                    public String getUsername() {
                        return user.getId();
                    }

                    @Override
                    public boolean isAccountNonExpired() {
                        return true;
                    }

                    @Override
                    public boolean isAccountNonLocked() {
                        return true;
                    }

                    @Override
                    public boolean isCredentialsNonExpired() {
                        return true;
                    }

                    @Override
                    public boolean isEnabled() {
                        return true;
                    }
                })
                .get();
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}

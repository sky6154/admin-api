package blog.develobeer.adminApi.service;

import blog.develobeer.adminApi.dao.admin.role.RoleRepository;
import blog.develobeer.adminApi.dao.admin.user.UserRepository;
import blog.develobeer.adminApi.domain.admin.role.Role;
import blog.develobeer.adminApi.domain.admin.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username);

        UserDetails userDetails = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                List<GrantedAuthority> authorities = new ArrayList<>();
                List<Role> roles = roleRepository.findAll();

                for(Role role : roles){
                    authorities.add(new SimpleGrantedAuthority( role.getRole() ));
                }

                return authorities;
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
        };

        return userDetails;
    }

    public User save(User user){
        return userRepository.save(user);
    }
}

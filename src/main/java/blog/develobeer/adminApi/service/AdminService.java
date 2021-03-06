package blog.develobeer.adminApi.service;

import blog.develobeer.adminApi.repo.admin.role.AdminRoleRepository;
import blog.develobeer.adminApi.repo.admin.user.AdminRepository;
import blog.develobeer.adminApi.domain.admin.role.AdminRole;
import blog.develobeer.adminApi.domain.admin.user.Admin;
import blog.develobeer.adminApi.domain.admin.user.AdminDetails;
import blog.develobeer.adminApi.utils.AdminContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService implements UserDetailsService, Serializable {
    private static final long serialVersionUID = -7643057474949440931L;

    private final AdminRepository adminRepository;
    private final AdminRoleRepository adminRoleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ROLE_PREFIX = "ROLE_";

    @Autowired
    public AdminService(AdminRepository adminRepository,
                        AdminRoleRepository adminRoleRepository,
                        PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.adminRoleRepository = adminRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Admin> optionalAdmin = adminRepository.findById(username);
        optionalAdmin.orElseThrow(() -> new UsernameNotFoundException("Admin not found."));

        Admin admin = optionalAdmin.get();
        List<AdminRole> adminRoles = adminRoleRepository.getAdminRolesByUserId(admin.getId());

        Collection<? extends GrantedAuthority> authorities = adminRoles
                .stream()
                .map(adminRole -> new SimpleGrantedAuthority(ROLE_PREFIX + adminRole.getRole()))
                .collect(Collectors.toList());

        return AdminDetails.builder()
                .authorities(authorities)
                .password(admin.getPwd())
                .username(admin.getId())
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .isEnabled(true)
                .build();
    }

    public boolean isExist(String adminId) {
        return adminRepository.findById(adminId).isPresent();
    }

    public Admin addAdmin(Admin admin) {
        String encryptedPassword = passwordEncoder.encode(admin.getPwd());
        admin.setPwd(encryptedPassword);

        return adminRepository.saveAndFlush(admin);
    }

    public boolean addAdminRole(List<AdminRole> adminRoleList) {
        try {
            adminRoleRepository.saveAll(adminRoleList);
            adminRoleRepository.flush();
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public List<Admin> getAllAdminList() {
        return adminRepository.getAdminList();
    }

    public boolean changePassword(String newPassword) {
        Optional<Admin> optionalAdmin = adminRepository.findById(AdminContext.getAdminName());

        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();

            admin.setPwd(passwordEncoder.encode(newPassword));
            adminRepository.saveAndFlush(admin);

            return true;
        } else {
            throw new UsernameNotFoundException("Invalid admin : " + AdminContext.getAdminName());
        }
    }
}

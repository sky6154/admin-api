package blog.develobeer.adminApi.service;

import blog.develobeer.adminApi.dao.admin.role.AdminRoleRepository;
import blog.develobeer.adminApi.dao.admin.user.AdminRepository;
import blog.develobeer.adminApi.domain.admin.role.AdminRole;
import blog.develobeer.adminApi.domain.admin.user.Admin;
import blog.develobeer.adminApi.utils.CommonTemplateMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final AdminRoleRepository adminRoleRepository;
    private final PasswordEncoder passwordEncoder;

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

        return optionalAdmin
                .map(admin -> new UserDetails() {
                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        List<AdminRole> adminRoles = adminRoleRepository.getAdminRolesByUserId(admin.getId()).orElse(Collections.emptyList());

                        return adminRoles
                                .stream()
                                .map(adminRole -> new SimpleGrantedAuthority("ROLE_" + adminRole.getRole()))
                                .collect(Collectors.toList());
                    }

                    @Override
                    public String getPassword() {
                        return admin.getPwd();
                    }

                    @Override
                    public String getUsername() {
                        return admin.getId();
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

    public boolean isExist(String adminId) {
        return adminRepository.findById(adminId).isPresent();
    }

    public Admin addAdmin(Admin admin) {
        String encryptedPassword = passwordEncoder.encode(admin.getPwd());
        admin.setPwd(encryptedPassword);

        return adminRepository.saveAndFlush(admin);
    }

    public boolean addAdminRole(List<AdminRole> adminRoleList) {
        return CommonTemplateMethod.simpleSaveTryCatchBooleanReturn(adminRoleRepository, adminRoleList);
    }

    public List<Admin> getAllAdminList(){
        List<Admin> adminList = adminRepository.findAll();

        for(int i = 0; i < adminList.size(); i++){
            adminList.get(i).setPwd(null);
        }

        return adminList;
    }
}

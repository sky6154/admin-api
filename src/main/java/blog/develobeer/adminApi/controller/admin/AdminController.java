package blog.develobeer.adminApi.controller.admin;

import blog.develobeer.adminApi.domain.admin.role.AdminRole;
import blog.develobeer.adminApi.domain.admin.role.AdminRoleId;
import blog.develobeer.adminApi.domain.admin.role.Role;
import blog.develobeer.adminApi.domain.admin.user.Admin;
import blog.develobeer.adminApi.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private static final String DEFAULT_PASSWORD = "1234";

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @RequestMapping(value = "/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("HELLO ADMIN !");
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity add(@RequestBody String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> jsonMap = objectMapper.readValue(json, Map.class);

            Admin admin = (Admin) jsonMap.get("admin");
            admin.setPwd(DEFAULT_PASSWORD);

            if (adminService.isExist(admin.getId())) {
                return ResponseEntity.badRequest().body("Admin already exist");
            } else {
                Admin result = adminService.addAdmin(admin);
                Role[] roles = objectMapper.readValue(jsonMap.get("role").toString(), Role[].class);

                List<AdminRole> adminRoleList = new ArrayList<>();

                for (Role role : roles) {
                    AdminRoleId adminRoleId = new AdminRoleId();
                    adminRoleId.setRoleId(role.getRoleId());
                    adminRoleId.setUserSeq(result.getSeq());

                    AdminRole adminRole = new AdminRole();
                    adminRole.setAdminRoleId(adminRoleId);

                    adminRoleList.add(adminRole);
                }

                adminService.addAdminRole(adminRoleList);

                URI uri = ControllerLinkBuilder.linkTo(AdminController.class).slash("add").slash(result.getId()).toUri();
                result.setPwd(null);

                return ResponseEntity.created(uri).body(result);
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Iterable<Admin> getAllAdminList() {
        return adminService.getAllAdminList();
    }

    @RequestMapping(value = "/password/change", method = RequestMethod.POST)
    public boolean changePassword(@RequestBody String newPassword) {
        return adminService.changePassword(newPassword);
    }
}

package blog.develobeer.adminApi.controller.admin;

import blog.develobeer.adminApi.config.GsonDateDeserializer;
import blog.develobeer.adminApi.controller.blog.PostController;
import blog.develobeer.adminApi.domain.admin.role.AdminRole;
import blog.develobeer.adminApi.domain.admin.role.AdminRoleId;
import blog.develobeer.adminApi.domain.admin.role.Role;
import blog.develobeer.adminApi.domain.admin.user.Admin;
import blog.develobeer.adminApi.service.AdminService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final Gson gson;
    private static final String DEFAULT_PASSWORD = "1234";

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
        this.gson = new GsonBuilder()
                .serializeNulls() // null인 object의 내부 변수가 null이면 key를 없애지 않고 null로 명시한다.
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .registerTypeAdapter(Date.class, new GsonDateDeserializer())
                .create();
    }

    @RequestMapping(value = "/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("HELLO ADMIN !");
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity add(@RequestBody String json) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();

        JsonObject jAdminUser = jsonObject.getAsJsonObject("admin");
        Admin admin = gson.fromJson(jAdminUser, Admin.class);
        admin.setPwd(DEFAULT_PASSWORD); // default password

        if (adminService.isExist(admin.getId())) {
            return new ResponseEntity<>("Admin already exist", HttpStatus.CONFLICT);
        }

        Admin result = adminService.addAdmin(admin);

        Iterator<com.google.gson.JsonElement> roleList = jsonObject.getAsJsonArray("role").iterator();
        List<AdminRole> adminRoleList = new ArrayList<>();

        while (roleList.hasNext()) {
            String val = roleList.next().toString();

            Role role = gson.fromJson(val, Role.class);

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

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Iterable<Admin> getAllAdminList() {
        return adminService.getAllAdminList();
    }
}

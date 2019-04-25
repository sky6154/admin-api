package blog.develobeer.adminApi.controller.admin;

import blog.develobeer.adminApi.config.GsonDateDeserializer;
import blog.develobeer.adminApi.domain.admin.role.Role;
import blog.develobeer.adminApi.domain.admin.role.AdminRole;
import blog.develobeer.adminApi.domain.admin.role.AdminRoleId;
import blog.develobeer.adminApi.domain.admin.user.Admin;
import blog.develobeer.adminApi.service.AdminService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final Gson gson;

    @Autowired
    public AdminController(AdminService adminService){
        this.adminService = adminService;
        this.gson = new GsonBuilder()
                .serializeNulls() // null인 object의 내부 변수가 null이면 key를 없애지 않고 null로 명시한다.
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .registerTypeAdapter(Date.class, new GsonDateDeserializer())
                .create();
    }

    @RequestMapping(value = "/")
    public String home(){
        return "HELLO ADMIN !";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json")
    public boolean add(@RequestBody String json, HttpServletResponse res) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();

        try {
            JsonObject jAdminUser = jsonObject.getAsJsonObject("adminAdmin");
            Admin adminAdmin = gson.fromJson(jAdminUser, Admin.class);
            adminAdmin.setPwd("1234"); // default password

            if(adminService.isExist(adminAdmin.getId())){
                res.sendError(HttpServletResponse.SC_CONFLICT, "Admin already exist");
                return false;
            }

            Admin result = adminService.addAdmin(adminAdmin);

            if(result != null){
                Iterator roleList = jsonObject.getAsJsonArray("role").iterator();

                List<AdminRole> adminRoleList = new ArrayList<>();

                while(roleList.hasNext()){
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

                return true;
            }
            else{
                return false;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    @RequestMapping(value = "/getAllAdmin", method = RequestMethod.GET)
    public String getAllAdmin(){
        return gson.toJson(adminService.getAllAdmin());
    }
}

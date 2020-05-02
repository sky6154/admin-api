package blog.develobeer.adminApi.dao.admin.role;

import blog.develobeer.adminApi.domain.admin.role.AdminRole;
import blog.develobeer.adminApi.domain.admin.role.AdminRoleId;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

import static blog.develobeer.adminApi.domain.admin.role.QAdminRole.adminRole;
import static blog.develobeer.adminApi.domain.admin.role.QRole.role1;
import static blog.develobeer.adminApi.domain.admin.user.QAdmin.admin;

public class QAdminRoleRepositoryImpl implements QAdminRoleRepository {
    private final JPAQueryFactory adminQueryFactory;

    @Autowired
    public QAdminRoleRepositoryImpl(
            @Qualifier("adminQueryFactory") JPAQueryFactory adminQueryFactory) {
        this.adminQueryFactory = adminQueryFactory;
    }

    @Override
    public List<AdminRole> getAdminRolesByUserId(String id) {
        List<Tuple> result = adminQueryFactory.select(admin.id, role1.role, adminRole.adminRoleId.userSeq, adminRole.adminRoleId.roleId, adminRole.regDate)
                .from(admin, role1, adminRole)
                .where(admin.id.eq(id).and(admin.seq.eq(adminRole.adminRoleId.userSeq)).and(adminRole.adminRoleId.roleId.eq(role1.roleId)))
                .fetch();

        List<AdminRole> adminList = new ArrayList<>();

        for (Tuple row : result) {
            AdminRole ar = new AdminRole();
            AdminRoleId ari = new AdminRoleId();

            ari.setUserSeq(row.get(adminRole.adminRoleId.userSeq));
            ari.setRoleId(row.get(adminRole.adminRoleId.roleId));

            ar.setAdminRoleId(ari);
            ar.setId(row.get(admin.id));
            ar.setRole(row.get(role1.role));
            ar.setRegDate(row.get(adminRole.regDate));

            adminList.add(ar);
        }

        return adminList;
    }
}

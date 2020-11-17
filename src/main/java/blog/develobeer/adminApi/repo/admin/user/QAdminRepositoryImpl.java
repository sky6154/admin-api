package blog.develobeer.adminApi.repo.admin.user;

import blog.develobeer.adminApi.domain.admin.user.Admin;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static blog.develobeer.adminApi.domain.admin.role.QAdminRole.adminRole;
import static blog.develobeer.adminApi.domain.admin.role.QRole.role1;
import static blog.develobeer.adminApi.domain.admin.user.QAdmin.admin;

public class QAdminRepositoryImpl implements QAdminRepository {
    private final JPAQueryFactory adminQueryFactory;

    @Autowired
    public QAdminRepositoryImpl(
            @Qualifier("adminQueryFactory") JPAQueryFactory adminQueryFactory) {
        this.adminQueryFactory = adminQueryFactory;
    }

    @Override
    public List<Admin> getAdminList() {
        List<Tuple> result = adminQueryFactory.select(admin, role1)
                .from(admin, role1, adminRole)
                .where(admin.seq.eq(adminRole.adminRoleId.userSeq).and(adminRole.adminRoleId.roleId.eq(role1.roleId)))
                .fetch();

        List<Admin> adminList = new ArrayList<>();

        for (Tuple row : result) {
            Admin res = row.get(admin);
            res.setRole(row.get(role1));

            if (Objects.nonNull(res.getPwd())) {
                res.setPwd(null);
            }

            adminList.add(res);
        }

        return adminList;
    }
}

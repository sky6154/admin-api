package blog.develobeer.adminApi.repo.admin.role;

import blog.develobeer.adminApi.domain.admin.role.AdminRole;
import blog.develobeer.adminApi.domain.admin.role.QAdminRole;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
        return adminQueryFactory.select(QAdminRole.create(adminRole.adminRoleId, admin.id, role1.role, adminRole.regDate))
                .from(admin)
                .join(adminRole)
                    .on(admin.seq.eq(adminRole.adminRoleId.userSeq))
                .join(role1)
                    .on(adminRole.adminRoleId.roleId.eq(role1.roleId))
                .where(
                        admin.id.eq(id))
                .fetch();
    }
}

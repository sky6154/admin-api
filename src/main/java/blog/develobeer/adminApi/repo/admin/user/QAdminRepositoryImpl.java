package blog.develobeer.adminApi.repo.admin.user;

import blog.develobeer.adminApi.domain.admin.user.Admin;
import blog.develobeer.adminApi.domain.admin.user.QAdmin;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

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
        return adminQueryFactory.select(QAdmin.create(admin.id, admin.pwd, admin.description, admin.name, admin.isActive, admin.email, role1, admin.modifyDate, admin.regDate))
                .from(admin)
                .join(adminRole)
                    .on(admin.seq.eq(adminRole.adminRoleId.userSeq))
                .join(role1)
                    .on(adminRole.adminRoleId.roleId.eq(role1.roleId))
                .fetch();
    }
}

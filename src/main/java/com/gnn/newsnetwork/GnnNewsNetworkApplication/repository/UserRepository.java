package com.gnn.newsnetwork.GnnNewsNetworkApplication.repository;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Users;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.ROLE;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.management.relation.Role;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    //
    Optional<Users> findByEmail(String email);
    List<Users> findByRoleAndActive(ROLE role, boolean active);
    void deleteByEmail(String email);
    List<Users> findByRole(ROLE role);

    Optional<Users> findByUsername(String username);
}

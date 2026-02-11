package com.qburst.microservice.auth.repository;

import com.qburst.microservice.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT ue from UserEntity ue WHERE ue.username=:username")
    public List<UserEntity> findUserByUsername(@Param("username") String username);

    Optional<UserEntity> findByEmail(String username);
}

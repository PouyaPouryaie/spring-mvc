package ir.bigz.springboot.springmvc.dal;

import ir.bigz.springboot.springmvc.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}

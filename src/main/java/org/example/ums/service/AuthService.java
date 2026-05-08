package org.example.ums.service;

import org.example.ums.entity.Admin;
import org.example.ums.entity.Instructor;
import org.example.ums.entity.Student;
import org.example.ums.entity.User;
import org.example.ums.entity.enums.Role;

import java.util.Optional;

public class AuthService {

	public Optional<User> authenticate(String email, String password) {
		return JpaUtil.execute(entityManager -> {
			User user = entityManager.createQuery(
						"select u from User u where u.email = :email and u.password = :password",
						User.class)
				.setParameter("email", email)
				.setParameter("password", password)
				.getResultStream()
				.findFirst()
				.orElse(null);

			if (user == null) {
				return Optional.empty();
			}

			if (user.getRole() == Role.STUDENT) {
				return Optional.ofNullable(entityManager.find(Student.class, user.getId()));
			}
			if (user.getRole() == Role.INSTRUCTOR) {
				return Optional.ofNullable(entityManager.find(Instructor.class, user.getId()));
			}
			if (user.getRole() == Role.ADMIN) {
				return Optional.ofNullable(entityManager.find(Admin.class, user.getId()));
			}

			return Optional.of(user);
		});
	}
}


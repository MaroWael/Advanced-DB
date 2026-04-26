package org.example.ums.service;

import org.example.ums.entity.User;
import org.example.ums.entity.enums.Role;

import java.util.Optional;

public class AuthService extends JpaServiceSupport {

	public Optional<User> authenticate(String email, String password) {
		return execute(entityManager -> {
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
				return Optional.ofNullable(entityManager.find(org.example.ums.entity.Student.class, user.getId()));
			}
			if (user.getRole() == Role.INSTRUCTOR) {
				return Optional.ofNullable(entityManager.find(org.example.ums.entity.Instructor.class, user.getId()));
			}
			if (user.getRole() == Role.ADMIN) {
				return Optional.ofNullable(entityManager.find(org.example.ums.entity.Admin.class, user.getId()));
			}

			return Optional.of(user);
		});
	}
}


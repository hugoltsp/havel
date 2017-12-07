package com.teles.havel.batch.test;

import java.util.UUID;

final class User {

	private String name;
	private String email;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public static User mockUser() {
		User user = new User();
		user.setEmail(uuid());
		user.setName(uuid());
		return user;
	}

	private static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", email=" + email + "]";
	}

}

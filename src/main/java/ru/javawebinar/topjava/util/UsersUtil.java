package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;

import java.util.Arrays;
import java.util.List;

public class UsersUtil {
    public static final List<User> USERS = Arrays.asList(
            new User(null, "Admin Name", "admin@mail.ru", "admin_password", Role.ROLE_ADMIN, Role.ROLE_USER),
            new User(null, "User Name", "user@mail.ru", "user_password", Role.ROLE_USER)
    );
}

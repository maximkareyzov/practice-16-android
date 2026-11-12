package com.example.practice16;

import java.util.ArrayList;
import java.util.List;

/** Данные разделяются между активностями в течение работы процесса приложения. */
public class UserStaticInfo {
    public static String profileId;
    public static final String USERS_PROFILE_INFO = "UsersProfileInfo";
    public static final String POSITION = "position";
    public static final List<User> users = new ArrayList<>();

    public UserStaticInfo() {
        if (users.isEmpty()) {
            users.add(new User("Иван", "Я устал", 19, 0));
            users.add(new User("Анна", "Учусь программировать", 20, 1));
            users.add(new User("Мария", "На занятиях", 21, 2));
            users.add(new User("Алексей", "Работаю над проектом", 19, 0));
        }
    }
}

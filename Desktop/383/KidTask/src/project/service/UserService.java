package project.service;

import project.model.User;
import project.util.JsonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final String FILE = "data/users.json";
    private List<User> users = new ArrayList<>();

    public UserService() {
        loadUsers();
    }

    // ---------------- LOAD / SAVE ----------------

    private void loadUsers() {
        File f = new File(FILE);

        if (!f.exists()) {
            users = new ArrayList<>();
            saveUsers();
            return;
        }

        users = JsonUtils.load(FILE, JsonUtils.listOf(User.class));
        if (users == null) users = new ArrayList<>();
    }

    public void saveUsers() {
        JsonUtils.save(FILE, users);
    }

    // ---------------- USER CRUD ----------------

    public void addUser(User u) {
        users.add(u);
        saveUsers();
    }

    public List<User> getAllUsers() {
        return users;
    }

    public User getUserById(String id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public User login(String name, String password) {
        return users.stream()
                .filter(u -> u.getName().equalsIgnoreCase(name)
                        && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    // ---------------- RELATIONS ----------------
    public List<User> getChildrenOfParent(String parentId) {
        User parent = getUserById(parentId);
        if (parent == null) return new ArrayList<>();

        List<String> ids = parent.getChildrenIds();
        List<User> result = new ArrayList<>();

        for (String id : ids) {
            User child = getUserById(id);
            if (child != null) result.add(child);
        }
        return result;
    }

    public List<User> getKidsOfClass(String classId) {
        List<User> result = new ArrayList<>();

        for (User u : users) {
            if ("kid".equals(u.getRole()) && classId.equals(u.getClassId()))
                result.add(u);
        }
        return result;
    }

    public List<User> getKidsOfTeacher(String teacherId) {
        User t = getUserById(teacherId);
        if (t == null) return new ArrayList<>();

        List<User> list = new ArrayList<>();
        for (String classId : t.getClassIds()) {
            list.addAll(getKidsOfClass(classId));
        }
        return list;
    }
}

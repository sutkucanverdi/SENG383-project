package project.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    private String id;
    private String name;
    private String surname;
    private int age;
    private String role; // kid, parent, teacher
    private String password;

    private int points = 0;      // Harcanabilir Puan (Cüzdan)
    private int totalPoints = 0; // Toplam Kazanılan Puan (XP - Level için)
    private int level = 1;

    private String classId;           // Kid's class
    private List<String> childrenIds; // Parent's kids
    private List<String> classIds;    // Teacher's classes

    public User() {}

    public User(String name, String surname, String role, String password) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.password = password;

        this.childrenIds = new ArrayList<>();
        this.classIds = new ArrayList<>();
        
        // Yeni kullanıcılar 0 puanla başlar
        this.points = 0;
        this.totalPoints = 0;
        this.level = 1;
    }

    // --- FACTORY METHODS ---
    public static User createKid(String name, String surname, int age, String classId, String password) {
        User u = new User(name, surname, "kid", password);
        u.age = age;
        u.classId = classId;
        return u;
    }

    public static User createParent(String name, String surname, String password) {
        return new User(name, surname, "parent", password);
    }

    public static User createTeacher(String name, String surname, String password) {
        return new User(name, surname, "teacher", password);
    }

    // --- GETTERS ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public int getAge() { return age; }
    public String getRole() { return role; }
    public String getPassword() { return password; }
    public int getPoints() { return points; }
    public int getLevel() { return level; }
    public String getClassId() { return classId; }
    public List<String> getChildrenIds() { return childrenIds; }
    public List<String> getClassIds() { return classIds; }

    // --- RELATION METHODS ---
    public void addChild(String childId) {
        if (childrenIds == null) childrenIds = new ArrayList<>();
        childrenIds.add(childId);
    }

    public void addClass(String classId) {
        if (classIds == null) classIds = new ArrayList<>();
        classIds.add(classId);
    }

    // --- PUAN VE LEVEL MANTIĞI (GÜNCELLENDİ) ---
    
    public void addPoints(int p) {
        if (p > 0) {
            // Puan KAZANILIYORSA: Hem cüzdan hem XP artar
            this.points += p;
            this.totalPoints += p;
            updateLevel(); // Sadece puan kazanılınca level kontrol et
        } else {
            // Puan HARCANIYORSA (Negatif değer gelirse): Sadece cüzdan azalır
            this.points += p; 
            // Level kontrolü YAPMA, böylece level düşmez.
        }
    }

    private void updateLevel() {
        // KURAL: Her 150 puanda 1 level atla. Max Level 5.
        // Level 1: 0 - 149
        // Level 2: 150 - 299
        // Level 3: 300 - 449
        // Level 4: 450 - 599
        // Level 5: 600+
        
        int calculatedLevel = (this.totalPoints / 150) + 1;

        if (calculatedLevel > 5) {
            this.level = 5;
        } else {
            this.level = calculatedLevel;
        }
    }

    @Override
    public String toString() {
        return "[" + role + "] " + name + " " + surname +
               " | Wallet: " + points + " | Level: " + level;
    }
}
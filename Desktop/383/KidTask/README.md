---
# 🧸 KidTask - Task and Wish Management Application
### Version 0.1 (Console Prototype)
**Developer:** Yiğit Tacir (c2328050)

[cite_start]This section tracks the development of the **KidTask** project, a Java-based application designed to help children manage tasks, earn points, and make wishes with parental/teacher approval[cite: 5].

## ✅ V0.1 - Completed Features (Backend & Logic)
This version establishes the core business logic and data structure of the application.

1.  **Core Architecture:**
    * Implemented MVC-like structure (`Model`, `Service`, `Util`) separating data and logic.
    * [cite_start]Created `User` (Kid, Parent, Teacher), `Task`, and `Wish` models[cite: 14].

2.  **Data Persistence:**
    * [cite_start]Integrated **JSON** file handling (`users.json`, `tasks.json`, `wishes.json`) to save data permanently[cite: 41].
    * [cite_start]System automatically loads data on startup and saves on changes[cite: 42].

3.  **Role-Based Logic:**
    * [cite_start]**Authentication:** Login and Registration system for Kids, Parents, and Teachers[cite: 20].
    * **Parent-Child Linking:** Implemented a logic to link Parent accounts with existing Child accounts using usernames.

4.  **Task & Point System:**
    * **Approval Workflow:** Kids can mark tasks as "Pending". [cite_start]Points are **only** awarded after a Parent or Teacher approves the task (Fixed logic issue)[cite: 31].
    * [cite_start]**Dynamic Levels:** User levels update automatically based on total points[cite: 39].

## ⚠️ Known Issues & Current Limitations
1.  **User Interface:** The application currently runs on the **Console (CLI)** via `Scanner`. [cite_start]It does not yet meet the GUI (Java Swing/JavaFX) requirement[cite: 6].
2.  **Input Handling:** Input validation is basic; entering text instead of numbers may cause runtime errors in the console environment.
3.  [cite_start]**Visualization:** No visual dashboards or progress bars are available yet[cite: 13].

## 🚀 Next Steps (v0.2 Roadmap)
* [ ] Migrate `MainMenu` from Console to **Java Swing GUI**.
* [cite_start][ ] Create Login Screen and Dashboard Panels for each role[cite: 21].
* [cite_start][ ] Implement visual tables for Task and Wish lists[cite: 25].
* [cite_start][ ] Add visual Progress Bars for points and levels[cite: 38].

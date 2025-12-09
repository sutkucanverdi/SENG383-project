# GUI REVIEW – BeePlan Course Scheduler

## 1. Main Timetable View and Year Tabs

### Strengths

- The main timetable uses a clear weekly grid (days vs. time slots) that matches the requirement for a “weekly timetable view (days × hours).”
- The four year tabs (1st–4th Year) are visually clear and allow switching between different year schedules in a simple way.
- The Friday 13:20–15:10 exam block requirement is visually represented via the “EXAM BLOCK” cells, which directly reflects the project rules and improves output clarity.
- The overall visual design (colors, spacing, card-like containers) is clean and modern, which supports readability and usability.

### Issues and Suggestions

- The timetable currently shows only an empty grid plus the exam block; there is no visible legend for course colors (theory vs. lab vs. conflict). Adding a small legend would help users understand the meaning of different colors or cell styles.
- There is no visible indication of which department or curriculum is being scheduled. Since BeePlan is supposed to work for department curricula and common courses, a small label or selector (for example, “Department / Program”) could be added at the top.
- Time slots are generic (08:00–18:00) but do not explicitly show that Friday 13:20–15:10 is a hard rule rather than just a visual block. A tooltip or short explanatory text under the timetable could clarify that this is an exam period and cannot be scheduled.

---

## 2. “Add Course” Form (Sidebar)

### Strengths

- The “Add Course” form provides the core fields needed to describe a course: course name, instructor, hours per week, year, laboratory flag, and class capacity. This matches the BeePlan inputs such as course hours, instructor, lab information, and capacity.
- The layout is simple and vertical, with clear labels and placeholders. This is good for usability and reduces user error.
- Using a checkbox for “Laboratory Course” is appropriate and makes it easy to distinguish lab courses from normal ones.

### Issues and Suggestions

- The form does not differentiate between theory and lab hours (for example, 3+2 vs. 3+0). BeePlan requirements mention theory/lab information and rules like “lab after theory,” so the GUI should include separate inputs such as “Theory Hours” and “Lab Hours” rather than a single “Hours per Week” value.
- There is no way to enter or link scheduling constraints such as instructor availability, Friday exam block rules, elective constraints, or room type preferences. At minimum, there should be a way to select or import instructor availability and classroom-type constraints, or a separate section for constraint configuration.
- The “Year” field is limited to 1–4, which is correct for undergraduate years, but there is no indication of which program or curriculum the course belongs to (CENG, SENG, service courses, etc.). Adding a “Program / Department” dropdown would better reflect the real data model.
- The capacity field allows up to 200 students without any guidance. Since lab capacity is limited to 40, this rule should be hinted in the UI (for example, helper text such as “Lab capacity must be ≤ 40”).

---

## 3. Actions and Course List

### Strengths

- The three main actions are clearly separated and labeled: “Generate Schedule,” “View Validation Report,” and “Clear All Courses.” This matches the BeePlan requirement for a “Generate Schedule” button and a “View Report” button.
- The “Course List” panel under the actions is useful; it shows a summary of added courses and gives feedback that the input has been saved.
- The vertical alignment of actions and list is simple and easy to scan.

### Issues and Suggestions

- The “Course List” item content is purely textual. It would be more informative to visually distinguish lab courses (for example, a small label “Lab”) and possibly show year and hours more clearly in a structured way.
- There is no way to edit or delete an individual course from the list; the only destructive action seems to be “Clear All Courses.” For usability, each course entry should ideally support edit/remove actions.
- The UI does not show any summary statistics such as total theory hours per day per instructor, total number of courses per year, or number of unresolved conflicts, which could help coordinators assess the schedule quality before generating a report.

---

## 4. Validation Report Panel

### Strengths

- The validation report panel clearly lists messages such as “No conflicts detected,” “All instructor availability constraints met,” “Friday exam block respected,” and “Lab capacity limits observed.” This directly matches the BeePlan requirement for validation reports on conflicts and capacity issues.
- The green success-style design communicates positive feedback well and makes it obvious when the schedule is valid.
- Structuring the report in a separate panel below the timetable is a good design choice, as it keeps the schedule view uncluttered while still making validation information easy to access.

### Issues and Suggestions

- The current design only shows success messages. To fully support usability, the report should also display errors and warnings in different visual styles (for example, separate sections for conflicts, capacity violations, and rule violations).
- There is no explicit link between report messages and the timetable. For better usability, clicking a message could highlight the corresponding conflicting time slots or courses in the timetable.
- There is no export or save option from the validation report. Since BeePlan requires output reports, adding an “Export Report (CSV/PDF)” or “Save Report” button would align the GUI better with the requirements.

---

## 5. General Usability and Alignment with Requirements

### Strengths

- The overall layout (left: controls; right: timetable and report) is intuitive and matches typical scheduling tools, which improves learnability for users.
- Visual hierarchy is strong: title at the top, year tabs, main timetable, then validation below. It is easy to understand the main purpose of the screen at a glance.
- The design already reflects several BeePlan constraints: year-based schedules, exam-block visualization, course capacity, and separation of lab versus non-lab courses.

### Issues and Suggestions

- Some core input concepts from the BeePlan specification are not yet visible in the GUI, such as:
  - importing common course schedules,
  - importing instructor schedules,
  - specifying scheduling rules and priorities,
  - managing classroom and lab lists.  
  These may be hidden in the backend or planned for later, but the current GUI does not provide an obvious way to manage them.
- There is no user-role context in the interface (for example, Course Schedule Coordinator versus Instructor). Since BeePlan is oriented around coordinators, it might be useful to show the current coordinator or department.
- No explicit import/export controls for JSON or CSV are visible, although the requirement states that import/export from JSON or CSV is needed. Buttons such as “Import Data (CSV/JSON)” and “Export Schedule” would make this requirement explicit in the UI.

---

## 6. Overall Evaluation (GUI)

The BeePlan GUI design is visually clean, modern, and generally aligned with the intended functionality of a course scheduling system. The timetable, year tabs, exam-block visualization, and validation report area are strong points and provide a clear foundation for the final application.

However, the current design does not yet expose all of the important inputs and constraints described in the BeePlan specification, such as theory versus lab hours, detailed instructor availability, elective constraints, and structured import/export operations. Extending the GUI to reflect these concepts more explicitly would significantly improve both output quality and usability and would better support the real workflow of a course schedule coordinator.

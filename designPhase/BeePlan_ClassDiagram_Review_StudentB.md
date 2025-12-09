## CLASS DIAGRAM REVIEW – BeePlan

### 1. Overall Assessment

The class diagram demonstrates a solid conceptual model of the BeePlan system. Core entities such as User, Child, Parent, Teacher, Task, Wish, and DataManager are represented, and the class responsibilities generally align with the functional requirements.

However, several structural issues, naming inconsistencies, missing relationships, and incomplete method definitions reduce the overall accuracy and robustness of the design. Refining these aspects will improve both implementation quality and UML correctness.

---

## 2. Strengths

### Well-Defined Core Entities
- The main user types (Child, Parent, Teacher) inherit from an abstract User class.
- Task and Wish classes include key attributes: title, description, points, deadlines, and status.
- DataManager reflects functionality for loading and saving persistent data.

### Use of Enumerations
Enums such as UserRole, TaskStatus, TaskType, WishType, and WishStatus improve clarity and prevent incorrect attribute values.

### Logical Relationships
- Parent and Teacher classes reference lists of Children.
- Wish and Task objects reference their requesters or assignees.

---

## 3. Issues and Recommendations

### 3.1 Missing or Incomplete Multiplicity Notation
The diagram uses associations but lacks multiplicities (1..*, 0..1, etc.).

**Recommendation:**  
Add multiplicities for clarity, e.g.:
- Parent → Child: 1 → *
- Child → Task: 1 → *
- Teacher → Child: 1 → *

---

### 3.2 Inconsistent Naming and Typographical Errors
Some class names, attributes, and methods do not follow standard naming conventions.

Examples:
- `studentList` vs. `childList`  
- Method names mixing lowerCamelCase and inconsistent casing

**Recommendation:**  
Adopt consistent naming conventions across all classes.

---

### 3.3 Controller Responsibilities Are Overloaded
The GUIController includes too many responsibilities:
- UI handling  
- File interactions  
- User role logic  

**Recommendation:**  
Apply separation of concerns:
- Create a dedicated `SchedulerController` or `LogicController`
- Keep GUIController only for UI actions

---

### 3.4 Missing Associations for Some Entities
The Task and Wish entities are not fully connected to users:

Examples:
- Tasks should link clearly to the assigned Child and the approving Parent/Teacher.
- Wishes should link to both requester (Child) and approver (Parent/Teacher).

**Recommendation:**  
Explicitly include these associations in the diagram.

---

### 3.5 Missing System-Level Rule Classes
BeePlan has rules such as:
- Lab after theory
- Friday exam block
- Instructor max hours per day
- Elective conflict constraints

None of these rules appear structurally in the class diagram.

**Recommendation:**  
Introduce a `Constraint` or `RuleEngine` class to centralize validation logic.

---

### 3.6 Some Methods Lack Clear Responsibilities
Example:
- `calculateLevel()` is in the Child class, but level-up logic depends on task rating and approval.

**Recommendation:**  
Move multi-actor logic into controller or service classes.

---

## 4. Summary

The class diagram provides a clear foundation and demonstrates understanding of BeePlan’s structure. To fully meet UML and design quality expectations, the diagram should include multiplicities, refined associations, corrected naming conventions, and a clearer separation of responsibilities through additional controller or rule-handling classes.

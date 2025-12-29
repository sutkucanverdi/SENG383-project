# KidTask - Class/Package Plan & Data Format Specification

## 1. Package Structure (Java Swing)

```
kidtask/
├── Main.java                          # Application entry point
│
├── enums/
│   ├── UserRole.java                  # CHILD, PARENT, TEACHER
│   ├── TaskStatus.java                # PENDING, COMPLETED, APPROVED
│   └── TaskType.java                  # DAILY, WEEKLY
│
├── models/
│   ├── User.java                      # Abstract base class
│   ├── Child.java                     # Extends User
│   ├── Parent.java                    # Extends User
│   ├── Teacher.java                   # Extends User
│   ├── Task.java                      # Task entity
│   └── Wish.java                      # Wish entity
│
├── managers/
│   ├── DataManager.java               # File I/O operations
│   ├── TaskManager.java               # Task business logic
│   ├── WishManager.java               # Wish business logic
│   └── UserManager.java               # User management
│
├── gui/
│   ├── LoginScreen.java               # Role selection/login
│   ├── Dashboard.java                 # Main dashboard
│   ├── TaskPanel.java                 # Task management UI
│   ├── WishPanel.java                 # Wish management UI
│   ├── ProgressPanel.java             # Points/Level display
│   └── GUIController.java             # GUI event handling
│
├── exceptions/
│   ├── DataPersistenceException.java  # File I/O errors
│   ├── TaskNotFoundException.java     # Task not found
│   ├── WishNotFoundException.java    # Wish not found
│   └── InvalidDataException.java     # Data validation errors
│
└── utils/
    ├── DateUtils.java                 # Date formatting utilities
    └── ValidationUtils.java          # Input validation helpers
```

## 2. Data File Formats

### Format Specification
- **Delimiter**: Pipe (`|`) - More reliable than comma for text fields
- **Header**: First line contains column names
- **Encoding**: UTF-8
- **Empty values**: Represented as empty string between delimiters
- **Date format**: ISO-8601 (YYYY-MM-DD)
- **Boolean**: `true` or `false` (lowercase)

---

### 2.1 Users.txt Format

**Header:**
```
id|name|role|points|level|ratingSum|ratingCount
```

**Field Descriptions:**
- `id`: UUID string (unique identifier)
- `name`: User's full name (no pipe characters allowed)
- `role`: CHILD, PARENT, or TEACHER
- `points`: Integer (only for CHILD, 0 for others)
- `level`: Integer (only for CHILD, 0 for others)
- `ratingSum`: Double (only for CHILD, 0.0 for others)
- `ratingCount`: Integer (only for CHILD, 0 for others)

**Example Rows:**
```
id|name|role|points|level|ratingSum|ratingCount
a1b2c3d4-e5f6-7890-abcd-ef1234567890|Ali Yılmaz|CHILD|150|2|8.5|2
b2c3d4e5-f6a7-8901-bcde-f12345678901|Ayşe Yılmaz|PARENT|0|0|0.0|0
c3d4e5f6-a7b8-9012-cdef-123456789012|Mehmet Demir|TEACHER|0|0|0.0|0
d4e5f6a7-b8c9-0123-def0-234567890123|Zeynep Kaya|CHILD|75|1|4.0|1
```

---

### 2.2 Tasks.txt Format

**Header:**
```
id|title|description|dueDate|points|status|type|childId|rating
```

**Field Descriptions:**
- `id`: UUID string (unique identifier)
- `title`: Task title (no pipe characters allowed)
- `description`: Task description (no pipe characters allowed)
- `dueDate`: ISO-8601 date (YYYY-MM-DD)
- `points`: Integer (positive, reward points)
- `status`: PENDING, COMPLETED, or APPROVED
- `type`: DAILY or WEEKLY
- `childId`: UUID reference to child user
- `rating`: Double (1.0-5.0) or empty if not approved yet

**Example Rows:**
```
id|title|description|dueDate|points|status|type|childId|rating
t1a2b3c4-d5e6-7890-abcd-ef1234567890|Read 20 Pages|Read chapter 3 from science book|2025-12-15|50|PENDING|DAILY|a1b2c3d4-e5f6-7890-abcd-ef1234567890|
t2b3c4d5-e6f7-8901-bcde-f12345678901|Math Homework|Complete exercises 1-10|2025-12-16|75|COMPLETED|DAILY|a1b2c3d4-e5f6-7890-abcd-ef1234567890|
t3c4d5e6-f7a8-9012-cdef-123456789012|Weekly Project|Prepare presentation about planets|2025-12-20|100|APPROVED|WEEKLY|a1b2c3d4-e5f6-7890-abcd-ef1234567890|4.5
t4d5e6f7-a8b9-0123-def0-234567890123|Clean Room|Organize toys and books|2025-12-14|30|APPROVED|DAILY|d4e5f6a7-b8c9-0123-def0-234567890123|5.0
```

---

### 2.3 Wishes.txt Format

**Header:**
```
id|title|description|costPoints|minLevel|approved|requestedByChildId|approvedByUserId
```

**Field Descriptions:**
- `id`: UUID string (unique identifier)
- `title`: Wish title (no pipe characters allowed)
- `description`: Wish description (no pipe characters allowed)
- `costPoints`: Integer (positive, points required)
- `minLevel`: Integer (minimum level required, >= 1)
- `approved`: Boolean (true or false)
- `requestedByChildId`: UUID reference to child user
- `approvedByUserId`: UUID reference to approver (PARENT/TEACHER) or empty if not approved

**Example Rows:**
```
id|title|description|costPoints|minLevel|approved|requestedByChildId|approvedByUserId
w1a2b3c4-d5e6-7890-abcd-ef1234567890|New Story Book|Adventure series volume 1|80|1|false|a1b2c3d4-e5f6-7890-abcd-ef1234567890|
w2b3c4d5-e6f7-8901-bcde-f12345678901|Bicycle|Red mountain bike|200|2|false|a1b2c3d4-e5f6-7890-abcd-ef1234567890|
w3c4d5e6-f7a8-9012-cdef-123456789012|Movie Night|Watch favorite movie with family|50|1|true|d4e5f6a7-b8c9-0123-def0-234567890123|b2c3d4e5-f6a7-8901-bcde-f12345678901
w4d5e6f7-a8b9-0123-def0-234567890123|Art Supplies|Colored pencils and sketchbook|60|1|false|d4e5f6a7-b8c9-0123-def0-234567890123|
```

---

## 3. Model Class Field Specifications

### 3.1 User (Abstract Base Class)
```java
- id: String (final, UUID)
- name: String (required, not blank)
- role: UserRole (final, enum: CHILD, PARENT, TEACHER)
```

### 3.2 Child (extends User)
```java
- Inherited: id, name, role=CHILD
- points: int (>= 0, default: 0)
- level: int (>= 1, default: 1, calculated from points)
- ratingSum: double (>= 0, sum of all ratings)
- ratingCount: int (>= 0, number of ratings received)
- averageRating: double (calculated: ratingSum/ratingCount, 0 if count=0)
```

**Methods:**
- `addPoints(int)` - Adds points and updates level
- `spendPoints(int)` - Deducts points, returns success boolean
- `recordRating(double)` - Records rating (1-5), updates sum/count
- `getAverageRating()` - Calculates average rating
- `updateLevelFromPoints()` - Private: level = 1 + (points / 100)

### 3.3 Parent (extends User)
```java
- Inherited: id, name, role=PARENT
- (No additional fields)
```

### 3.4 Teacher (extends User)
```java
- Inherited: id, name, role=TEACHER
- (No additional fields)
```

### 3.5 Task
```java
- id: String (final, UUID)
- title: String (required, not blank)
- description: String (optional, can be empty)
- dueDate: LocalDate (required, not null)
- points: int (required, > 0)
- status: TaskStatus (enum: PENDING, COMPLETED, APPROVED)
- type: TaskType (enum: DAILY, WEEKLY)
- childId: String (required, UUID reference to Child)
- rating: Double (nullable, 1.0-5.0, only set when APPROVED)
```

**Lifecycle:**
1. Created → PENDING
2. Child marks complete → COMPLETED
3. Parent/Teacher approves → APPROVED (with rating)

### 3.6 Wish
```java
- id: String (final, UUID)
- title: String (required, not blank)
- description: String (optional, can be empty)
- costPoints: int (required, > 0)
- minLevel: int (required, >= 1)
- approved: boolean (default: false)
- requestedByChildId: String (required, UUID reference to Child)
- approvedByUserId: String (nullable, UUID reference to Parent/Teacher)
```

**Business Rules:**
- Child can only see wishes where `child.level >= wish.minLevel`
- Approval requires: child has enough points AND child.level >= minLevel
- When approved, child's points are deducted

---

## 4. Data Validation Rules

### User Validation
- Name: Required, non-blank, max 100 characters
- Role: Must be valid enum value
- Child points: >= 0
- Child level: >= 1

### Task Validation
- Title: Required, non-blank, max 200 characters
- Description: Max 1000 characters
- Points: > 0, max 1000
- Due date: Not null, not in past (for new tasks)
- Rating: If set, must be 1.0-5.0

### Wish Validation
- Title: Required, non-blank, max 200 characters
- Description: Max 1000 characters
- Cost points: > 0, max 10000
- Min level: >= 1, max 100

---

## 5. File I/O Error Handling

- **File not found**: Create file with header
- **Invalid format**: Throw `DataPersistenceException` with details
- **Missing fields**: Skip row, log warning
- **Invalid data**: Throw `InvalidDataException` with field name
- **IO errors**: Wrap in `DataPersistenceException`

---

## 6. Level Calculation Algorithm

```java
level = Math.max(1, 1 + (points / 100))
```

**Examples:**
- 0-99 points → Level 1
- 100-199 points → Level 2
- 200-299 points → Level 3
- etc.


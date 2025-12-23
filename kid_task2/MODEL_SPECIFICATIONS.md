# KidTask - Model Class Specifications

## Model Class Field Details

### 1. User (Abstract Base Class)

**Package:** `models`

**Fields:**
| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | `String` (final) | UUID format, non-null | Unique identifier |
| `name` | `String` | Required, non-blank, max 100 chars | User's full name |
| `role` | `UserRole` (final) | Enum: CHILD, PARENT, TEACHER | User role |

**Constructors:**
- `User(String name, UserRole role)` - Creates new user with auto-generated UUID
- `User(String id, String name, UserRole role)` - Creates user with existing ID

**Methods:**
- `getId(): String`
- `getName(): String`
- `setName(String name): void` - Validates non-blank
- `getRole(): UserRole`

---

### 2. Child (extends User)

**Package:** `models`

**Inherited Fields:**
- `id`, `name`, `role=CHILD`

**Additional Fields:**
| Field | Type | Constraints | Default | Description |
|-------|------|-------------|---------|-------------|
| `points` | `int` | >= 0 | 0 | Current point balance |
| `level` | `int` | >= 1 | 1 | Current level (calculated) |
| `ratingSum` | `double` | >= 0 | 0.0 | Sum of all ratings received |
| `ratingCount` | `int` | >= 0 | 0 | Number of ratings received |

**Constructors:**
- `Child(String name)` - New child with default values (0 points, level 1)
- `Child(String id, String name, int points, int level, double ratingSum, int ratingCount)` - Full constructor

**Methods:**
- `getPoints(): int`
- `setPoints(int points): void` - Validates >= 0
- `getLevel(): int`
- `setLevel(int level): void` - Validates >= 1
- `getRatingSum(): double`
- `getRatingCount(): int`
- `getAverageRating(): double` - Calculated: ratingSum/ratingCount (0 if count=0)
- `addPoints(int additionalPoints): void` - Adds points, auto-updates level
- `spendPoints(int cost): boolean` - Deducts points if sufficient, returns success
- `recordRating(double rating): void` - Validates 1-5, updates sum/count
- `updateLevelFromPoints(): void` - Private: level = 1 + (points / 100)

**Level Calculation:**
```
level = Math.max(1, 1 + (points / 100))
```

---

### 3. Parent (extends User)

**Package:** `models`

**Inherited Fields:**
- `id`, `name`, `role=PARENT`

**Additional Fields:** None

**Constructors:**
- `Parent(String name)` - Creates new parent
- `Parent(String id, String name)` - Creates parent with existing ID

**Methods:**
- Inherits all User methods

---

### 4. Teacher (extends User)

**Package:** `models`

**Inherited Fields:**
- `id`, `name`, `role=TEACHER`

**Additional Fields:** None

**Constructors:**
- `Teacher(String name)` - Creates new teacher
- `Teacher(String id, String name)` - Creates teacher with existing ID

**Methods:**
- Inherits all User methods

---

### 5. Task

**Package:** `models`

**Fields:**
| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | `String` (final) | UUID format, non-null | Unique identifier |
| `title` | `String` | Required, non-blank, max 200 chars | Task title |
| `description` | `String` | Optional, max 1000 chars | Task description |
| `dueDate` | `LocalDate` | Required, non-null | Due date (ISO-8601) |
| `points` | `int` | Required, > 0, max 1000 | Reward points |
| `status` | `TaskStatus` | Enum: PENDING, COMPLETED, APPROVED | Current status |
| `type` | `TaskType` | Enum: DAILY, WEEKLY | Task type |
| `childId` | `String` | Required, UUID reference | Owner child ID |
| `rating` | `Double` | Nullable, 1.0-5.0 if set | Rating (only when APPROVED) |

**Constructors:**
- `Task(String title, String description, LocalDate dueDate, int points, TaskType type, String childId)` - Creates PENDING task
- `Task(String id, String title, String description, LocalDate dueDate, int points, TaskStatus status, TaskType type, String childId, Double rating)` - Full constructor

**Methods:**
- `getId(): String`
- `getTitle(): String`
- `setTitle(String title): void` - Validates non-blank
- `getDescription(): String`
- `setDescription(String description): void` - Can be empty
- `getDueDate(): LocalDate`
- `setDueDate(LocalDate dueDate): void` - Validates non-null
- `getPoints(): int`
- `setPoints(int points): void` - Validates > 0
- `getStatus(): TaskStatus`
- `setStatus(TaskStatus status): void` - Validates non-null
- `getType(): TaskType`
- `setType(TaskType type): void` - Validates non-null
- `getChildId(): String`
- `setChildId(String childId): void` - Validates non-null
- `getRating(): Double`
- `setRating(Double rating): void` - Validates 1.0-5.0 if not null

**Status Lifecycle:**
1. **PENDING** - Task created, not completed
2. **COMPLETED** - Child marked as done, awaiting approval
3. **APPROVED** - Parent/Teacher approved with rating, points awarded

**Business Rules:**
- Only CHILD can mark task as COMPLETED
- Only PARENT or TEACHER can approve (set to APPROVED)
- Rating can only be set when status is APPROVED
- When approved, child receives points and rating is recorded

---

### 6. Wish

**Package:** `models`

**Fields:**
| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | `String` (final) | UUID format, non-null | Unique identifier |
| `title` | `String` | Required, non-blank, max 200 chars | Wish title |
| `description` | `String` | Optional, max 1000 chars | Wish description |
| `costPoints` | `int` | Required, > 0, max 10000 | Points required |
| `minLevel` | `int` | Required, >= 1, max 100 | Minimum level required |
| `approved` | `boolean` | Default: false | Approval status |
| `requestedByChildId` | `String` | Required, UUID reference | Requesting child ID |
| `approvedByUserId` | `String` | Nullable, UUID reference | Approver ID (PARENT/TEACHER) |

**Constructors:**
- `Wish(String title, String description, int costPoints, int minLevel, String requestedByChildId)` - Creates unapproved wish
- `Wish(String id, String title, String description, int costPoints, int minLevel, boolean approved, String requestedByChildId, String approvedByUserId)` - Full constructor

**Methods:**
- `getId(): String`
- `getTitle(): String`
- `setTitle(String title): void` - Validates non-blank
- `getDescription(): String`
- `setDescription(String description): void` - Can be empty
- `getCostPoints(): int`
- `setCostPoints(int costPoints): void` - Validates > 0
- `getMinLevel(): int`
- `setMinLevel(int minLevel): void` - Validates >= 1
- `isApproved(): boolean`
- `setApproved(boolean approved): void`
- `getRequestedByChildId(): String`
- `setRequestedByChildId(String requestedByChildId): void` - Validates non-null
- `getApprovedByUserId(): String`
- `setApprovedByUserId(String approvedByUserId): void` - Can be null

**Business Rules:**
- Child can only see wishes where `child.level >= wish.minLevel`
- Approval requires:
  1. `child.level >= wish.minLevel`
  2. `child.points >= wish.costPoints`
- When approved:
  - `approved` set to `true`
  - `approvedByUserId` set to approver's ID
  - Child's points deducted (`spendPoints()`)
- Only PARENT or TEACHER can approve wishes

---

## Enum Types

### UserRole
```java
CHILD    // Child user
PARENT   // Parent user
TEACHER  // Teacher user
```

### TaskStatus
```java
PENDING   // Task created, not completed
COMPLETED // Task completed by child, awaiting approval
APPROVED  // Task approved by parent/teacher with rating
```

### TaskType
```java
DAILY   // Daily task
WEEKLY  // Weekly task
```

---

## Validation Summary

### Common Validations
- **UUID format**: Must match UUID pattern
- **Non-null**: Required fields cannot be null
- **Non-blank**: String fields cannot be empty or whitespace-only
- **String length**: Enforced max lengths

### Child Validations
- Points: >= 0
- Level: >= 1
- Rating: 1.0 - 5.0 (when recording)

### Task Validations
- Points: > 0, max 1000
- Due date: Not null, not in past (for new tasks)
- Rating: 1.0 - 5.0 (when set)

### Wish Validations
- Cost points: > 0, max 10000
- Min level: >= 1, max 100

---

## Data Relationships

```
User (abstract)
├── Child (1) ──< (many) Task
│                └── childId references Child.id
│
├── Child (1) ──< (many) Wish
│                └── requestedByChildId references Child.id
│
├── Parent (1) ──< (many) Task (approver)
│                └── approves via TaskManager.approveTask()
│
├── Parent (1) ──< (many) Wish (approver)
│                └── approvedByUserId references Parent.id
│
└── Teacher (1) ──< (many) Task (approver)
                 └── approves via TaskManager.approveTask()
```

---

## Key Algorithms

### Level Calculation
```java
level = Math.max(1, 1 + (points / 100))
```

### Average Rating Calculation
```java
averageRating = (ratingCount == 0) ? 0 : (ratingSum / ratingCount)
```

### Wish Availability Check
```java
isAvailable = (child.level >= wish.minLevel)
```

### Wish Approval Check
```java
canApprove = (child.level >= wish.minLevel) && (child.points >= wish.costPoints)
```


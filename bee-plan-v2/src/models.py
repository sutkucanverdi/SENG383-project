from dataclasses import dataclass, field
from typing import List, Tuple

@dataclass
class Course:
    code: str
    name: str
    instructor: str
    year: int
    semester: int
    theory_hours: int
    lab_hours: int
    students: int = 0

    is_fixed: bool = False
    fixed_slots: List[list] = field(default_factory=list)
    
    # This will be populated by the scheduling engine
    assigned_slots: List[Tuple[int, int, str]] = field(default_factory=list)

    @property
    def department(self) -> str:
        """Derives the department from the course code (e.g., 'SENG' from 'SENG 101')."""
        return self.code.split()[0]

    def reset(self):
        """Resets the schedule for this course back to its initial state."""
        if not self.is_fixed:
            self.assigned_slots = []
        else:
            # Format fixed slots from [day, hour, type] to (day, hour, type)
            self.assigned_slots = [(s[0], s[1], s[2]) for s in self.fixed_slots]

@dataclass
class Room:
    name: str
    capacity: int
    type: str # "Lab" or "Classroom"
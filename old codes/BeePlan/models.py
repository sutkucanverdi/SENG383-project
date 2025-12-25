# models.py
from consts import DAYS

class Instructor:
    def __init__(self, id, name):
        self.id = id
        self.name = name
        self.daily_load = {day: 0 for day in DAYS}

    def can_teach(self, day, duration):
        if self.daily_load[day] + duration > 4:
            return False
        return True

    def add_load(self, day, duration):
        self.daily_load[day] += duration

    def remove_load(self, day, duration):
        self.daily_load[day] -= duration
    
    # JSON Dönüşümleri
    def to_dict(self):
        return {"id": self.id, "name": self.name}

    @classmethod
    def from_dict(cls, data):
        return cls(data["id"], data["name"])


class Room:
    def __init__(self, id, capacity, is_lab=False):
        self.id = id
        self.capacity = capacity
        self.is_lab = is_lab 

    def __repr__(self):
        return f"{self.id}"

    # JSON Dönüşümleri
    def to_dict(self):
        return {"id": self.id, "capacity": self.capacity, "is_lab": self.is_lab}

    @classmethod
    def from_dict(cls, data):
        return cls(data["id"], data["capacity"], data["is_lab"])


class Course:
    def __init__(self, code, name, instructor_id, duration, students, year, is_lab=False, theory_course_code=None):
        self.code = code
        self.name = name
        self.instructor_id = instructor_id
        self.duration = duration
        self.students = students
        self.year = year
        self.is_lab = is_lab
        self.theory_course_code = theory_course_code 

    def __repr__(self):
        return f"{self.code} (Y{self.year})"

    # JSON Dönüşümleri
    def to_dict(self):
        return {
            "code": self.code,
            "name": self.name,
            "instructor_id": self.instructor_id,
            "duration": self.duration,
            "students": self.students,
            "year": self.year,
            "is_lab": self.is_lab,
            "theory_course_code": self.theory_course_code
        }

    @classmethod
    def from_dict(cls, data):
        return cls(
            data["code"], data["name"], data["instructor_id"],
            data["duration"], data["students"], data["year"],
            data.get("is_lab", False), data.get("theory_course_code", None)
        )
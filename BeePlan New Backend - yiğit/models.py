class Instructor:
    def __init__(self, id, name):
        self.id = id
        self.name = name


class Room:
    def __init__(self, id, name, capacity, is_lab=False):
        self.id = id
        self.name = name
        self.capacity = capacity
        self.is_lab = is_lab


class Course:
    def __init__(self, code, name, instructor_id, duration, year, is_lab=False, theory_course_code=None):
        self.code = code
        self.name = name
        self.instructor_id = instructor_id
        self.duration = duration
        self.year = year
        self.is_lab = is_lab
        self.theory_course_code = theory_course_code

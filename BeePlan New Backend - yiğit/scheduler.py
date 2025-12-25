from consts import DAYS, TIMESLOTS, FRIDAY_BLOCKED_INDICES, MAX_DAILY_THEORY


class BeePlanScheduler:
    def __init__(self, courses, rooms, instructors):
        self.courses = courses
        self.rooms = rooms
        self.instructors = instructors

        self.schedule = {}
        self.instructor_usage = {}
        self.year_usage = {}

    def is_valid(self, course, day, slot, room):
        # Friday restriction
        if DAYS[day] == "Friday" and slot in FRIDAY_BLOCKED_INDICES:
            return False

        # Room type match
        if room.is_lab != course.is_lab:
            return False

        # Room occupied
        if (day, slot, room.id) in self.schedule:
            return False

        # Instructor conflict
        if (day, slot, course.instructor_id) in self.instructor_usage:
            return False

        # Same year conflict
        if (day, slot, course.year) in self.year_usage:
            return False

        # Instructor daily limit
        if not course.is_lab:
            count = sum(
                1 for (d, s, i) in self.instructor_usage
                if d == day and i == course.instructor_id
            )
            if count >= MAX_DAILY_THEORY:
                return False

        return True

    def solve(self):
        tasks = []
        for c in self.courses:
            for _ in range(c.duration):
                tasks.append(c)

        tasks.sort(key=lambda c: c.is_lab)

        return self._backtrack(tasks)

    def _backtrack(self, tasks):
        if not tasks:
            return True

        course = tasks[0]

        for d in range(len(DAYS)):
            for s in range(len(TIMESLOTS)):
                for room in self.rooms:
                    if self.is_valid(course, d, s, room):
                        self.schedule[(d, s, room.id)] = course
                        self.instructor_usage[(d, s, course.instructor_id)] = course
                        self.year_usage[(d, s, course.year)] = course

                        if self._backtrack(tasks[1:]):
                            return True

                        del self.schedule[(d, s, room.id)]
                        del self.instructor_usage[(d, s, course.instructor_id)]
                        del self.year_usage[(d, s, course.year)]

        return False

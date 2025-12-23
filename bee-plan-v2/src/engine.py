import random

import random

class SchedulerEngine:
    def __init__(self, courses):
        self.courses = courses
        self.grid = {}  # (Year, Day, Hour) -> Course
        self.instructor_grid = {}  # (Instructor, Day, Hour) -> (Course, Type)
        # YENİ: Gruplar arası çakışmaları yönetmek için grid
        # Gruplar: 'YEAR_3', 'CENG_ELECTIVE', 'SENG_ELECTIVE'
        self.cross_group_grid = {} # (Day, Hour) -> Set of group names
        self.tasks = []

        self.reset_grids()

    def _get_course_groups(self, course):
        """Bir dersin ait olduğu özel çakışma gruplarını döndürür."""
        groups = set()
        if course.year == 3:
            groups.add('YEAR_3')
        
        # Seçmelilerin 4. sınıf dersleri olduğunu varsayıyoruz
        if course.year == 4:
            if course.department == 'CENG':
                groups.add('CENG_ELECTIVE')
            elif course.department == 'SENG':
                groups.add('SENG_ELECTIVE')
        return groups

    def reset_grids(self):
        self.grid = {}
        self.instructor_grid = {}
        self.cross_group_grid = {}

        for c in self.courses:
            c.reset()
            if c.is_fixed:
                for slot in c.fixed_slots:
                    day, hour, type_ = slot[0], slot[1], slot[2]
                    self._mark(c, day, hour, type_, is_fixed_allocation=True)

    def _mark(self, course, day, hour, type_, is_fixed_allocation=False):
        self.grid[(course.year, day, hour)] = course
        self.instructor_grid[(course.instructor, day, hour)] = (course, type_)
        
        # Grup çakışma grid'ini güncelle
        groups = self._get_course_groups(course)
        if groups:
            slot_groups = self.cross_group_grid.setdefault((day, hour), set())
            slot_groups.update(groups)

        if not is_fixed_allocation:
            course.assigned_slots.append((day, hour, type_))

    def _unmark(self, course, day, hour):
        if (course.year, day, hour) in self.grid:
            del self.grid[(course.year, day, hour)]
        if (course.instructor, day, hour) in self.instructor_grid:
            del self.instructor_grid[(course.instructor, day, hour)]

        # Grup çakışma grid'ini temizle
        groups = self._get_course_groups(course)
        if groups:
            slot_groups = self.cross_group_grid.get((day, hour))
            if slot_groups:
                slot_groups.difference_update(groups)
                if not slot_groups: # Set boşaldıysa sil
                    del self.cross_group_grid[(day, hour)]

        for i, slot in enumerate(course.assigned_slots):
            if slot[0] == day and slot[1] == hour:
                course.assigned_slots.pop(i)
                return

    def is_safe(self, course, day, hour, type_) -> bool:
        # KURAL 1: Cuma yasağı
        if day == 4 and hour in [4, 5]:
            return False

        # KURAL 2: Sınıf çakışması
        if (course.year, day, hour) in self.grid:
            return False

        # KURAL 3: Hoca çakışması
        if (course.instructor, day, hour) in self.instructor_grid:
            return False

        # KURAL: Hoca günlük 4 saat teori limiti
        if type_ == 'T':
            daily_theory_hours = 0
            for h in range(8):
                slot_info = self.instructor_grid.get((course.instructor, day, h))
                if slot_info and slot_info[1] == 'T':
                    daily_theory_hours += 1
            if daily_theory_hours >= 4:
                return False

        # KURAL: Lab sırası
        if type_ == 'L' and course.theory_hours > 0 and not any(s[2] == 'T' for s in course.assigned_slots):
            return False
            
        # YENİ GRUP KURALLARI
        course_groups = self._get_course_groups(course)
        existing_groups = self.cross_group_grid.get((day, hour), set())

        if not course_groups or not existing_groups:
            return True # Gruplar arası çakışma yoksa devam et

        # KURAL 4: 3. Sınıf vs Seçmeli
        is_year_3 = 'YEAR_3' in course_groups
        is_elective = 'CENG_ELECTIVE' in course_groups or 'SENG_ELECTIVE' in course_groups
        
        has_year_3 = 'YEAR_3' in existing_groups
        has_elective = 'CENG_ELECTIVE' in existing_groups or 'SENG_ELECTIVE' in existing_groups

        if (is_year_3 and has_elective) or (is_elective and has_year_3):
            return False

        # KURAL 5: CENG vs SENG Seçmeli
        is_ceng = 'CENG_ELECTIVE' in course_groups
        is_seng = 'SENG_ELECTIVE' in course_groups
        has_ceng = 'CENG_ELECTIVE' in existing_groups
        has_seng = 'SENG_ELECTIVE' in existing_groups

        if (is_ceng and has_seng) or (is_seng and has_ceng):
            return False

        return True

    def solve(self):
        self.reset_grids()

        to_schedule = [c for c in self.courses if not c.is_fixed]
        to_schedule.sort(key=lambda x: (x.theory_hours + x.lab_hours, x.lab_hours), reverse=True)

        self.tasks = []
        for c in to_schedule:
            for _ in range(c.theory_hours):
                self.tasks.append((c, 'T'))
            for _ in range(c.lab_hours):
                self.tasks.append((c, 'L'))

        print(f"Toplam atanacak saat sayısı: {len(self.tasks)}")
        return self._backtrack(0)

    def _backtrack(self, index):
        if index == len(self.tasks):
            return True

        course, type_ = self.tasks[index]

        days = list(range(5))
        hours = list(range(8))
        
        for d in days:
            for h in hours:
                if self.is_safe(course, d, h, type_):
                    self._mark(course, d, h, type_)
                    if self._backtrack(index + 1):
                        return True
                    self._unmark(course, d, h)
        return False
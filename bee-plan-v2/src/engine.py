import random

import random

class SchedulerEngine:
    def __init__(self, courses, rooms):
        self.courses = courses
        self.rooms = rooms
        self.grid = {}  # (Year, Day, Hour) -> Course
        self.instructor_grid = {}  # (Instructor, Day, Hour) -> (Course, Type)
        self.cross_group_grid = {} # (Day, Hour) -> Set of group names
        
        # ODA YÖNETİMİ İÇİN YENİ GRİDLER
        self.room_grid = {} # (Day, Hour) -> Set of occupied room names
        self.lab_rooms = [r for r in self.rooms if r.type == 'Lab']

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
        self.room_grid = {}

        for c in self.courses:
            c.reset()
            if c.is_fixed:
                for slot in c.fixed_slots:
                    day, hour, type_ = slot[0], slot[1], slot[2]
                    self._mark(c, day, hour, type_, is_fixed_allocation=True)

    def _mark(self, course, day, hour, type_, room=None, is_fixed_allocation=False):
        self.grid[(course.year, day, hour)] = course
        self.instructor_grid[(course.instructor, day, hour)] = (course, type_)
        
        groups = self._get_course_groups(course)
        if groups:
            self.cross_group_grid.setdefault((day, hour), set()).update(groups)

        # Oda ataması
        if room:
            self.room_grid.setdefault((day, hour), set()).add(room.name)

        if not is_fixed_allocation:
            # Laboratuvarlar için odayı da kaydet
            slot_data = (day, hour, type_, room.name if room else None)
            course.assigned_slots.append(slot_data)

    def _unmark(self, course, day, hour, type_, room=None):
        if (course.year, day, hour) in self.grid:
            del self.grid[(course.year, day, hour)]
        if (course.instructor, day, hour) in self.instructor_grid:
            del self.instructor_grid[(course.instructor, day, hour)]

        groups = self._get_course_groups(course)
        if groups:
            if (day, hour) in self.cross_group_grid:
                self.cross_group_grid[(day, hour)].difference_update(groups)
                if not self.cross_group_grid[(day, hour)]:
                    del self.cross_group_grid[(day, hour)]
        
        # Oda temizliği
        if room:
            if (day, hour) in self.room_grid:
                self.room_grid[(day, hour)].remove(room.name)
                if not self.room_grid[(day, hour)]:
                    del self.room_grid[(day, hour)]

        # Atanmış slotlardan kaldır
        for i, slot in enumerate(course.assigned_slots):
            if slot[0] == day and slot[1] == hour:
                course.assigned_slots.pop(i)
                return

    def is_safe(self, course, day, hour, type_, room=None) -> bool:
        # KURAL 1: Cuma yasağı (13:20 ve 14:20 saatleri) - GEÇİCİ OLARAK DEVRE DIŞI
        # if day == 4 and hour in [4, 5]:
        #     return False

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
                entry = self.instructor_grid.get((course.instructor, day, h))
                if entry and entry[1] == 'T':
                    daily_theory_hours += 1
            if daily_theory_hours >= 4:
                return False

        # KURAL: Lab sırası (Lab dersi teori dersinden sonra olmalı)
        if type_ == 'L' and course.theory_hours > 0:
            has_theory_before = any(
                s[2] == 'T' and (s[0] < day or (s[0] == day and s[1] < hour))
                for s in course.assigned_slots
            )
            if not has_theory_before:
                return False
        
        # ODA KISITLARI (Sadece laboratuvarlar için)
        if type_ == 'L':
            if not room: # Oda belirtilmemişse, bu bir sorun
                return False
            # KURAL: Oda kapasitesi
            if course.students > room.capacity:
                return False
            # KURAL: Oda çakışması
            occupied_rooms = self.room_grid.get((day, hour), set())
            if room.name in occupied_rooms:
                return False

        # GRUP KURALLARI (3. Sınıf vs Seçmeli & CENG vs SENG Seçmeli)
        course_groups = self._get_course_groups(course)
        if course_groups:
            existing_groups = self.cross_group_grid.get((day, hour), set())
            if existing_groups:
                # 3. Sınıf ve Seçmeli Çakışması
                is_year_3_course = 'YEAR_3' in course_groups
                is_elective_course = 'CENG_ELECTIVE' in course_groups or 'SENG_ELECTIVE' in course_groups
                has_year_3_slot = 'YEAR_3' in existing_groups
                has_elective_slot = 'CENG_ELECTIVE' in existing_groups or 'SENG_ELECTIVE' in existing_groups

                if (is_year_3_course and has_elective_slot) or (is_elective_course and has_year_3_slot):
                    return False
                
                # CENG ve SENG Seçmeli Çakışması
                is_ceng_elective = 'CENG_ELECTIVE' in course_groups
                is_seng_elective = 'SENG_ELECTIVE' in course_groups
                has_ceng_slot = 'CENG_ELECTIVE' in existing_groups
                has_seng_slot = 'SENG_ELECTIVE' in existing_groups

                if (is_ceng_elective and has_seng_slot) or (is_seng_elective and has_ceng_slot):
                    return False

        return True

    def solve(self):
        self.reset_grids()

        to_schedule = [c for c in self.courses if not c.is_fixed]
        # Önceliklendirme: En çok saatlik dersler ve lab'ı olanlar önce
        to_schedule.sort(key=lambda x: (x.theory_hours + x.lab_hours, x.lab_hours), reverse=True)

        self.tasks = []
        for c in to_schedule:
            # Önce teoriler, sonra lablar
            for _ in range(c.theory_hours):
                self.tasks.append((c, 'T'))
            for _ in range(c.lab_hours):
                self.tasks.append((c, 'L'))
        
        return self._backtrack(0)

    def _backtrack(self, index):
        if index == len(self.tasks):
            return True

        course, type_ = self.tasks[index]

        days = list(range(5))
        hours = list(range(8))
        random.shuffle(days) # Çözüm uzayını rastgele karıştır
        random.shuffle(hours)

        for d in days:
            for h in hours:
                if type_ == 'T':
                    if self.is_safe(course, d, h, 'T'):
                        self._mark(course, d, h, 'T')
                        if self._backtrack(index + 1):
                            return True
                        self._unmark(course, d, h, 'T')
                elif type_ == 'L':
                    # Uygun bir laboratuvar odası ara
                    for room in self.lab_rooms:
                        if self.is_safe(course, d, h, 'L', room=room):
                            self._mark(course, d, h, 'L', room=room)
                            if self._backtrack(index + 1):
                                return True
                            self._unmark(course, d, h, 'L', room=room)
        return False
# scheduler.py
from consts import DAYS, TIME_SLOTS, FRIDAY_BLOCKED_INDICES

class BeePlanScheduler:
    def __init__(self, courses, rooms, instructors):
        self.courses = courses
        self.rooms = rooms
        self.instructors = {i.id: i for i in instructors}
        self.schedule = {d: {t: {r.id: None for r in rooms} for t in range(len(TIME_SLOTS))} for d in DAYS}
        self.assignments = {} 

    def _is_valid(self, course, day, start_slot, room):
        # 1. Süre Sınırı
        if start_slot + course.duration > len(TIME_SLOTS): return False

        # 2. Cuma Yasağı
        if day == "Friday":
            for i in range(course.duration):
                if (start_slot + i) in FRIDAY_BLOCKED_INDICES: return False

        # 3. Kapasite ve Tip
        if course.students > room.capacity: return False
        if course.is_lab != room.is_lab: return False

        # 4. Oda Doluluğu
        for i in range(course.duration):
            if self.schedule[day][start_slot + i][room.id] is not None: return False

        # 5. Hoca Çakışması
        instructor = self.instructors[course.instructor_id]
        for i in range(course.duration):
            current_slot = start_slot + i
            for other_room in self.rooms:
                existing = self.schedule[day][current_slot][other_room.id]
                if existing and existing.instructor_id == course.instructor_id: return False
        
        if not course.is_lab:
            if not instructor.can_teach(day, course.duration): return False

        # 6. Lab Sıralaması
        if course.is_lab and course.theory_course_code:
            if course.theory_course_code not in self.assignments: return False
            theory_day, theory_slot = self.assignments[course.theory_course_code]
            days_list = list(DAYS)
            if days_list.index(day) < days_list.index(theory_day): return False
            if day == theory_day and start_slot <= theory_slot: return False

        # --- YENİ EKLENEN KURAL ---
        # 7. Öğrenci Yıl Çakışması (Year Conflict)
        # Aynı saatte, aynı yıl (örn: 1. sınıf) için başka bir ders var mı?
        for i in range(course.duration):
            current_slot = start_slot + i
            for other_room in self.rooms:
                existing = self.schedule[day][current_slot][other_room.id]
                # Eğer o saatte başka ders varsa VE yılları aynıysa -> ÇAKIŞMA!
                if existing and existing.year == course.year:
                    return False
        
        return True

    def solve(self):
        to_assign = [c for c in self.courses if c.code not in self.assignments]
        if not to_assign: return True

        to_assign.sort(key=lambda x: x.is_lab)
        course = to_assign[0]

        for day in DAYS:
            for slot in range(len(TIME_SLOTS)):
                for room in self.rooms:
                    if self._is_valid(course, day, slot, room):
                        self.assignments[course.code] = (day, slot)
                        instructor = self.instructors[course.instructor_id]
                        if not course.is_lab: instructor.add_load(day, course.duration)
                        for i in range(course.duration):
                            self.schedule[day][slot + i][room.id] = course

                        if self.solve(): return True

                        del self.assignments[course.code]
                        if not course.is_lab: instructor.remove_load(day, course.duration)
                        for i in range(course.duration):
                            self.schedule[day][slot + i][room.id] = None
        return False

    def generate_validation_report(self):
        """PDF Madde 13: Validation Reports"""
        print("\n" + "#"*30)
        print("   DOĞRULAMA RAPORU (VALIDATION)")
        print("#"*30)
        
        issues = []
        checks = {
            "Capacity": 0,
            "Instructor Overlap": 0,
            "Year Conflict": 0,
            "Friday Rule": 0
        }

        # Tüm programı tara
        for day in DAYS:
            for slot in range(len(TIME_SLOTS)):
                courses_in_slot = []
                instructors_in_slot = []
                years_in_slot = []

                for room in self.rooms:
                    course = self.schedule[day][slot][room.id]
                    if course:
                        courses_in_slot.append(course)
                        
                        # Check 1: Kapasite
                        if course.students > room.capacity:
                            issues.append(f"[KAPASİTE HATASI] {course.code} ({course.students} öğr) > {room.id} ({room.capacity})")
                        else:
                            checks["Capacity"] += 1
                        
                        # Check 2: Cuma Kuralı
                        if day == "Friday" and slot in FRIDAY_BLOCKED_INDICES:
                            issues.append(f"[CUMA KURALI HATASI] {course.code} yasaklı saatte!")
                        else:
                            checks["Friday Rule"] += 1

                        # Listelere ekle (Çakışma kontrolü için)
                        instructors_in_slot.append(course.instructor_id)
                        years_in_slot.append(course.year)

                # Check 3: Hoca Çakışması
                if len(instructors_in_slot) != len(set(instructors_in_slot)):
                    issues.append(f"[HOCA ÇAKIŞMASI] {day} {TIME_SLOTS[slot]} saatinde aynı hoca birden fazla derste!")
                else:
                    checks["Instructor Overlap"] += 1

                # Check 4: Yıl Çakışması
                if len(years_in_slot) != len(set(years_in_slot)):
                    # Hangi yıl çakıştı?
                    seen = set()
                    dupes = [x for x in years_in_slot if x in seen or seen.add(x)]
                    issues.append(f"[YIL ÇAKIŞMASI] {day} {TIME_SLOTS[slot]} - {dupes[0]}. sınıfların iki dersi var!")
                else:
                    checks["Year Conflict"] += 1

        if not issues:
            print("✅ TÜM KONTROLLER BAŞARILI!")
            print(f"- Kapasite Kontrolleri: {checks['Capacity']} OK")
            print(f"- Hoca Çakışma Kontrolü: {checks['Instructor Overlap']} blok OK")
            print(f"- Öğrenci Yıl Çakışması: {checks['Year Conflict']} blok OK")
            print(f"- Cuma Yasağı: {checks['Friday Rule']} OK")
        else:
            print("❌ HATALAR BULUNDU:")
            for issue in issues:
                print(issue)

    def print_schedule_by_year(self):
        """PDF Madde 12: 1-4. Yıllar için program"""
        print("\n" + "="*50)
        print("YILLARA GÖRE DERS PROGRAMI (1. - 4. Sınıf)")
        print("="*50)
        
        # 1'den 4'e kadar yılları dön
        for year in range(1, 5):
            print(f"\n>>> {year}. SINIF PROGRAMI <<<")
            print(f"{'GÜN':<10} | {'SAAT':<10} | {'DERS KODU':<10} | {'ODA':<10}")
            print("-" * 46)
            
            has_course = False
            for day in DAYS:
                for slot_idx, slot_name in enumerate(TIME_SLOTS):
                    for room in self.rooms:
                        course = self.schedule[day][slot_idx][room.id]
                        # Sadece o yıla ait dersleri yazdır
                        if course and course.year == year:
                            print(f"{day:<10} | {slot_name:<10} | {course.code:<10} | {room.id:<10}")
                            has_course = True
            
            if not has_course:
                print("   (Bu sınıf için ders bulunamadı)")
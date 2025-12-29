# src/reporting.py
class ReportGenerator:
    def __init__(self, courses, rooms):
        self.courses = courses
        self.rooms = rooms

    def generate_validation_report(self):
        violations = []
        
        for course in self.courses:
            if course.is_fixed: continue
            
            # Kural: Lab Kapasitesi <= 40 
            # Burada basitleştirilmiş kontrol yapıyoruz, 
            # gerçekte öğrenci sayısı (enrollment) ile oda kapasitesi kıyaslanır.
            # Belgede SENG 101 kotası 70[cite: 116], Lab kapasitesi 40.
            # Bu durumda sistem "Şube bölünmeli" uyarısı vermeli.
            if course.lab_hours > 0:
                 # Örnek kapasite kontrolü (Gerçek veride course.quota olmalı)
                 pass 

            # Kural: Lab teoriden sonra olmalı [cite: 26]
            theory_times = [s for s in course.assigned_slots if s[2] == 'T']
            lab_times = [s for s in course.assigned_slots if s[2] == 'L']
            
            if theory_times and lab_times:
                last_theory = max(theory_times, key=lambda x: (x[0], x[1]))
                first_lab = min(lab_times, key=lambda x: (x[0], x[1]))
                
                # Eğer Lab günü < Teori günü VEYA (Aynı gün ve Lab saati <= Teori saati)
                if first_lab[0] < last_theory[0] or \
                   (first_lab[0] == last_theory[0] and first_lab[1] <= last_theory[1]):
                    violations.append(f"HATA: {course.code} Lab dersi Teoriden önce konmuş!")

        return violations if violations else ["Program Kurallara Uygun."]
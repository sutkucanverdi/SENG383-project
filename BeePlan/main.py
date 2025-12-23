import sys
import json
from PyQt6.QtWidgets import (QApplication, QMainWindow, QWidget, QVBoxLayout, 
                             QHBoxLayout, QTableWidget, QTableWidgetItem, 
                             QPushButton, QLabel, QComboBox, QMessageBox, QHeaderView)
from PyQt6.QtCore import Qt
from PyQt6.QtGui import QColor

# --- VERİ YAPILARI VE MODELLEME ---

class Course:
    def __init__(self, code, name, theory_hours, lab_hours, instructor, year, is_fixed=False):
        self.code = code
        self.name = name
        self.theory_hours = theory_hours
        self.lab_hours = lab_hours
        self.instructor = instructor
        self.year = year
        self.is_fixed = is_fixed
        self.assigned_slots = [] # Format: (Day, Hour, Type='T' or 'L')

class Instructor:
    def __init__(self, name):
        self.name = name
        self.daily_theory_load = {day: 0 for day in range(5)} # Pazartesi=0 ... Cuma=4

# --- SABİT VERİLER (Belgelerden Çıkarılan) ---
# Günler ve Saatler
DAYS = ["Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma"]
HOURS = ["09:20", "10:20", "11:20", "12:20", "13:20", "14:20", "15:20", "16:20"]

# Müfredat ve Hoca Verileri [Kaynak: 93, 112, 114, 116, 118]
def get_initial_data():
    courses = []
    
    # 1. Sınıf Sabit Dersler (Belgedeki Tablolardan [Kaynak: 96])
    # Sabit dersleri manuel olarak "assigned" işaretliyoruz.
    c_phys131 = Course("PHYS 131", "Physics I", 3, 2, "Physics Dept", 1, is_fixed=True)
    c_phys131.assigned_slots = [(0, 0, 'T'), (2, 0, 'T'), (2, 2, 'L'), (2, 3, 'L')] # Örnek slotlar
    courses.append(c_phys131)
    
    # SENG Bölüm Dersleri (Gerçek Veriler)
    # [Kaynak: 116] S.Esmelioglu
    courses.append(Course("SENG 101", "Computer Programming I", 3, 2, "S.Esmelioglu", 1))
    courses.append(Course("SENG 303", "Software Testing", 3, 0, "S.Esmelioglu", 3))
    
    # [Kaynak: 114] B.Çelikkale
    courses.append(Course("SENG 201", "Data Structures", 3, 2, "B.Celikkale", 2))
    courses.append(Course("SENG 426", "Formal Methods", 4, 0, "B.Celikkale", 4))
    
    # [Kaynak: 112] B.Avenoglu
    courses.append(Course("SENG 315", "Concurrent Programming", 3, 0, "B.Avenoglu", 3))
    
    # [Kaynak: 118] S.K. Tunç
    courses.append(Course("SENG 206", "Software Design", 3, 0, "S.K.Tunc", 2))
    courses.append(Course("SENG 301", "Software Project Mgmt", 2, 2, "S.K.Tunc", 3))

    return courses

# --- SCHEDULING ENGINE (ALGORİTMA) ---
# Backtracking yaklaşımı [Kaynak: 31, 32]

class Scheduler:
    def __init__(self, courses):
        self.courses = courses
        self.schedule_grid = {} # Key: (Year, Day, Hour), Value: CourseCode
        self.instructor_schedule = {} # Key: (Instructor, Day, Hour), Value: CourseCode

        # Mevcut sabit dersleri grid'e işle
        for course in self.courses:
            if course.is_fixed:
                for day, hour_idx, type_ in course.assigned_slots:
                    self.mark_slot(course, day, hour_idx, type_)

    def is_conflict(self, course, day, hour, is_theory):
        # 1. KURAL: Ortak Sınav Saati (Cuma 13:20-15:10) [Kaynak: 24, 68]
        # Cuma = 4. gün. 13:20 (index 4) ve 14:20 (index 5)
        if day == 4 and hour in [4, 5]:
            return True

        # 2. KURAL: Çakışma Kontrolü (Aynı sınıf seviyesi aynı anda dolu mu?)
        if (course.year, day, hour) in self.schedule_grid:
            return True

        # 3. KURAL: Hoca Müsaitliği [Kaynak: 42, 58]
        if (course.instructor, day, hour) in self.instructor_schedule:
            return True
            
        # 4. KURAL: Hoca Günlük Teori Yükü Max 4 Saat [Kaynak: 25, 69]
        if is_theory:
            current_load = 0
            # Basit bir kontrol: o gün hocanın kaç saati var say
            for h in range(len(HOURS)):
                if (course.instructor, day, h) in self.instructor_schedule:
                     # Detaylı implementasyonda dersin tipine bakılmalı, şimdilik basit sayıyoruz
                     current_load += 1
            if current_load >= 4:
                return True

        return False

    def mark_slot(self, course, day, hour, type_):
        self.schedule_grid[(course.year, day, hour)] = f"{course.code} ({type_})"
        self.instructor_schedule[(course.instructor, day, hour)] = course.code
        course.assigned_slots.append((day, hour, type_))

    def unmark_slot(self, course, day, hour):
        if (course.year, day, hour) in self.schedule_grid:
            del self.schedule_grid[(course.year, day, hour)]
        if (course.instructor, day, hour) in self.instructor_schedule:
            del self.instructor_schedule[(course.instructor, day, hour)]
        # assigned_slots listesinden silme işlemi eklenebilir

    def solve(self):
        # Sadece yerleşmemiş dersleri al
        courses_to_schedule = [c for c in self.courses if not c.is_fixed and not c.assigned_slots]
        return self.backtrack(courses_to_schedule)

    def backtrack(self, remaining_courses):
        if not remaining_courses:
            return True

        course = remaining_courses[0]
        
        # Basitlik için sadece Teori saatlerini yerleştirmeyi deneyelim
        # Gerçek uygulamada Lab saatleri Teori'den sonra gelmeli kuralı (constraint) eklenir [Kaynak: 26, 62]
        needed_slots = course.theory_hours
        
        # Tüm gün ve saatleri dene
        for d in range(5):
            for h in range(len(HOURS) - needed_slots + 1):
                # Ardışık saat kontrolü (Blok ders)
                slots_ok = True
                for i in range(needed_slots):
                    if self.is_conflict(course, d, h+i, True):
                        slots_ok = False
                        break
                
                if slots_ok:
                    # Yerleştir
                    for i in range(needed_slots):
                        self.mark_slot(course, d, h+i, 'T')
                    
                    if self.backtrack(remaining_courses[1:]):
                        return True
                    
                    # Backtrack (Geri al)
                    for i in range(needed_slots):
                        self.unmark_slot(course, d, h+i)
                        
        return False

# --- GUI (ARAYÜZ) ---
# [Kaynak: 17, 19, 20, 21]

class BeePlanApp(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("BeePlan - Çankaya Üniversitesi Ders Programı Sistemi")
        self.setGeometry(100, 100, 1200, 800)
        
        # Veri Hazırlığı
        self.courses = get_initial_data()
        self.scheduler = Scheduler(self.courses)

        self.init_ui()

    def init_ui(self):
        main_widget = QWidget()
        layout = QVBoxLayout()
        
        # Başlık ve Kontroller
        control_layout = QHBoxLayout()
        
        self.year_combo = QComboBox()
        self.year_combo.addItems(["1. Sınıf", "2. Sınıf", "3. Sınıf", "4. Sınıf"])
        self.year_combo.currentIndexChanged.connect(self.update_table)
        
        btn_generate = QPushButton("Otomatik Program Oluştur (Generate Schedule)")
        btn_generate.setStyleSheet("background-color: #F4D03F; font-weight: bold; padding: 10px;") # Arı Sarısı
        btn_generate.clicked.connect(self.run_scheduler)
        
        btn_export = QPushButton("Dışa Aktar (JSON)")
        
        control_layout.addWidget(QLabel("Görüntülenen Sınıf:"))
        control_layout.addWidget(self.year_combo)
        control_layout.addStretch()
        control_layout.addWidget(btn_generate)
        control_layout.addWidget(btn_export)
        
        layout.addLayout(control_layout)

        # Ders Programı Tablosu [Kaynak: 19]
        self.table = QTableWidget()
        self.table.setRowCount(len(HOURS))
        self.table.setColumnCount(len(DAYS))
        self.table.setHorizontalHeaderLabels(DAYS)
        self.table.setVerticalHeaderLabels(HOURS)
        self.table.horizontalHeader().setSectionResizeMode(QHeaderView.ResizeMode.Stretch)
        
        layout.addWidget(self.table)
        
        # Durum Çubuğu
        self.status_label = QLabel("Hazır. Veriler yüklendi.")
        layout.addWidget(self.status_label)

        main_widget.setLayout(layout)
        self.setCentralWidget(main_widget)
        
        # İlk tabloyu boş göster
        self.update_table()

    def run_scheduler(self):
        self.status_label.setText("Algoritma çalışıyor...")
        QApplication.processEvents()
        
        success = self.scheduler.solve()
        
        if success:
            self.status_label.setText("Çözüm bulundu! Program güncelleniyor.")
            QMessageBox.information(self, "Başarılı", "Çakışmasız ders programı oluşturuldu!")
            self.update_table()
        else:
            self.status_label.setText("Hata: Uygun bir program bulunamadı.")
            QMessageBox.warning(self, "Hata", "Mevcut kısıtlarla çakışmasız program oluşturulamadı.\nLütfen kısıtları gevşetin.")

    def update_table(self):
        self.table.clearContents()
        selected_year = self.year_combo.currentIndex() + 1
        
        # Cuma 13:20-15:10 Blokunu Boya [Kaynak: 24]
        exam_hours = [4, 5] # 13:20, 14:20 indices
        exam_day = 4 # Friday
        
        for r in range(self.table.rowCount()):
            for c in range(self.table.columnCount()):
                # Hücreyi temizle
                self.table.setItem(r, c, QTableWidgetItem(""))
                
                # Cuma Yasağını Görselleştir [Kaynak: 20]
                if c == exam_day and r in exam_hours:
                    item = QTableWidgetItem("ORTAK SINAV")
                    item.setBackground(QColor("#E74C3C")) # Kırmızı
                    item.setForeground(QColor("white"))
                    item.setTextAlignment(Qt.AlignmentFlag.AlignCenter)
                    self.table.setItem(r, c, item)

        # Programı Tabloya İşle
        grid = self.scheduler.schedule_grid
        for (year, day, hour), course_info in grid.items():
            if year == selected_year:
                item = QTableWidgetItem(course_info)
                item.setBackground(QColor("#ABEBC6")) # Yeşil
                item.setTextAlignment(Qt.AlignmentFlag.AlignCenter)
                self.table.setItem(hour, day, item)

if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = BeePlanApp()
    window.show()
    sys.exit(app.exec())
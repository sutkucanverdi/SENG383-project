from PyQt6.QtWidgets import (QMainWindow, QWidget, QVBoxLayout, QHBoxLayout, 
                             QTableWidget, QTableWidgetItem, QPushButton, 
                             QLabel, QComboBox, QMessageBox, QHeaderView, QFrame)
from PyQt6.QtGui import QColor, QFont, QIcon
from PyQt6.QtCore import Qt, QSize
from ..engine import SchedulerEngine
from ..database import DataLoader
from ..reporting import ReportGenerator
from ..exporter import Exporter

# --- MODERN VE TAM KOYU TEMA STİL DOSYASI (QSS) ---
STYLE_SHEET = """
QMainWindow {
    background-color: #2C3E50; /* Ana Arka Plan */
}
QWidget {
    font-family: 'Segoe UI', sans-serif;
    font-size: 14px;
    color: #ECF0F1; /* Genel Yazı Rengi */
}

/* --- YAN MENÜ (SIDEBAR) --- */
QFrame#SideBar {
    background-color: #34495E;
    border-right: 1px solid #22313F;
    min-width: 220px;
}
QLabel#LogoLabel {
    font-size: 24px;
    font-weight: bold;
    color: #F1C40F; /* Arı Sarısı Logo */
    padding: 25px 10px;
    qproperty-alignment: AlignCenter;
}

/* --- SINIF SEÇİM KUTUSU (COMBOBOX) - DÜZELTİLDİ --- */
QComboBox {
    background-color: #2C3E50;   /* Sidebar'dan biraz daha koyu */
    color: #F1C40F;              /* Yazı Rengi Sarı */
    border: 1px solid #5D6D7E;   /* İnce Gri Çerçeve */
    border-radius: 6px;
    padding: 10px;
    padding-left: 15px;
    font-weight: bold;
    min-width: 150px;
}
QComboBox:hover {
    border: 1px solid #F1C40F;   /* Üzerine gelince Sarı Çerçeve */
    background-color: #22313F;
}
QComboBox::drop-down {
    border: none;
    background: transparent;
    width: 30px;
}
/* Açılır Liste Kısmı */
QComboBox QAbstractItemView {
    background-color: #34495E;   /* Liste Arka Planı */
    color: #ECF0F1;              /* Liste Yazıları */
    border: 1px solid #F1C40F;
    selection-background-color: #F1C40F; /* Seçili Olan Sarı */
    selection-color: #2C3E50;    /* Seçili Yazı Koyu */
    outline: none;
}

/* --- BUTONLAR --- */
QPushButton {
    background-color: #2980B9;
    color: white;
    border: none;
    padding: 12px;
    border-radius: 6px;
    text-align: left;
    padding-left: 20px;
    font-weight: 600;
    margin-bottom: 5px;
}
QPushButton:hover {
    background-color: #3498DB;
    padding-left: 25px; /* Hover animasyonu efekti */
}
QPushButton#RunButton {
    background-color: #F1C40F; /* Ana Aksiyon Butonu */
    color: #2C3E50;
    text-align: center;
    padding-left: 0;
    font-size: 16px;
    font-weight: bold;
    margin-top: 20px;
    border: 2px solid #F1C40F;
}
QPushButton#RunButton:hover {
    background-color: #F39C12;
    border-color: #F39C12;
    color: white;
}

/* --- TABLO TASARIMI --- */
QTableWidget {
    background-color: #ECF0F1; /* Tablo içi açık renk kalsın (okunabilirlik için) */
    color: #2C3E50;
    gridline-color: #BDC3C7;
    border: none;
    border-radius: 8px;
}
QHeaderView::section {
    background-color: #2C3E50;
    color: white;
    padding: 12px;
    font-weight: bold;
    border: none;
    border-bottom: 2px solid #F1C40F; /* Başlık altı sarı çizgi */
}
QScrollBar:vertical {
    background: #2C3E50;
    width: 12px;
}
QScrollBar::handle:vertical {
    background: #7F8C8D;
    border-radius: 6px;
}
"""

class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("BeePlan - Akademik Dönem Destekli")
        self.resize(1280, 800)
        self.setStyleSheet(STYLE_SHEET)
        
        # 1. Verileri Yükle
        self.loader = DataLoader()
        self.all_courses = self.loader.courses # DataLoader'dan hazır al
        self.rooms = self.loader.rooms
        
        # 2. Varsayılan Dönem Ayarı (Güz)
        self.current_semester = 1 
        self.courses = [] # Başlangıçta boş, filter fonksiyonu dolduracak
        self.engine = None
        
        # 3. Filtreleme Yap (Motoru ve self.courses'u hazırlar)
        self.filter_courses_by_semester() 
        
        # 4. Arayüzü Çiz
        self.init_ui()

    def filter_courses_by_semester(self):
        """
        Tüm dersler arasından sadece seçili döneme ait olanları ayıklar
        ve aktif çalışma listesine (self.courses) atar.
        """
        # KRİTİK DÜZELTME: self.courses değişkenini burada güncelliyoruz.
        self.courses = [c for c in self.all_courses if c.semester == self.current_semester]
        
        # Motoru sadece bu derslerle ve odalarla yeniden başlat
        self.engine = SchedulerEngine(self.courses, self.rooms)

    def init_ui(self):
        central_widget = QWidget()
        main_layout = QHBoxLayout()
        main_layout.setContentsMargins(0, 0, 0, 0)
        main_layout.setSpacing(0)
        
        # --- SOL MENÜ (SIDEBAR) ---
        sidebar = QFrame()
        sidebar.setObjectName("SideBar")
        sidebar_layout = QVBoxLayout()
        sidebar_layout.setContentsMargins(10, 20, 10, 20)
        sidebar_layout.setSpacing(15)
        
        # Logo
        lbl_logo = QLabel("🐝 BeePlan")
        lbl_logo.setObjectName("LogoLabel")
        sidebar_layout.addWidget(lbl_logo)
        
        sidebar_layout.addSpacing(20)

        # Dönem Seçimi
        lbl_term = QLabel("Akademik Dönem:")
        lbl_term.setStyleSheet("color: #BDC3C7; font-size: 12px; margin-left: 5px;")
        self.combo_term = QComboBox()
        self.combo_term.addItems(["GÜZ DÖNEMİ (Fall)", "BAHAR DÖNEMİ (Spring)"])
        self.combo_term.currentIndexChanged.connect(self.on_semester_change)
        
        # Sınıf Seçimi
        lbl_year = QLabel("Sınıf Seviyesi:")
        lbl_year.setStyleSheet("color: #BDC3C7; font-size: 12px; margin-left: 5px;")
        self.combo_year = QComboBox()
        self.combo_year.addItems(["1. Sınıf", "2. Sınıf", "3. Sınıf", "4. Sınıf"])
        self.combo_year.currentIndexChanged.connect(self.update_table)
        
        sidebar_layout.addWidget(lbl_term)
        sidebar_layout.addWidget(self.combo_term)
        sidebar_layout.addSpacing(10)
        sidebar_layout.addWidget(lbl_year)
        sidebar_layout.addWidget(self.combo_year)
        
        sidebar_layout.addSpacing(20)
        
        # Butonlar
        btn_report = QPushButton("📋 Raporları İncele")
        btn_report.clicked.connect(self.show_report)
        
        btn_export = QPushButton("💾 Dışa Aktar (JSON)")
        btn_export.clicked.connect(self.export_data)
        
        btn_run = QPushButton("⚡ Programı Oluştur")
        btn_run.setObjectName("RunButton")
        btn_run.clicked.connect(self.run_algorithm)
        
        sidebar_layout.addWidget(btn_report)
        sidebar_layout.addWidget(btn_export)
        sidebar_layout.addWidget(btn_run)
        sidebar_layout.addStretch()
        
        lbl_footer = QLabel("v1.0 - Çankaya Univ.")
        lbl_footer.setStyleSheet("color: #7F8C8D; font-size: 11px; qproperty-alignment: AlignCenter;")
        sidebar_layout.addWidget(lbl_footer)
        
        sidebar.setLayout(sidebar_layout)
        main_layout.addWidget(sidebar)
        
        # --- SAĞ İÇERİK ---
        content_widget = QWidget()
        content_layout = QVBoxLayout()
        content_layout.setContentsMargins(20, 20, 20, 20)
        
        self.lbl_status = QLabel("Ders Programı Bekleniyor...")
        self.lbl_status.setStyleSheet("font-size: 18px; color: #34495E; font-weight: bold; background-color: #ECF0F1; padding: 10px; border-radius: 5px;")
        content_layout.addWidget(self.lbl_status)
        
        self.table = QTableWidget(8, 5)
        self.days = ["Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma"]
        self.hours = ["09:20", "10:20", "11:20", "12:20", "13:20", "14:20", "15:20", "16:20"]
        self.table.setHorizontalHeaderLabels(self.days)
        self.table.setVerticalHeaderLabels(self.hours)
        self.table.horizontalHeader().setSectionResizeMode(QHeaderView.ResizeMode.Stretch)
        self.table.verticalHeader().setSectionResizeMode(QHeaderView.ResizeMode.Stretch)
        self.table.setEditTriggers(QTableWidget.EditTrigger.NoEditTriggers)
        self.table.setFocusPolicy(Qt.FocusPolicy.NoFocus)
        
        content_layout.addWidget(self.table)
        content_widget.setLayout(content_layout)
        main_layout.addWidget(content_widget, stretch=1)
        
        central_widget.setLayout(main_layout)
        self.setCentralWidget(central_widget)
        
        self.update_table()

    def on_semester_change(self):
        idx = self.combo_term.currentIndex()
        self.current_semester = idx + 1 
        
        self.lbl_status.setText(f"⏳ {self.combo_term.currentText()} yükleniyor...")
        self.lbl_status.setStyleSheet("color: #E67E22; background-color: #FDEBD0; font-size: 18px; font-weight: bold; padding: 10px; border-radius: 5px;")

        self.filter_courses_by_semester()
        
        # Tabloyu temizle
        self.engine.reset_grids() 
        self.update_table()
        self.lbl_status.setText("Dönem değişti. Lütfen 'Programı Oluştur'a basın.")

    def run_algorithm(self):
        self.lbl_status.setText("⏳ Algoritma en uygun programı hesaplıyor...")
        from PyQt6.QtWidgets import QApplication
        QApplication.processEvents()
        
        success = self.engine.solve()
        
        if success:
            self.lbl_status.setText("✅ Çakışmasız Program Oluşturuldu!")
            self.lbl_status.setStyleSheet("color: #27AE60; background-color: #D5F5E3; font-size: 18px; font-weight: bold; padding: 10px; border-radius: 5px;")
            self.update_table()
        else:
            self.lbl_status.setText("❌ Çözüm Bulunamadı (Kısıtları Kontrol Edin)")
            self.lbl_status.setStyleSheet("color: #C0392B; background-color: #FADBD8; font-size: 18px; font-weight: bold; padding: 10px; border-radius: 5px;")
            QMessageBox.warning(self, "Hata", "Mevcut kısıtlarla çözüm bulunamadı.")

    def show_report(self):
        # Artık self.courses dolu olduğu için hata vermeyecek
        reporter = ReportGenerator(self.courses, [])
        violations = reporter.generate_validation_report()
        msg = "\n".join(violations)
        QMessageBox.information(self, "Validasyon Raporu", msg if msg else "Harika! Hiçbir kural ihlali yok.")

    def export_data(self):
        fname = Exporter.to_json(self.courses)
        QMessageBox.information(self, "Dışa Aktar", f"Veriler başarıyla kaydedildi:\n{fname}")

    def update_table(self):
        self.table.clearContents()
        target_year = self.combo_year.currentIndex() + 1
        
        # Cuma Sınav Saatini İşaretle
        for r in [4, 5]: 
            item = QTableWidgetItem("ORTAK SINAV")
            item.setBackground(QColor("#C0392B"))
            item.setForeground(QColor("white"))
            item.setTextAlignment(Qt.AlignmentFlag.AlignCenter)
            item.setFont(QFont("Segoe UI", 10, QFont.Weight.Bold))
            self.table.setItem(r, 4, item)

        # Dersleri Yerleştir
        for (year, day, hour), course in self.engine.grid.items():
            if year == target_year:
                slot_info = None
                # Atanmış veya sabitlenmiş slotu bul
                for s in course.assigned_slots:
                    if s[0] == day and s[1] == hour:
                        slot_info = s
                        break
                if not slot_info and course.is_fixed:
                     for s in course.fixed_slots:
                        if s[0] == day and s[1] == hour:
                            # fixed_slots formatı: [day, hour, type]
                            slot_info = (s[0], s[1], s[2], None) # Oda bilgisi yok
                            break
                
                if not slot_info: continue

                ctype = slot_info[2]
                room_name = slot_info[3] if len(slot_info) > 3 else None

                text = f"{course.code}\n({ctype}) - {course.instructor}"
                if ctype == 'L' and room_name:
                    text = f"{course.code}\n({ctype} @ {room_name})\n{course.instructor}"

                item = QTableWidgetItem(text)
                item.setTextAlignment(Qt.AlignmentFlag.AlignCenter)
                item.setFont(QFont("Segoe UI", 9))
                
                # Renklendirme
                if ctype == 'L': 
                    item.setBackground(QColor("#5DADE2")) # Mavi
                    item.setForeground(QColor("white"))
                else: 
                    item.setBackground(QColor("#58D68D")) # Yeşil
                    item.setForeground(QColor("#2C3E50"))
                    
                self.table.setItem(hour, day, item)
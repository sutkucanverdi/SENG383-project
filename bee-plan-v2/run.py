import sys
from PyQt6.QtWidgets import QApplication
# src klasöründen import edebilmek için
from src.ui.main_window import MainWindow

if __name__ == "__main__":
    app = QApplication(sys.argv)
    
    # Stil (Opsiyonel: Daha güzel görünüm için)
    app.setStyle("Fusion")
    
    window = MainWindow()
    window.show()
    sys.exit(app.exec())
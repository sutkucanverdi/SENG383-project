# src/main.py
import sys
from PyQt6.QtWidgets import QApplication
from src.ui.main_window import MainWindow

def main():
    # Uygulama örneğini oluştur
    app = QApplication(sys.argv)
    
    # Ana pencereyi yükle
    window = MainWindow()
    window.show()
    
    # Olay döngüsünü (Event Loop) başlat
    sys.exit(app.exec())

if __name__ == "__main__":
    main()
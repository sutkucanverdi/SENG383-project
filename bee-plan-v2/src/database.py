import json
import os
from .models import Course

class DataLoader:
    def __init__(self, data_path="data/courses.json"):
        # "data" klasörünü bulmak için mutlak yol kullanalım
        base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
        self.data_path = os.path.join(base_dir, "data", "courses.json")

    def load_courses(self) -> list[Course]:
        if not os.path.exists(self.data_path):
            # Dosya yoksa boş liste yerine hata fırlatmayalım, UI halletsin
            print(f"UYARI: {self.data_path} bulunamadı.")
            return []
            
        try:
            with open(self.data_path, "r", encoding="utf-8") as f:
                data = json.load(f)
                courses = []
                for item in data:
                    # JSON verisini Course objesine çevir
                    c = Course(**item)
                    
                    # Kritik Düzeltme: Fixed derslerin assigned_slots'unu fixed_slots'dan doldurma
                    # Bu işlem artık Engine içinde yapılıyor (Reset Grids), 
                    # burada sadece ham veriyi yüklüyoruz.
                    c.assigned_slots = [] 
                    courses.append(c)
                print(f"{len(courses)} ders başarıyla yüklendi.")
                return courses
        except Exception as e:
            print(f"Veri yükleme hatası: {e}")
            return []
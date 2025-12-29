import json
import os
from .models import Course, Room

class DataLoader:
    def __init__(self, courses_path="data/courses.json", rooms_path="data/rooms.json"):
        base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
        self.courses_path = os.path.join(base_dir, "data", "courses.json")
        self.rooms_path = os.path.join(base_dir, "data", "rooms.json")
        
        self.courses = self.load_courses()
        self.rooms = self.load_rooms()

    def load_courses(self) -> list[Course]:
        if not os.path.exists(self.courses_path):
            print(f"UYARI: {self.courses_path} bulunamadı.")
            return []
            
        try:
            with open(self.courses_path, "r", encoding="utf-8") as f:
                data = json.load(f)
                courses = [Course(**item) for item in data]
                print(f"{len(courses)} ders başarıyla yüklendi.")
                return courses
        except Exception as e:
            print(f"Ders yükleme hatası: {e}")
            return []

    def load_rooms(self) -> list[Room]:
        if not os.path.exists(self.rooms_path):
            print(f"UYARI: {self.rooms_path} bulunamadı.")
            return []
            
        try:
            with open(self.rooms_path, "r", encoding="utf-8") as f:
                data = json.load(f)
                rooms = [Room(**item) for item in data]
                print(f"{len(rooms)} oda başarıyla yüklendi.")
                return rooms
        except Exception as e:
            print(f"Oda yükleme hatası: {e}")
            return []
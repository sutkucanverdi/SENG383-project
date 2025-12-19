import json
from models import Instructor, Room, Course
from consts import DAYS, TIME_SLOTS

class DataManager:
    @staticmethod
    def load_input_data(filename):
        """JSON dosyasından tüm verileri okur ve nesne listelerine çevirir."""
        try:
            with open(filename, 'r', encoding='utf-8') as f:
                data = json.load(f)
            
            instructors = [Instructor.from_dict(d) for d in data.get("instructors", [])]
            rooms = [Room.from_dict(d) for d in data.get("rooms", [])]
            courses = [Course.from_dict(d) for d in data.get("courses", [])]
            
            print(f"[INFO] {len(courses)} ders, {len(rooms)} oda, {len(instructors)} eğitmen yüklendi.")
            return instructors, rooms, courses
        except FileNotFoundError:
            print(f"[ERROR] '{filename}' dosyası bulunamadı!")
            return [], [], []
        except Exception as e:
            print(f"[ERROR] Veri yükleme hatası: {e}")
            return [], [], []

    @staticmethod
    def export_schedule_to_json(schedule_obj, filename="schedule_output.json"):
        """Oluşan programı JSON formatında dışarı aktarır."""
        output_data = []

        # schedule yapısı: [GUN][SAAT][ODA] -> Course
        for day in DAYS:
            for slot_idx, time_str in enumerate(TIME_SLOTS):
                for room_id in schedule_obj.schedule[day][slot_idx]:
                    course = schedule_obj.schedule[day][slot_idx][room_id]
                    if course:
                        # Kayıt oluştur
                        entry = {
                            "day": day,
                            "time": time_str,
                            "room": room_id,
                            "course_code": course.code,
                            "course_name": course.name,
                            "instructor_id": course.instructor_id,
                            "year": course.year
                        }
                        # Tekrarları önlemek için basit kontrol (Blok dersler her saat için yazılır)
                        output_data.append(entry)
        
        try:
            with open(filename, 'w', encoding='utf-8') as f:
                json.dump(output_data, f, indent=4, ensure_ascii=False)
            print(f"[INFO] Program '{filename}' dosyasına kaydedildi.")
        except Exception as e:
            print(f"[ERROR] Kaydetme hatası: {e}")
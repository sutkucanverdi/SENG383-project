# src/exporter.py
import json
import csv

class Exporter:
    @staticmethod
    def to_json(courses, filename="schedule_output.json"):
        data = []
        for c in courses:
            data.append({
                "code": c.code,
                "instructor": c.instructor,
                "schedule": c.assigned_slots  # [(day, hour, type), ...]
            })
        
        with open(filename, "w", encoding="utf-8") as f:
            json.dump(data, f, ensure_ascii=False, indent=4)
        return filename

    @staticmethod
    def to_csv(courses, filename="schedule_output.csv"):
        # Gün ve Saat bazlı basit CSV
        with open(filename, "w", newline='', encoding="utf-8") as f:
            writer = csv.writer(f)
            writer.writerow(["Course Code", "Instructor", "Day (0=Mon)", "Hour", "Type"])
            for c in courses:
                for slot in c.assigned_slots:
                    writer.writerow([c.code, c.instructor, slot[0], slot[1], slot[2]])
        return filename
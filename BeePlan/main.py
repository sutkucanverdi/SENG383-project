# main.py
from scheduler import BeePlanScheduler
from data_manager import DataManager

def main():
    print("BeePlan Başlatılıyor...")

    # --- 1. GİRDİLER (JSON Dosyasından) ---
    input_file = "input_data.json"
    print(f"Veriler '{input_file}' dosyasından okunuyor...")
    
    instructors, rooms, courses = DataManager.load_input_data(input_file)

    if not courses:
        print("Ders verisi bulunamadı, program sonlandırılıyor.")
        return

    # --- 2. ÇÖZÜM (Process) ---
    engine = BeePlanScheduler(courses, rooms, instructors)
    
    print("Program hesaplanıyor...")
    success = engine.solve()

    # --- 3. ÇIKTI (Output & Export) ---
    if success:
        print("✅ Çözüm Bulundu!")
        
        # Konsol Raporları
        engine.generate_validation_report()
        engine.print_schedule_by_year()
        
        # JSON Olarak Kaydet (Yeni Özellik)
        DataManager.export_schedule_to_json(engine, "output_schedule.json")
        
    else:
        print("❌ Çözüm Bulunamadı! Kısıtlamalar çok sıkı veya veri hatalı.")

if __name__ == "__main__":
    main()
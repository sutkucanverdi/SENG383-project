from data_manager import get_mock_data
from scheduler import BeePlanScheduler
from consts import DAYS, TIMESLOTS

def main():
    instructors, rooms, courses = get_mock_data()
    scheduler = BeePlanScheduler(courses, rooms, instructors)

    if scheduler.solve():
        print("✅ Program oluşturuldu!\n")
        for (d, s, r), course in sorted(scheduler.schedule.items()):
            print(f"{DAYS[d]} | {TIMESLOTS[s]} | {r} -> {course.code}")
    else:
        print("❌ Program oluşturulamadı")

if __name__ == "__main__":
    main()

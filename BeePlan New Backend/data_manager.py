from models import Instructor, Room, Course

def get_mock_data():
    instructors = [
        Instructor(1, "Dr. Alice"),
        Instructor(2, "Dr. Bob"),
        Instructor(3, "Dr. Charlie")
    ]

    rooms = [
        Room("R101", "Room 101", 50),
        Room("R102", "Room 102", 50),
        Room("LAB1", "Computer Lab", 40, True)
    ]

    courses = [
        Course("CSE101", "Intro to CS", 1, 3, 1),
        Course("CSE101L", "Intro to CS Lab", 1, 2, 1, True, "CSE101"),
        Course("CSE202", "Data Structures", 2, 3, 2),
        Course("CSE303", "Algorithms", 3, 3, 3),
        Course("CSE401", "Final Project", 2, 2, 4)
    ]

    return instructors, rooms, courses

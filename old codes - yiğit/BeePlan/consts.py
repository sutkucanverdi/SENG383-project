# consts.py

DAYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"]

# Günlük 9 saatlik dilim (08:30 - 17:20 arası)
TIME_SLOTS = [
    "08:30", "09:30", "10:30", "11:30", 
    "13:20", "14:20", "15:20", "16:20", "17:20"
]

# Cuma günü yasaklı saatlerin indeksleri (13:20 ve 14:20)
# TIME_SLOTS listesindeki 4. ve 5. indeksler
FRIDAY_BLOCKED_INDICES = [4, 5]
# KidTask Project - Planning Documents

Bu klasör KidTask projesinin planlama dokümantasyonunu içerir.

## 📁 Dosyalar

### 1. `PROJECT_PLAN.md`
- **Package yapısı** (Java Swing için)
- **Dosya formatları** (Users.txt, Tasks.txt, Wishes.txt)
- **Delimiter formatı** (Pipe `|` kullanımı)
- **Örnek satırlar** ve header'lar
- **Data validation kuralları**

### 2. `MODEL_SPECIFICATIONS.md`
- **Model sınıflarının detaylı alanları**
- **Constructor'lar ve method'lar**
- **Validation kuralları**
- **Business logic kuralları**
- **İlişkiler ve algoritmalar**

### 3. `examples/` Klasörü
- `Users.txt` - Örnek kullanıcı verileri
- `Tasks.txt` - Örnek görev verileri
- `Wishes.txt` - Örnek istek verileri

## 🎯 Hızlı Özet

### Package Yapısı
```
kidtask/
├── Main.java
├── enums/          (UserRole, TaskStatus, TaskType)
├── models/         (User, Child, Parent, Teacher, Task, Wish)
├── managers/       (DataManager, TaskManager, WishManager, UserManager)
├── gui/            (LoginScreen, Dashboard, TaskPanel, WishPanel, etc.)
├── exceptions/     (DataPersistenceException, TaskNotFoundException, etc.)
└── utils/          (DateUtils, ValidationUtils)
```

### Dosya Formatları

**Delimiter:** Pipe (`|`)  
**Encoding:** UTF-8  
**Date Format:** ISO-8601 (YYYY-MM-DD)

#### Users.txt
```
id|name|role|points|level|ratingSum|ratingCount
```

#### Tasks.txt
```
id|title|description|dueDate|points|status|type|childId|rating
```

#### Wishes.txt
```
id|title|description|costPoints|minLevel|approved|requestedByChildId|approvedByUserId
```

### Model Özeti

- **User** (abstract): id, name, role
- **Child**: + points, level, ratingSum, ratingCount
- **Parent**: No additional fields
- **Teacher**: No additional fields
- **Task**: id, title, description, dueDate, points, status, type, childId, rating
- **Wish**: id, title, description, costPoints, minLevel, approved, requestedByChildId, approvedByUserId

## 📋 Sonraki Adımlar

1. ✅ Package/Class planı hazır
2. ✅ Dosya formatları belirlendi
3. ✅ Model alanları netleştirildi
4. ⏭️ Kod implementasyonu (Coding Task1)


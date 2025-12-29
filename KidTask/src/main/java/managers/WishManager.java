package managers;

import models.Child;
import models.Wish;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class WishManager {
    private final DataManager dataManager;
    private final Map<String, Wish> wishesById = new HashMap<>();

    public WishManager(DataManager dataManager) {
        this.dataManager = Objects.requireNonNull(dataManager, "dataManager");
        load();
    }

    private void load() {
        wishesById.clear();
        dataManager.loadWishes().forEach(wish -> wishesById.put(wish.getId(), wish));
    }

    public Wish addWish(String title, String description, int costPoints, int minLevel, String childId) {
        Wish wish = new Wish(title, description, costPoints, minLevel, childId);
        wishesById.put(wish.getId(), wish);
        persist();
        return wish;
    }

    public List<Wish> listWishes() {
        return wishesById.values().stream().toList();
    }

    public List<Wish> listAvailableForChild(Child child) {
        return wishesById.values().stream()
                .filter(w -> child.getLevel() >= w.getMinLevel())
                .collect(Collectors.toList());
    }

    public boolean approveWish(String wishId, Child child, String approverUserId) {
        Wish wish = wishesById.get(wishId);
        if (wish == null) {
            return false;
        }
        if (wish.isApproved()) {
            return true; // already approved
        }
        if (child.getLevel() < wish.getMinLevel()) {
            return false;
        }
        if (!child.spendPoints(wish.getCostPoints())) {
            return false;
        }
        wish.setApproved(true);
        wish.setApprovedByUserId(approverUserId);
        persist();
        return true;
    }

    private void persist() {
        dataManager.saveWishes(wishesById.values());
    }
}


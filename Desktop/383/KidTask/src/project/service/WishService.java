package project.service;

import project.model.Wish;
import project.util.JsonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WishService {

    private final String FILE = "data/wishes.json";
    private List<Wish> wishes = new ArrayList<>();

    private final UserService userService;

    public WishService(UserService u) {
        this.userService = u;
        loadWishes();
    }

    private void loadWishes() {
        File f = new File(FILE);

        if (!f.exists()) {
            wishes = new ArrayList<>();
            saveWishes();
            return;
        }

        wishes = JsonUtils.load(FILE, JsonUtils.listOf(Wish.class));
        if (wishes == null) wishes = new ArrayList<>();
    }

    private void saveWishes() {
        JsonUtils.save(FILE, wishes);
    }

    // ---------------- KID ACTIONS ----------------
    public void addWishFromKid(String kidId, String name, String desc) {
    int id = wishes.size() + 1;

    Wish w = new Wish(
            id,
            name,
            desc,
            0,          // requiredPoints
            1,          // requiredLevel
            false,      // approved
            false,      // purchased
            kidId
    );

    wishes.add(w);
    saveWishes();
}


    public List<Wish> getWishesOfKid(String kidId) {
        List<Wish> result = new ArrayList<>();

        for (Wish w : wishes) {
            if (w.getKidId().equals(kidId))
                result.add(w);
        }
        return result;
    }

    public boolean purchaseWish(String kidId, int wishId) {
        for (Wish w : wishes) {
            if (w.getId() == wishId &&
                w.getKidId().equals(kidId) &&
                w.isApproved() &&
                !w.isPurchased()) {

                var kid = userService.getUserById(kidId);

                if (kid.getPoints() >= w.getRequiredPoints() &&
                    kid.getLevel() >= w.getRequiredLevel()) {

                    kid.addPoints(-w.getRequiredPoints());
                    userService.saveUsers();

                    w.setPurchased(true);
                    saveWishes();
                    return true;
                }
            }
        }
        return false;
    }

    // ---------------- PARENT/TEACHER ACTIONS ----------------
   public void approveWish(int wishId, int points, int level) {
    for (Wish w : wishes) {
        if (w.getId() == wishId) {
            w.setRequiredPoints(points);
            w.setRequiredLevel(level);
            w.setApproved(true);
            saveWishes();
            return;
        }
    }
}

}

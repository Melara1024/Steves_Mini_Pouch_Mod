package ga.melara.stevesminipouch.data;

public class ClientInventoryData {

    private static int slot;
    private static boolean isActiveInventory;
    private static boolean isActivateArmor;
    private static boolean isActiveOffhand;
    private static boolean isActivateCraft;

    public static void set(int slot, boolean isActiveInventory, boolean isActivateArmor, boolean isActiveOffhand, boolean isActivateCraft) {
        ClientInventoryData.slot = slot;
        ClientInventoryData.isActiveInventory = isActiveInventory;
        ClientInventoryData.isActivateArmor = isActivateArmor;
        ClientInventoryData.isActiveOffhand = isActiveOffhand;
        ClientInventoryData.isActivateCraft = isActivateCraft;
    }

    public static int getSlot() {
        return slot;
    }

    public static boolean isActiveInventory() {
        return isActiveInventory;
    }

    public static boolean isEquippable() {
        return isActivateArmor;
    }

    public static boolean isActiveOffhand() {
        return isActiveOffhand;
    }

    public static boolean isCraftable() {
        return isActivateCraft;
    }
}

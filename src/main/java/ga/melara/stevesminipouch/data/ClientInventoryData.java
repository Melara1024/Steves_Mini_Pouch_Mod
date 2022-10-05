package ga.melara.stevesminipouch.data;

public class ClientInventoryData {

    private static int slot;
    private static boolean isActiveInventory;
    private static boolean isActiveOffhand;
    private static boolean isCraftable;
    private static boolean isEquippable;

    public static void set(int slot, boolean isActiveInventory, boolean isActiveOffhand, boolean isCraftable, boolean isEquippable) {
        ClientInventoryData.slot = slot;
        ClientInventoryData.isActiveInventory = isActiveInventory;
        ClientInventoryData.isActiveOffhand = isActiveOffhand;
        ClientInventoryData.isCraftable = isCraftable;
        ClientInventoryData.isEquippable = isEquippable;
    }

    public static boolean isActiveInventory() {
        return isActiveInventory;
    }

    public static boolean isActiveOffhand() {
        return isActiveOffhand;
    }

    public static boolean isCraftable() {
        return isCraftable;
    }

    public static boolean isEquippable() {
        return isEquippable;
    }

    public static int getSlot() {
        return slot;
    }
}

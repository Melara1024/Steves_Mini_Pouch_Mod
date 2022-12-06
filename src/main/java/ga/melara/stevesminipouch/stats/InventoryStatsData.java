package ga.melara.stevesminipouch.stats;


import ga.melara.stevesminipouch.Config;

//データを保持する本体
public class InventoryStatsData {

    private int inventorySize;
    private int effectSize;
    private boolean isActiveInventory;
    private boolean isActivateArmor;
    private boolean isActiveOffhand;
    private boolean isActivateCraft;

    public InventoryStatsData() {
        inventorySize = Config.DEFAULT_SIZE.get();
        effectSize = 0;
        isActiveInventory = Config.DEFAULT_INVENTORY.get();
        isActivateArmor = Config.DEFAULT_ARMOR.get();
        isActiveOffhand = Config.DEFAULT_OFFHAND.get();
        isActivateCraft = Config.DEFAULT_CRAFT.get();
    }

    public InventoryStatsData(int inventorySize, int effectSize, boolean inv, boolean arm, boolean off, boolean cft) {
        this.inventorySize = inventorySize;
        this.effectSize = effectSize;
        this.isActiveInventory = inv;
        this.isActivateArmor = arm;
        this.isActiveOffhand = off;
        this.isActivateCraft = cft;
    }

    public void copyFrom(InventoryStatsData source) {
        inventorySize = source.inventorySize;
        effectSize = source.effectSize;
        isActiveInventory = source.isActiveInventory;
        isActiveOffhand = source.isActiveOffhand;
        isActivateCraft = source.isActivateCraft;
        isActivateArmor = source.isActivateArmor;
    }

    public int getInventorySize() {
        return inventorySize;
    }

    public int getEffectSize() {
        return effectSize;
    }

    public boolean isActiveInventory() {
        return isActiveInventory;
    }

    public boolean isActiveArmor() {
        return isActivateArmor;
    }

    public boolean isActiveOffhand() {
        return isActiveOffhand;
    }

    public boolean isActiveCraft() {
        return isActivateCraft;
    }

    @Override
    public String toString() {
        return String.format("player inv data -> [main: %d, effect: %d, inv: %b, arm: %b, off: %b, cft: %b]", inventorySize, effectSize, isActiveInventory, isActivateArmor, isActiveOffhand, isActivateCraft);
    }
}

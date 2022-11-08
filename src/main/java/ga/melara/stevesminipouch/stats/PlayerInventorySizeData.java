package ga.melara.stevesminipouch.stats;


//データを保持する本体
public class PlayerInventorySizeData {

    private int slot;

    private int effectSlot;
    private boolean isActiveInventory;
    private boolean isActiveOffhand;
    private boolean isCraftable;
    private boolean isEquippable;

    public PlayerInventorySizeData(){
        slot = 36;
        effectSlot = 0;
        isActiveInventory = true;
        isActiveOffhand = true;
        isCraftable = true;
        isEquippable = true;
    }

    public PlayerInventorySizeData(int slot, boolean inv, boolean off, boolean cft, boolean arm)
    {
        this.slot = slot;
        this.isActiveInventory = inv;
        this.isActiveOffhand = off;
        this.isCraftable = cft;
        this.isEquippable = arm;
    }

    public PlayerInventorySizeData(int slot, int effectSlot, boolean inv, boolean off, boolean cft, boolean arm)
    {
        this.slot = slot;
        this.effectSlot = effectSlot;
        this.isActiveInventory = inv;
        this.isActiveOffhand = off;
        this.isCraftable = cft;
        this.isEquippable = arm;
    }

    public void copyFrom(PlayerInventorySizeData source) {
        slot = source.slot;
        effectSlot = source.effectSlot;
        isActiveInventory = source.isActiveInventory;
        isActiveOffhand = source.isActiveOffhand;
        isCraftable = source.isCraftable;
        isEquippable = source.isEquippable;
    }


    public boolean isActiveInventory() {
        return isActiveInventory;
    }

    public boolean isActiveOffhand() {
        return isActiveOffhand;
    }

    public boolean isCraftable() {
        return isCraftable;
    }

    public boolean isEquippable() {
        return isEquippable;
    }

    public int getSlot() {
        return slot;
    }

    public int getEffectSlot() {return effectSlot;}

    @Override
    public String toString()
    {
        return String.format("player inv data -> [slot: %d, effectslot: %d, inv: %b, armor: %b, offhand: %b, craft: %b]", slot, effectSlot, isActiveInventory, isEquippable, isActiveOffhand, isCraftable);
    }
}

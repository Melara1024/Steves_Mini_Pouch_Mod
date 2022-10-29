package ga.melara.stevesminipouch.data;

import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.mixin.InventoryMixin;
import ga.melara.stevesminipouch.util.IStorageChangable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import org.openjdk.nashorn.internal.objects.annotations.Getter;
import org.openjdk.nashorn.internal.objects.annotations.Setter;



//データを保持する本体
public class PlayerInventorySizeData {

    private int slot;
    private boolean isActiveInventory;
    private boolean isActiveOffhand;
    private boolean isCraftable;
    private boolean isEquippable;

    public PlayerInventorySizeData(){
        slot = 36;
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


    public void setActiveInventory(boolean activeInventory)
    {
        isActiveInventory = activeInventory;
    }

    public void setActiveOffhand(boolean activeOffhand)
    {
        isActiveOffhand = activeOffhand;
    }

    public void setCraftable(boolean craftable)
    {
        isCraftable = craftable;
    }

    public void setEquippable(boolean equippable)
    {
        isEquippable = equippable;
    }

    public void setSlot(int slot)
    {
        this.slot = slot;
    }

    public void copyFrom(PlayerInventorySizeData source) {
        slot = source.slot;
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

}

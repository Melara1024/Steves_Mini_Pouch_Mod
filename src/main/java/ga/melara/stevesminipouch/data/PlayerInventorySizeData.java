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
//NBTとして保存する用
@Mod.EventBusSubscriber
public class PlayerInventorySizeData {

    private int slot;
    private boolean isActiveInventory;
    private boolean isActiveOffhand;
    private boolean isCraftable;
    private boolean isEquippable;


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


    public void saveNBTData(CompoundTag compound) {
        compound.putInt("inventorysize", slot);
        compound.putBoolean("activateinventory", isActiveInventory);
        compound.putBoolean("activateoffhand", isActiveOffhand);
        compound.putBoolean("craftable", isCraftable);
        compound.putBoolean("equippable", isEquippable);

//        System.out.println("saveNBTData");
//        System.out.println(slot);
//        System.out.println(isActiveInventory);
//        System.out.println(isEquippable);
//        System.out.println(isActiveOffhand);
//        System.out.println(isCraftable);
    }

    public void loadNBTData(CompoundTag compound) {
//        if(compound.contains("inventorysize"))slot = compound.getInt("inventorysize");
//        else slot = Config.DEFAULT_SIZE.get();
        if(compound.contains("inventorysize"))slot = compound.getInt("inventorysize");
        else slot = Config.DEFAULT_SIZE.get();
        if(compound.contains("activateinventory"))isActiveInventory = compound.getBoolean("activateinventory");
        else isActiveInventory = Config.DEFAULT_INVENTORY.get();
        if(compound.contains("activateoffhand"))isActiveOffhand = compound.getBoolean("activateoffhand");
        else isActiveOffhand = Config.DEFAULT_OFFHAND.get();
        if(compound.contains("craftable"))isCraftable = compound.getBoolean("craftable");
        else isCraftable = Config.DEFAULT_CRAFT.get();
        if(compound.contains("equippable"))isEquippable = compound.getBoolean("equippable");
        else isEquippable = Config.DEFAULT_ARMOR.get();



//        System.out.println("loadNBTData");
//        System.out.println(slot);
//        System.out.println(isActiveInventory);
//        System.out.println(isEquippable);
//        System.out.println(isActiveOffhand);
//        System.out.println(isCraftable);




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

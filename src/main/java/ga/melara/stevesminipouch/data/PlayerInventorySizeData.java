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

    public void toggleInventory()
    {
        this.isActiveInventory = !this.isActiveInventory;
        if(this.isActiveInventory == false)
        {
            this.isActiveOffhand = false;
            this.isCraftable = false;
            this.isEquippable = false;
            return;
        }
    }

    public void toggleOffhand()
    {
        this.isActiveOffhand = !this.isActiveOffhand;
    }

    public void toggleCraftable()
    {
        this.isCraftable = !this.isCraftable;
    }

    public void toggleEquippable()
    {
        this.isEquippable = !this.isEquippable;
    }

    public void increaseSlot(int i)
    {
        if(this.slot+i < Integer.MAX_VALUE)this.slot = this.slot+i;
    }

    public void decreaseSlot(int d)
    {
        if(this.slot-d > 0)this.slot= this.slot-d;
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
    }

    public void loadNBTData(CompoundTag compound) {
//        if(compound.contains("inventorysize"))slot = compound.getInt("inventorysize");
//        else slot = Config.DEFAULT_SIZE.get();
        slot = 63;
        if(compound.contains("activateinventory"))isActiveInventory = compound.getBoolean("activateinventory");
        else isActiveInventory = Config.DEFAULT_INVENTORY.get();
        if(compound.contains("activateoffhand"))isActiveOffhand = compound.getBoolean("activateoffhand");
        else isActiveOffhand = Config.DEFAULT_OFFHAND.get();
        if(compound.contains("craftable"))isCraftable = compound.getBoolean("craftable");
        else isCraftable = Config.DEFAULT_CRAFT.get();
        if(compound.contains("equippable"))isEquippable = compound.getBoolean("equippable");
        else isEquippable = Config.DEFAULT_ARMOR.get();
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

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent e)
    {
        LazyOptional<PlayerInventorySizeData> l = e.getEntity().getCapability(PlayerInventoryProvider.DATA);
        PlayerInventorySizeData p = l.orElse(new PlayerInventorySizeData());

        ((IStorageChangable)(Object)Inventory.class).changeStorageSize(slot, e.getEntity().level, e.getEntity());
    }
}

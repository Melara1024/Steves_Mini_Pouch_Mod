package ga.melara.stevesminipouch;

import ga.melara.stevesminipouch.items.*;
import ga.melara.stevesminipouch.items.slotitems.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static ga.melara.stevesminipouch.StevesMiniPouch.MODID;

public class ModRegistry
{

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static void registerItems()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(eventBus);
    }


    public static final RegistryObject<Item> INVENTORY_ACTIVATE_ITEM = ITEMS.register("berrinventory", InventoryActivateItem.build());
    public static final RegistryObject<Item> CRAFT_ACTIVATE_ITEM = ITEMS.register("crafruit", CraftActivatItem.build());
    public static final RegistryObject<Item> ARMOR_ACTIVATE_ITEM = ITEMS.register("armorpple", ArmorActivateItem.build());
    public static final RegistryObject<Item> OFFHAND_ACTIVATE_ITEM = ITEMS.register("offhandrian", OffhandActivateItem.build());

    public static final RegistryObject<Item> SLOT_ADD1_ITEM  = Add1SlotItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> SLOT_ADD9_ITEM = Add9SlotItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> SLOT_ADD27_ITEM = Add27SlotItem.buildInTo(ITEMS);

    public static final RegistryObject<Item> SLOT_SHRINK1_ITEM = Sub1SlotItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> SLOT_SHRINK9_ITEM = Sub9SlotItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> SLOT_SHRINK27_ITEM = Sub27SlotItem.buildInTo(ITEMS);

    public static final RegistryObject<Item> DUMMY_ITEM = ITEMS.register("dummy", DummyItem.build());




    //スロットが増える状態異常，エンチャントの導入
    //エンチャント盆の追加，ポーションの追加もあとでやる

}

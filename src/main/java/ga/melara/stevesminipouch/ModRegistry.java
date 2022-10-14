package ga.melara.stevesminipouch;

import ga.melara.stevesminipouch.items.*;
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
    public static final RegistryObject<Item> SLOT_ADD_ITEM = ITEMS.register("edible_chest", SlotItem.build(1));
    public static final RegistryObject<Item> SLOT_SHRINK_ITEM = ITEMS.register("edible_air", SlotItem.build(-1));
    public static final RegistryObject<Item> SLOT_ADD_ITEM_2 = ITEMS.register("edible_largechest", SlotItem.build(9));
    public static final RegistryObject<Item> SLOT_SHRINK_ITEM_2 = ITEMS.register("edible_void", SlotItem.build(-9));
    public static final RegistryObject<Item> SLOT_ADD_ITEM_3 = ITEMS.register("edible_triplechest", SlotItem.build(27));
    public static final RegistryObject<Item> SLOT_SHRINK_ITEM_3 = ITEMS.register("edible_null", SlotItem.build(-27));


    //スロットが増える状態異常，エンチャントの導入
    //エンチャント盆の追加，ポーションの追加もあとでやる

}

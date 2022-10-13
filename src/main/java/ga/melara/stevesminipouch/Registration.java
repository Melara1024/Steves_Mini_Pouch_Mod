package ga.melara.stevesminipouch;

import ga.melara.stevesminipouch.util.InventoryEffect;
import ga.melara.stevesminipouch.util.MobEffectInstanceWithFunction;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static ga.melara.stevesminipouch.StevesMiniPouch.MODID;

public class Registration
{

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static void registerItems()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(eventBus);
    }

    public static final FoodProperties FOOD_PROPERTIES = new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(2)
            .effect(()-> new MobEffectInstanceWithFunction(InventoryEffect.ADD_SLOT, 1), 1)
            .build();
    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(CreativeModeTab.TAB_FOOD).rarity(Rarity.EPIC).stacksTo(16).food(FOOD_PROPERTIES);



    public static final RegistryObject<Item> INVENTORY_ACTIVATE_ITEM = ITEMS.register("berrinventory", () -> new Item(ITEM_PROPERTIES));
    public static final RegistryObject<Item> CRAFT_ACTIVATE_ITEM = ITEMS.register("crafruit", () -> new Item(ITEM_PROPERTIES));
    public static final RegistryObject<Item> ARMOR_ACTIVATE_ITEM = ITEMS.register("armorpple", () -> new Item(ITEM_PROPERTIES));
    public static final RegistryObject<Item> OFFHAND_ACTIVATE_ITEM = ITEMS.register("offhandrian", () -> new Item(ITEM_PROPERTIES));
    public static final RegistryObject<Item> SLOT_ADD_ITEM = ITEMS.register("edible_chest", () -> new Item(ITEM_PROPERTIES));
    public static final RegistryObject<Item> SLOT_SHRINK_ITEM = ITEMS.register("edible_air", () -> new Item(ITEM_PROPERTIES));
    public static final RegistryObject<Item> SLOT_ADD_ITEM_2 = ITEMS.register("edible_largechest", () -> new Item(ITEM_PROPERTIES));
    public static final RegistryObject<Item> SLOT_SHRINK_ITEM_2 = ITEMS.register("edible_void", () -> new Item(ITEM_PROPERTIES));
    public static final RegistryObject<Item> SLOT_ADD_ITEM_3 = ITEMS.register("edible_triplechest", () -> new Item(ITEM_PROPERTIES));
    public static final RegistryObject<Item> SLOT_SHRINK_ITEM_3 = ITEMS.register("edible_null", () -> new Item(ITEM_PROPERTIES));


    //スロットが増える状態異常，エンチャントの導入
    //エンチャント盆の追加，ポーションの追加もあとでやる

}

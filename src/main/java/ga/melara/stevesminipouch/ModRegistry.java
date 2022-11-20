package ga.melara.stevesminipouch;

import ga.melara.stevesminipouch.command.SlotChangeCommand;
import ga.melara.stevesminipouch.effect.SlotEffect;
import ga.melara.stevesminipouch.enchant.SlotEnchant;
import ga.melara.stevesminipouch.items.*;
import ga.melara.stevesminipouch.items.slotitems.*;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static ga.melara.stevesminipouch.StevesMiniPouch.MODID;

@Mod.EventBusSubscriber
public class ModRegistry {
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab("steves_mini_pouch") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.BUNDLE);
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> pItems) {
            for(Item item : Registry.ITEM) {
                pItems.sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getItem().toString(), o2.getItem().toString()));
                item.fillItemCategory(this, pItems);
            }
        }
    };


    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    private static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);

    private static final DeferredRegister<MobEffect> EFFECT = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);

    public static void registerItems() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(eventBus);
        ENCHANTMENTS.register(eventBus);
        EFFECT.register(eventBus);
    }


    public static final RegistryObject<Item> INVENTORY_ACTIVATE_ITEM = InventoryActivateItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> CRAFT_ACTIVATE_ITEM = CraftActivatItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> ARMOR_ACTIVATE_ITEM = ArmorActivateItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> OFFHAND_ACTIVATE_ITEM = OffhandActivateItem.buildInTo(ITEMS);

    public static final RegistryObject<Item> SLOT_ADD1_ITEM = Add1SlotItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> SLOT_ADD9_ITEM = Add9SlotItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> SLOT_ADD27_ITEM = Add27SlotItem.buildInTo(ITEMS);

    public static final RegistryObject<Item> SLOT_SHRINK1_ITEM = Sub1SlotItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> SLOT_SHRINK9_ITEM = Sub9SlotItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> SLOT_SHRINK27_ITEM = Sub27SlotItem.buildInTo(ITEMS);


    public static final RegistryObject<Enchantment> SLOT_ENCHANT = SlotEnchant.buildInTo(ENCHANTMENTS);

    public static final RegistryObject<MobEffect> SLOT_EFFECT = SlotEffect.buildInTo(EFFECT);

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        SlotChangeCommand.register(event.getDispatcher());
    }

}

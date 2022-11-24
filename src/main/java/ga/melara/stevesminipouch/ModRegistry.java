package ga.melara.stevesminipouch;

import ga.melara.stevesminipouch.command.SlotChangeCommand;
import ga.melara.stevesminipouch.effect.SlotEffect;
import ga.melara.stevesminipouch.enchant.SlotEnchant;
import ga.melara.stevesminipouch.items.*;
import ga.melara.stevesminipouch.items.slotitems.*;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.enchantment.Enchantment;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


import static ga.melara.stevesminipouch.StevesMiniPouch.MODID;

@Mod.EventBusSubscriber
public class ModRegistry {

    public static final ItemGroup ITEM_GROUP = new ItemGroup("stevesminipouch") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModRegistry.SLOT_ADD1_ITEM.get());
        }
    };


    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    private static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);

    //private static final DeferredRegister<Effect> EFFECT = DeferredRegister.create(ForgeRegistries.E, MODID);

    public static void registerItems() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(eventBus);
        ENCHANTMENTS.register(eventBus);
        //EFFECT.register(eventBus);
    }

    public static final RegistryObject<Item> SLOT_ADD1_ITEM = Add1SlotItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> SLOT_ADD9_ITEM = Add9SlotItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> SLOT_ADD27_ITEM = Add27SlotItem.buildInTo(ITEMS);

    public static final RegistryObject<Item> SLOT_SUB1_ITEM = Sub1SlotItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> SLOT_SUB9_ITEM = Sub9SlotItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> SLOT_SUB27_ITEM = Sub27SlotItem.buildInTo(ITEMS);

    public static final RegistryObject<Item> INVENTORY_ACTIVATE_ITEM = InventoryActivateItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> CRAFT_ACTIVATE_ITEM = CraftActivatItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> ARMOR_ACTIVATE_ITEM = ArmorActivateItem.buildInTo(ITEMS);
    public static final RegistryObject<Item> OFFHAND_ACTIVATE_ITEM = OffhandActivateItem.buildInTo(ITEMS);


    public static final RegistryObject<Enchantment> SLOT_ENCHANT = SlotEnchant.buildInTo(ENCHANTMENTS);

    //public static final RegistryObject<Effect> SLOT_EFFECT = SlotEffect.buildInTo(EFFECT);

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        SlotChangeCommand.register(event.getDispatcher());
    }

}

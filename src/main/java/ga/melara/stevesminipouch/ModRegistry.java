package ga.melara.stevesminipouch;

import ga.melara.stevesminipouch.command.SlotChangeCommand;
import ga.melara.stevesminipouch.effect.SlotEffect;
import ga.melara.stevesminipouch.enchant.SlotEnchant;
import ga.melara.stevesminipouch.items.*;
import ga.melara.stevesminipouch.items.slotitems.*;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.CreativeModeTabSearchRegistry;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
import net.minecraftforge.server.command.ConfigCommand;
import net.minecraftforge.server.command.ForgeCommand;

import java.util.List;
import java.util.function.Consumer;

import static ga.melara.stevesminipouch.StevesMiniPouch.LOGGER;
import static ga.melara.stevesminipouch.StevesMiniPouch.MODID;
import static net.minecraft.world.item.CreativeModeTab.builder;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRegistry {
//    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab() {
//        @Override
//        public ItemStack getIconItem() {
//            return new ItemStack(SLOT_ADD1_ITEM.get());
//        }
//
//        @Override
//        public void setSearchTreeBuilder(Consumer<List<ItemStack>> p_259669_) {
//            p_259669_.accept((itemList) -> {
//
//                for(Item item : Registry.ITEM) {
//                    itemList.sort((stack1, stack2) -> {
//                        if(stack1.getItem() instanceof FunctionFoodItem f1 && stack2.getItem() instanceof FunctionFoodItem f2) {
//                            return Integer.compare(f1.getRegistryNumber(), f2.getRegistryNumber());
//                        }
//                        return 0;
//                    });
//                    item.fillItemCategory(this, pItems);
//                }
//
//            });
//        }
//    };


    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    private static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);

    private static final DeferredRegister<MobEffect> EFFECT = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);

    public static void registerItems() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(eventBus);
        ENCHANTMENTS.register(eventBus);
        EFFECT.register(eventBus);

        MinecraftForge.EVENT_BUS.register(new ModRegistry());
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

    public static final RegistryObject<MobEffect> SLOT_EFFECT = SlotEffect.buildInTo(EFFECT);

    @SubscribeEvent
    public static void registerTabs(CreativeModeTabEvent.Register e) {
        e.registerCreativeModeTab(new ResourceLocation(MODID), (builder) -> {
            builder.title(Component.translatable("itemGroup.tab_minipouch"))
                    .icon(() -> new ItemStack(SLOT_ADD1_ITEM.get()))
                    .displayItems((featureFlagSet, toAdd, flag) -> {
                        toAdd.accept(SLOT_ADD1_ITEM.get());
                        toAdd.accept(SLOT_ADD9_ITEM.get());
                        toAdd.accept(SLOT_ADD27_ITEM.get());

                        toAdd.accept(SLOT_SUB1_ITEM.get());
                        toAdd.accept(SLOT_SUB9_ITEM.get());
                        toAdd.accept(SLOT_SUB27_ITEM.get());

                        toAdd.accept(INVENTORY_ACTIVATE_ITEM.get());
                        toAdd.accept(ARMOR_ACTIVATE_ITEM.get());
                        toAdd.accept(OFFHAND_ACTIVATE_ITEM.get());
                        toAdd.accept(CRAFT_ACTIVATE_ITEM.get());
                    }).build();
        });
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        SlotChangeCommand.register(event.getDispatcher());
    }
}

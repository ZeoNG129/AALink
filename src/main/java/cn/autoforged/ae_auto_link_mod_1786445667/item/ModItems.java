package cn.autoforged.ae_auto_link_mod_1786445667.item;

import cn.autoforged.ae_auto_link_mod_1786445667.MainMod;
import cn.autoforged.ae_auto_link_mod_1786445667.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, MainMod.MOD_ID);

    public static final RegistryObject<Item> WIRELESS_CONNECTOR_ITEM = ITEMS.register("wireless_connector",
        () -> new BlockItem(ModBlocks.WIRELESS_CONNECTOR.get(), new Item.Properties()));

    public static final RegistryObject<Item> WIRELESS_LINK_TOOL = ITEMS.register("wireless_link_tool",
        () -> new WirelessLinkTool(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> INFINITE_COBBLESTONE_CELL = ITEMS.register("infinite_cobblestone_cell",
        () -> new InfiniteCellItem(InfiniteCellItem.Type.COBBLESTONE, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> INFINITE_WATER_CELL = ITEMS.register("infinite_water_cell",
        () -> new InfiniteCellItem(InfiniteCellItem.Type.WATER, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> INFINITE_LAVA_CELL = ITEMS.register("infinite_lava_cell",
        () -> new InfiniteCellItem(InfiniteCellItem.Type.LAVA, new Item.Properties().stacksTo(1)));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}

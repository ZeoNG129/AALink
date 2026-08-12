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

    public static final RegistryObject<Item> P2P_TUNNEL_BLOCK_ITEM = ITEMS.register("wireless_connector",
        () -> new BlockItem(ModBlocks.P2P_TUNNEL_BLOCK.get(), new Item.Properties()));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}

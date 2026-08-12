package cn.autoforged.ae_auto_link_mod_1786445667.block;

import cn.autoforged.ae_auto_link_mod_1786445667.MainMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(ForgeRegistries.BLOCKS, MainMod.MOD_ID);

    public static final RegistryObject<Block> P2P_TUNNEL_BLOCK = BLOCKS.register("wireless_connector",
        () -> new P2PTunnelBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_CYAN)
            .strength(2.0F, 6.0F)
            .sound(SoundType.METAL)
            .lightLevel(state -> 7)
            .requiresCorrectToolForDrops()));

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}

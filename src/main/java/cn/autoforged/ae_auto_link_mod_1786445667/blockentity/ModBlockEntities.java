package cn.autoforged.ae_auto_link_mod_1786445667.blockentity;

import cn.autoforged.ae_auto_link_mod_1786445667.MainMod;
import cn.autoforged.ae_auto_link_mod_1786445667.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MainMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<P2PTunnelBlockEntity>> P2P_TUNNEL =
        BLOCK_ENTITIES.register("wireless_connector",
            () -> BlockEntityType.Builder.of(
                P2PTunnelBlockEntity::new,
                ModBlocks.P2P_TUNNEL_BLOCK.get()
            ).build(null));

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}

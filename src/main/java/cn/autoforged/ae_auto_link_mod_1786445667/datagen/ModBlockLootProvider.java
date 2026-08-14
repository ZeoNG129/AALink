package cn.autoforged.ae_auto_link_mod_1786445667.datagen;

import cn.autoforged.ae_auto_link_mod_1786445667.block.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockLootProvider extends BlockLootSubProvider {

    protected ModBlockLootProvider() {
        super(java.util.Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        // drop: self —— 破坏后掉落方块自身
        this.dropSelf(ModBlocks.WIRELESS_CONNECTOR.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream()
            .map(RegistryObject::get)
            .collect(java.util.stream.Collectors.toList());
    }
}

package cn.autoforged.ae_auto_link_mod_1786445667.datagen;

import cn.autoforged.ae_auto_link_mod_1786445667.MainMod;
import cn.autoforged.ae_auto_link_mod_1786445667.block.ModBlocks;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockTagProvider extends BlockTagsProvider {

    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup,
                               ExistingFileHelper helper) {
        super(output, lookup, MainMod.MOD_ID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // required_tool: pickaxe —— 需镐挖掘（配合方块的 requiresCorrectToolForDrops）
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.WIRELESS_CONNECTOR.get());
    }
}

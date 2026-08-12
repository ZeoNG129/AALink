package cn.autoforged.ae_auto_link_mod_1786445667.datagen;

import cn.autoforged.ae_auto_link_mod_1786445667.MainMod;
import cn.autoforged.ae_auto_link_mod_1786445667.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, MainMod.MOD_ID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ModBlocks.P2P_TUNNEL_BLOCK.get(), cubeAll(ModBlocks.P2P_TUNNEL_BLOCK.get()));
    }
}

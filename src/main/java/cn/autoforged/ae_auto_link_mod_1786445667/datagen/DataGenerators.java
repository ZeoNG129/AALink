package cn.autoforged.ae_auto_link_mod_1786445667.datagen;

import cn.autoforged.ae_auto_link_mod_1786445667.MainMod;
import java.util.Collections;
import java.util.List;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MainMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        boolean server = event.includeServer();
        boolean client = event.includeClient();

        gen.addProvider(client, new ModBlockStateProvider(gen.getPackOutput(), helper));

        BlockTagsProvider blockTags = new ModBlockTagProvider(
            gen.getPackOutput(), event.getLookupProvider(), helper);
        gen.addProvider(server, blockTags);

        gen.addProvider(server, new LootTableProvider(
            gen.getPackOutput(), Collections.emptySet(),
            List.of(new LootTableProvider.SubProviderEntry(
                ModBlockLootProvider::new, LootContextParamSets.BLOCK))));
    }
}

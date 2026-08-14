package cn.autoforged.ae_auto_link_mod_1786445667.item;

import cn.autoforged.ae_auto_link_mod_1786445667.MainMod;
import cn.autoforged.ae_auto_link_mod_1786445667.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MainMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> AE_AUTO_LINK_TAB = CREATIVE_MODE_TABS.register("main",
        () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModBlocks.WIRELESS_CONNECTOR.get()))
            .title(Component.translatable("creativetab." + MainMod.MOD_ID + ".main"))
            .displayItems((params, output) -> {
                output.accept(ModBlocks.WIRELESS_CONNECTOR.get());
            })
            .build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}

package cn.autoforged.ae_auto_link_mod_1786445667;

import cn.autoforged.ae_auto_link_mod_1786445667.block.ModBlocks;
import cn.autoforged.ae_auto_link_mod_1786445667.blockentity.AutoLinkBridge;
import cn.autoforged.ae_auto_link_mod_1786445667.blockentity.ModBlockEntities;
import cn.autoforged.ae_auto_link_mod_1786445667.item.ModCreativeTabs;
import cn.autoforged.ae_auto_link_mod_1786445667.item.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MainMod.MOD_ID)
public class MainMod {
    public static final String MOD_ID = "aalink";

    public MainMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModCreativeTabs.register(modBus);
        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModBlockEntities.register(modBus);
        // 把 AE 无线桥接中枢注册到 Forge 事件总线：监听方块放置，自动把任意位置的 AE 设备接入中枢网络。
        MinecraftForge.EVENT_BUS.register(AutoLinkBridge.class);
    }
}

package cn.autoforged.ae_auto_link_mod_1786445667.command;

import cn.autoforged.ae_auto_link_mod_1786445667.blockentity.AutoLinkBridge;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * AALink 指令：远程控制自动连接开关。
 * 用法：
 *   /aalink on     开启自动连接
 *   /aalink off    关闭自动连接（已建立的连接保持不断开）
 *   /aalink toggle 切换开关
 *   /aalink status 查看当前开关状态
 */
@Mod.EventBusSubscriber(modid = cn.autoforged.ae_auto_link_mod_1786445667.MainMod.MOD_ID)
public class AALinkCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("aalink")
                .requires(source -> source.hasPermission(2)) // 管理员权限
                .then(Commands.literal("on").executes(ctx -> setEnabled(ctx.getSource(), true)))
                .then(Commands.literal("off").executes(ctx -> setEnabled(ctx.getSource(), false)))
                .then(Commands.literal("toggle").executes(ctx -> {
                    AutoLinkBridge.toggleEnabled();
                    ctx.getSource().sendSuccess(() -> Component.literal("AALink 自动连接已" + (AutoLinkBridge.isEnabled() ? "开启" : "关闭")), true);
                    return 1;
                }))
                .then(Commands.literal("status").executes(ctx -> {
                    ctx.getSource().sendSuccess(() -> Component.literal("AALink 自动连接状态：" + (AutoLinkBridge.isEnabled() ? "开启" : "关闭")), false);
                    return 1;
                }))
        );
    }

    private static int setEnabled(CommandSourceStack source, boolean value) {
        AutoLinkBridge.setEnabled(value);
        source.sendSuccess(() -> Component.literal("AALink 自动连接已" + (value ? "开启" : "关闭")), true);
        return 1;
    }
}

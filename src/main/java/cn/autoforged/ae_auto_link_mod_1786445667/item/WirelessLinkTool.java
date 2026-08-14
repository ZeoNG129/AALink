package cn.autoforged.ae_auto_link_mod_1786445667.item;

import cn.autoforged.ae_auto_link_mod_1786445667.blockentity.AALinkSavedData;
import cn.autoforged.ae_auto_link_mod_1786445667.blockentity.AutoLinkBridge;
import cn.autoforged.ae_auto_link_mod_1786445667.blockentity.WirelessConnectorBlockEntity;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * 无线连接工具：把任意 AE 设备手动接入已绑定无线连接器所在的 ME 网络。
 *
 * <ul>
 *   <li>蹲下右键无线连接器 → 绑定（记录连接器位置与维度到物品 NBT）</li>
 *   <li>蹲下对着空气右键 → 取消绑定</li>
 *   <li>右键任意能连接 AE 网络的方块（线缆/总线/机器等）→ 把该设备接入已绑定连接器所在网络</li>
 * </ul>
 *
 * <p>与自动桥接（{@link AutoLinkBridge}）同一套逻辑：{@link GridHelper#createConnection}
 * 建立不依赖物理位置的逻辑连接，无视距离与维度；连接结果写入 {@link AALinkSavedData}，
 * 重进存档/区块重载后保持。
 */
public class WirelessLinkTool extends Item {

    private static final String TAG_BINDING = "aalink.binding";
    private static final String TAG_DIM = "dim";
    private static final String TAG_X = "x";
    private static final String TAG_Y = "y";
    private static final String TAG_Z = "z";

    public WirelessLinkTool(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        // Forge 交互链最前端：返回非 PASS 时方块 use（打开 GUI）不会执行。
        // 这里消费右键，保证手持工具右键方块时是"连接"而不是打开方块 UI。
        return handleUse(context);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return handleUse(context);
    }

    /**
     * 工具交互核心：
     * <ul>
     *   <li>右键无线连接器：蹲下=绑定，非蹲下=提示用蹲下绑定</li>
     *   <li>蹲下右键其它方块：不消费，保持原语义</li>
     *   <li>非蹲下右键其它方块：把该方块接入已绑定连接器所在网络，并阻止方块 UI 打开</li>
     * </ul>
     */
    private InteractionResult handleUse(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        BlockEntity be = level.getBlockEntity(pos);

        // 右键无线连接器：蹲下=绑定，否则提示
        if (be instanceof WirelessConnectorBlockEntity) {
            if (!level.isClientSide) {
                if (player.isShiftKeyDown()) {
                    bind(stack, level, pos);
                    player.sendSystemMessage(Component.literal(
                            "无线连接工具已绑定: " + level.dimension().location() + " " + pos.toShortString()));
                } else {
                    player.sendSystemMessage(Component.literal("请蹲下右键无线连接器进行绑定"));
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // 蹲下右键其它方块：不消费，交给方块/其它逻辑处理
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        // 非蹲下右键其它方块：尝试接入网络，并消费交互阻止方块 UI 打开
        if (!level.isClientSide) {
            connectDevice((ServerLevel) level, player, stack, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // 蹲下对着空气右键 → 取消绑定
        if (player.isShiftKeyDown() && isBound(stack)) {
            if (!level.isClientSide) {
                clearBinding(stack);
                player.sendSystemMessage(Component.literal("无线连接工具已取消绑定"));
            }
            return level.isClientSide
                    ? InteractionResultHolder.success(stack)
                    : InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    private static void bind(ItemStack stack, Level level, BlockPos pos) {
        CompoundTag binding = new CompoundTag();
        binding.putString(TAG_DIM, level.dimension().location().toString());
        binding.putInt(TAG_X, pos.getX());
        binding.putInt(TAG_Y, pos.getY());
        binding.putInt(TAG_Z, pos.getZ());
        stack.getOrCreateTag().put(TAG_BINDING, binding);
    }

    private static boolean isBound(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains(TAG_BINDING);
    }

    private static void clearBinding(ItemStack stack) {
        if (stack.getTag() != null) {
            stack.getTag().remove(TAG_BINDING);
        }
    }

    private static void connectDevice(ServerLevel level, Player player, ItemStack stack, BlockPos devicePos) {
        if (!isBound(stack)) {
            player.sendSystemMessage(Component.literal("无线连接工具未绑定：请蹲下右键无线连接器进行绑定"));
            return;
        }
        CompoundTag binding = stack.getTag().getCompound(TAG_BINDING);
        ResourceLocation dim = ResourceLocation.tryParse(binding.getString(TAG_DIM));
        BlockPos hubPos = new BlockPos(binding.getInt(TAG_X), binding.getInt(TAG_Y), binding.getInt(TAG_Z));
        ServerLevel hubLevel = dim == null
                ? null
                : level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, dim));
        if (hubLevel == null) {
            player.sendSystemMessage(Component.literal("找不到绑定的无线连接器所在维度"));
            return;
        }
        if (!(hubLevel.getBlockEntity(hubPos) instanceof WirelessConnectorBlockEntity hub)) {
            player.sendSystemMessage(Component.literal("绑定的无线连接器不存在或所在区块未加载"));
            return;
        }
        IGridNode hubNode = hub.getHubNode();
        if (hubNode == null || hubNode.getGrid() == null) {
            player.sendSystemMessage(Component.literal("无线连接器网络节点尚未就绪"));
            return;
        }
        IGridNode deviceNode = AutoLinkBridge.resolveDeviceNode(level, devicePos);
        if (deviceNode == null) {
            player.sendSystemMessage(Component.literal("该方块无法连接 AE 网络"));
            return;
        }
        if (deviceNode == hubNode || deviceNode.getGrid() == hubNode.getGrid()) {
            player.sendSystemMessage(Component.literal("该设备已在 AE 网络中"));
            return;
        }
        try {
            GridHelper.createConnection(deviceNode, hubNode);
        } catch (Exception e) {
            player.sendSystemMessage(Component.literal("连接失败: " + e.getMessage()));
            return;
        }
        AALinkSavedData.get(level).link(devicePos); // 持久化：重进存档后保持连接
        player.sendSystemMessage(Component.literal("设备已接入 AE 网络"));
    }
}

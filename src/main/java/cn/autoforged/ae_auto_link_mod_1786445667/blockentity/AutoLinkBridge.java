package cn.autoforged.ae_auto_link_mod_1786445667.blockentity;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 全局桥接器：监听玩家放置方块事件，把任意位置放置的 AE 设备接入"无线桥接中枢"所在网络。
 *
 * <p>机制：每当一个 AE 设备方块（承载 AE 网格节点的方块，如 ME 线缆、输入总线、机器等）被放置时，
 * 取出该设备的网格节点，与 {@link P2PTunnelBlockEntity} 登记的中枢节点做一次
 * {@link GridHelper#createConnection}。AE2 的 GridConnection 是"逻辑连接"，不依赖节点间的物理位置，
 * 因此无论设备放在多远的距离、甚至其它维度，都能直接并入中枢所在的 ME 网格——满足"无视距离无视维度"。
 *
 * <p>时序：方块被放置时其 BlockEntity 的网格节点可能尚未创建（AE2 节点多在首个 tick 才建），
 * 因此这里先只记录放置位置，下一 tick 由 {@link #bridgePending()} 重试解析节点再建连。
 */
public final class AutoLinkBridge {

    private static final int MAX_ATTEMPTS = 60;

    private static final Set<IGridNode> HUBS = Collections.synchronizedSet(new HashSet<>());
    private static final Set<PlacementRef> PENDING = Collections.synchronizedSet(new HashSet<>());
    /** 已知承载 AE 网格节点的方块实体类型缓存：同一类型方块只需查询一次 getNodeHost */
    private static final Set<net.minecraft.world.level.block.entity.BlockEntityType<?>> KNOWN_NODE_HOST_TYPES =
            Collections.synchronizedSet(new HashSet<>());

    private AutoLinkBridge() {
    }

    static void registerHub(IGridNode node) {
        if (node != null) {
            HUBS.add(node);
        }
    }

    static void unregisterHub(IGridNode node) {
        if (node != null) {
            HUBS.remove(node);
        }
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return; // 只在服务端处理
        }
        // 快速过滤：没有中枢时直接忽略，省去后续一切查询
        if (HUBS.isEmpty()) {
            return;
        }
        BlockPos pos = event.getBlockSnapshot().getPos();
        BlockEntity be = serverLevel.getBlockEntity(pos);
        if (be == null || be instanceof P2PTunnelBlockEntity) {
            return; // 无实体或中枢本身
        }
        // 缓存已知承载 AE 节点的方块实体类型：
        //  - 首次遇到某类型 → 查一次 getNodeHost 判断，承载节点则缓存该类型
        //  - 之后同类型方块放置 → 直接命中缓存，跳过 getNodeHost（对普通方块零额外开销）
        // 功能与原来完全一致（所有 AE 设备都会被识别），只是把重复查询降到每个类型一次。
        if (KNOWN_NODE_HOST_TYPES.contains(be.getType())) {
            PENDING.add(new PlacementRef(serverLevel, pos));
            return;
        }
        if (GridHelper.getNodeHost(serverLevel, pos) != null) {
            KNOWN_NODE_HOST_TYPES.add(be.getType());
            PENDING.add(new PlacementRef(serverLevel, pos));
        }
    }

    /**
     * 服务端 tick 时尝试解析尚未建连的设备节点并桥接。由 Forge 事件总线注册。
     */
    @SubscribeEvent
    public static void onServerTick(net.minecraftforge.event.TickEvent.ServerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) {
            return;
        }
        if (HUBS.isEmpty() || PENDING.isEmpty()) {
            return;
        }
        bridgePending();
    }

    /**
     * 区块加载时扫描其中的 AE 设备并重新桥接。
     * 服务器重启后，已放置的设备不会触发放置事件，但随区块重新加载，
     * 通过这里把设备节点重新接入中枢，保证网络不丢。
     * 不依赖中枢是否已就绪：设备位置先记录，中枢就绪后 bridgePending 会自动建连。
     */
    @SubscribeEvent
    public static void onChunkLoad(net.minecraftforge.event.level.ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!(event.getChunk() instanceof net.minecraft.world.level.chunk.LevelChunk levelChunk)) {
            return;
        }
        for (java.util.Map.Entry<BlockPos, BlockEntity> entry : levelChunk.getBlockEntities().entrySet()) {
            BlockEntity be = entry.getValue();
            if (be == null || be instanceof P2PTunnelBlockEntity) {
                continue;
            }
            PENDING.add(new PlacementRef(serverLevel, entry.getKey()));
        }
    }

    private static void bridgePending() {
        synchronized (PENDING) {
            if (HUBS.isEmpty()) {
                return; // 无中枢时保留所有待处理项，等中枢就绪后再建连
            }
            java.util.Iterator<PlacementRef> it = PENDING.iterator();
            while (it.hasNext()) {
                PlacementRef ref = it.next();
                if (ref.attempts >= MAX_ATTEMPTS) {
                    it.remove(); // 重试次数用尽（设备可能已被拆除），丢弃
                    continue;
                }
                IGridNode deviceNode = resolveDeviceNode(ref.level, ref.pos);
                if (deviceNode == null) {
                    ref.attempts++;
                    continue; // 节点还没建好，下一 tick 再试
                }
                IGridNode hubNode = pickHubNode(ref.level);
                if (hubNode == null) {
                    // 有设备节点但没找到中枢：保留等待（不计数，避免误删）
                    continue;
                }
                it.remove();
                try {
                    // 已在同一网格 / 已建连则跳过，否则逻辑建连并入中枢网络。
                    if (deviceNode == hubNode || deviceNode.getGrid() == hubNode.getGrid()) {
                        continue;
                    }
                    GridHelper.createConnection(deviceNode, hubNode);
                } catch (Exception ignored) {
                    // 重复建连等场景安全忽略
                }
            }
        }
    }

    private static IGridNode resolveDeviceNode(ServerLevel level, BlockPos pos) {
        // 优先处理 AE2 部件（Part）：输出总线/输入总线/存储总线等挂在电缆总线里，
        // 它们不是 IInWorldGridNodeHost 方块，必须通过 IPartHost.getPart(side) 取部件自己的节点。
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof appeng.api.parts.IPartHost partHost) {
            for (Direction side : Direction.values()) {
                appeng.api.parts.IPart part = partHost.getPart(side);
                if (part != null) {
                    IGridNode node = part.getGridNode();
                    if (node != null) {
                        return node;
                    }
                }
            }
            // 电缆总线里没有任何部件但自身也有节点（如纯线缆），回退到宿主节点
        }
        // 完整方块（ME 线缆/机器/存储方块）：走 IInWorldGridNodeHost
        IInWorldGridNodeHost host = GridHelper.getNodeHost(level, pos);
        if (host == null) {
            return null;
        }
        for (Direction side : Direction.values()) {
            IGridNode node = host.getGridNode(side);
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    private static IGridNode pickHubNode(ServerLevel level) {
        synchronized (HUBS) {
            // 优先选择与设备同维度的中枢，否则退回任意一个可用中枢。
            for (IGridNode node : HUBS) {
                if (node.getGrid() != null && node.getLevel() == level) {
                    return node;
                }
            }
            for (IGridNode node : HUBS) {
                if (node.getGrid() != null) {
                    return node;
                }
            }
        }
        return null;
    }

    private static final class PlacementRef {
        private final ServerLevel level;
        private final BlockPos pos;
        private int attempts = 0;

        private PlacementRef(ServerLevel level, BlockPos pos) {
            this.level = level;
            this.pos = pos;
        }
    }
}
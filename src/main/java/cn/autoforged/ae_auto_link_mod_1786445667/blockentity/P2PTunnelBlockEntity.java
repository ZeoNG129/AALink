package cn.autoforged.ae_auto_link_mod_1786445667.blockentity;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.util.AECableType;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 方块实体：AE 无线网格桥接中枢（wireless grid bridge hub）。
 *
 * <p>核心功能：把本方块放进 ME 网络里（放好后其 in-world 网格节点暴露在六个面，与周围线缆/控制器
 * 自动建连），然后玩家在任意地方放置任意能连接 AE 网络的设备（如输入总线），这些设备会被自动接入
 * 本方块所在的那个 ME 网络——无视距离、无视维度。
 *
 * <p>实现思路：本方块承载一个 {@link IManagedGridNode}（in-world，六面暴露）。另有一个
 * {@link AutoLinkBridge} 在 Forge 事件总线上监听玩家放置方块事件：每当一个 AE 设备（承载网格节点的
 * 方块）被放置，就把该设备节点的网格与本方块的网格节点通过 {@link GridHelper#createConnection} 逻辑建连
 * （createConnection 建立的是不依赖物理位置的逻辑连接，天然无视距离与维度），从而使该设备并入本网络。
 *
 * <p>本方块实体只负责：(1) 创建/销毁网格节点并持久化；(2) 把自己登记为桥接中枢，供
 * {@link AutoLinkBridge} 把设备接进来。
 */
public class P2PTunnelBlockEntity extends BlockEntity implements IInWorldGridNodeHost {

    private final IManagedGridNode mainNode;
    private boolean nodeCreated;

    public P2PTunnelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.P2P_TUNNEL.get(), pos, state);
        // 创建 in-world 网格节点：暴露在全部六个面，放置后与相邻 ME 设备（线缆/控制器/机器）自动建连，
        // 从而"放在网络里"即接入该 ME 网格。
        this.mainNode = GridHelper.createManagedNode(this, new NodeListener())
            .setInWorldNode(true)
            .setExposedOnSides(EnumSet.allOf(Direction.class))
            .setIdlePowerUsage(0.0);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, P2PTunnelBlockEntity be) {
        if (level.isClientSide) {
            return;
        }
        be.ensureNodeCreated();
    }

    private void ensureNodeCreated() {
        if (this.nodeCreated || this.level == null || this.level.isClientSide) {
            return;
        }
        this.nodeCreated = true;
        this.mainNode.create(this.level, this.worldPosition);
        // 登记为桥接中枢，供 AutoLinkBridge 将任意放置的 AE 设备接入本网络。
        AutoLinkBridge.registerHub(this.mainNode.getNode());
    }

    public IGridNode getHubNode() {
        return this.mainNode.getNode();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.mainNode.loadFromNBT(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        this.mainNode.saveToNBT(tag);
    }

    @Override
    public void setRemoved() {
        if (this.nodeCreated && this.mainNode.getNode() != null) {
            AutoLinkBridge.unregisterHub(this.mainNode.getNode());
            this.mainNode.destroy();
        }
        super.setRemoved();
    }

    // ---------------- IInWorldGridNodeHost ----------------

    @Override
    public IGridNode getGridNode(Direction direction) {
        return this.mainNode.getNode();
    }

    @Override
    public AECableType getCableConnectionType(Direction direction) {
        return AECableType.SMART;
    }

    /**
     * 网格节点监听器：节点状态变化时标记方块为已变更以便存档。
     */
    private static final class NodeListener implements IGridNodeListener<P2PTunnelBlockEntity> {
        @Override
        public void onSaveChanges(P2PTunnelBlockEntity owner, IGridNode node) {
            owner.setChanged();
        }
    }
}

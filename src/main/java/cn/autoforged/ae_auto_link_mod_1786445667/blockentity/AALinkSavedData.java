package cn.autoforged.ae_auto_link_mod_1786445667.blockentity;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * 持久化自动连接配置：开关状态 + 已桥接设备位置。
 *
 * <p>开关：玩家 /aalink off 后退出存档重进，开关状态保持关闭。
 *
 * <p>已桥接设备：记录"曾经成功桥接过的设备"位置。区块加载重连时只恢复这些设备，
 * 因此开关关闭期间放置的设备（从未桥接）在重进存档后不会被自动连接。
 */
public class AALinkSavedData extends SavedData {
    private static final String NAME = "aalink_config";
    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_LINKED = "linked";

    private boolean enabled = false;
    private final Set<BlockPos> linked = new HashSet<>();

    public static AALinkSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                AALinkSavedData::new, AALinkSavedData::new, NAME);
    }

    public AALinkSavedData() {
    }

    public AALinkSavedData(CompoundTag tag) {
        this.enabled = tag.getBoolean(KEY_ENABLED);
        ListTag list = tag.getList(KEY_LINKED, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            this.linked.add(new BlockPos(entry.getInt("x"), entry.getInt("y"), entry.getInt("z")));
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
        this.setDirty();
    }

    public boolean isLinked(BlockPos pos) {
        return linked.contains(pos);
    }

    public void link(BlockPos pos) {
        if (linked.add(pos)) {
            this.setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean(KEY_ENABLED, enabled);
        ListTag list = new ListTag();
        for (BlockPos pos : linked) {
            CompoundTag entry = new CompoundTag();
            entry.putInt("x", pos.getX());
            entry.putInt("y", pos.getY());
            entry.putInt("z", pos.getZ());
            list.add(entry);
        }
        tag.put(KEY_LINKED, list);
        return tag;
    }
}

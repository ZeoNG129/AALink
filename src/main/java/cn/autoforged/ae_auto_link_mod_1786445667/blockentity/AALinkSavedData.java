package cn.autoforged.ae_auto_link_mod_1786445667.blockentity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * 持久化自动连接开关状态：玩家 /aalink off 后退出存档重进，开关状态保持关闭。
 */
public class AALinkSavedData extends SavedData {
    private static final String NAME = "aalink_config";
    private static final String KEY_ENABLED = "enabled";

    private boolean enabled = false;

    public static AALinkSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                AALinkSavedData::new, AALinkSavedData::new, NAME);
    }

    public AALinkSavedData() {
    }

    public AALinkSavedData(CompoundTag tag) {
        this.enabled = tag.getBoolean(KEY_ENABLED);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
        this.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean(KEY_ENABLED, enabled);
        return tag;
    }
}

package cn.autoforged.ae_auto_link_mod_1786445667.item;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.StorageCell;
import net.minecraft.network.chat.Component;

/**
 * 无限元件的存储实现：储量恒为 {@link #INFINITE_AMOUNT}（2^63 - 1），永不变化。
 *
 * <ul>
 *   <li>插入：一律拒绝（返回 0），储量不会因插入而改变</li>
 *   <li>抽取：只要请求的是本元件的对应物品/流体，就返回请求数量，储量不减少——无限抽取</li>
 *   <li>列表：恒显示 2^63 - 1 个/ mB</li>
 * </ul>
 */
public class InfiniteCellStorage implements StorageCell {

    /** 2^63 - 1：无限元件恒定储量（物品：个；流体：mB） */
    public static final long INFINITE_AMOUNT = Long.MAX_VALUE;

    private final AEKey infiniteKey;

    public InfiniteCellStorage(AEKey infiniteKey) {
        this.infiniteKey = infiniteKey;
    }

    @Override
    public CellState getStatus() {
        return CellState.FULL;
    }

    @Override
    public double getIdleDrain() {
        return 0.0;
    }

    @Override
    public void persist() {
        // 无限元件无状态，无需持久化
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource src) {
        return 0; // 储量不变：拒绝一切插入
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource src) {
        if (amount > 0 && what.equals(infiniteKey)) {
            return amount; // 无限抽取，储量不变
        }
        return 0;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        out.add(infiniteKey, INFINITE_AMOUNT);
    }

    @Override
    public Component getDescription() {
        return infiniteKey.getDisplayName();
    }
}

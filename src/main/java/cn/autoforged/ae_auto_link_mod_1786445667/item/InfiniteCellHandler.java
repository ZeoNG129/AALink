package cn.autoforged.ae_auto_link_mod_1786445667.item;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import net.minecraft.world.item.ItemStack;

/**
 * AE2 存储单元处理器：让 {@link InfiniteCellItem}（无限元件）能被 ME 驱动器/ME 箱子识别，
 * 并返回 {@link InfiniteCellStorage} 作为其存储实现。
 */
public class InfiniteCellHandler implements ICellHandler {

    @Override
    public boolean isCell(ItemStack is) {
        return is.getItem() instanceof InfiniteCellItem;
    }

    @Override
    public StorageCell getCellInventory(ItemStack is, ISaveProvider container) {
        if (is.getItem() instanceof InfiniteCellItem cell) {
            return new InfiniteCellStorage(cell.getInfiniteKey());
        }
        return null;
    }
}

package cn.autoforged.ae_auto_link_mod_1786445667.item;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEFluidKey;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

/**
 * 无限元件：内部恒有 2^63 - 1 个/ mB 对应物品/流体，储量不会变化，放入 ME 驱动器后可无限抽取。
 *
 * <ul>
 *   <li>无限圆石元件 — 物品类，{@link net.minecraft.world.level.block.Blocks#COBBLESTONE}</li>
 *   <li>无限水元件 — 流体类，{@link Fluids#WATER}</li>
 *   <li>无限熔岩元件 — 流体类，{@link Fluids#LAVA}</li>
 * </ul>
 *
 * <p>由 {@link InfiniteCellHandler} 注册到 AE2 的 {@code StorageCells}，返回 {@link InfiniteCellStorage}
 * 作为该元件的存储实现。
 */
public class InfiniteCellItem extends Item {

    public enum Type {
        COBBLESTONE(AEKeyType.items(), AEItemKey.of((ItemLike) net.minecraft.world.level.block.Blocks.COBBLESTONE)),
        WATER(AEKeyType.fluids(), AEFluidKey.of(Fluids.WATER)),
        LAVA(AEKeyType.fluids(), AEFluidKey.of(Fluids.LAVA));

        private final AEKeyType keyType;
        private final AEKey key;

        Type(AEKeyType keyType, AEKey key) {
            this.keyType = keyType;
            this.key = key;
        }

        public AEKeyType keyType() {
            return keyType;
        }

        public AEKey key() {
            return key;
        }
    }

    private final Type type;

    public InfiniteCellItem(Type type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    /** 该元件恒定包含的 AEKey（圆石物品键 / 水、熔岩流体键） */
    public AEKey getInfiniteKey() {
        return type.key();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        boolean fluid = type.keyType() == AEKeyType.fluids();
        tooltip.add(Component.translatable(
                fluid ? "item.aalink.infinite_cell.tooltip.fluid" : "item.aalink.infinite_cell.tooltip.item",
                type.key().getDisplayName()));
    }
}

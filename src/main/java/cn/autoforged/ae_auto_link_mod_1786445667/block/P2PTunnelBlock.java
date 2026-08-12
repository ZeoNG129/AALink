package cn.autoforged.ae_auto_link_mod_1786445667.block;

import cn.autoforged.ae_auto_link_mod_1786445667.blockentity.ModBlockEntities;
import cn.autoforged.ae_auto_link_mod_1786445667.blockentity.P2PTunnelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class P2PTunnelBlock extends Block implements EntityBlock {

    public P2PTunnelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new P2PTunnelBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type == ModBlockEntities.P2P_TUNNEL.get()) {
            return (lvl, pos, st, be) -> P2PTunnelBlockEntity.tick(lvl, pos, st, (P2PTunnelBlockEntity) be);
        }
        return null;
    }
}

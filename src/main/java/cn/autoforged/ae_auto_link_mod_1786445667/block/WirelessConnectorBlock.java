package cn.autoforged.ae_auto_link_mod_1786445667.block;

import cn.autoforged.ae_auto_link_mod_1786445667.blockentity.ModBlockEntities;
import cn.autoforged.ae_auto_link_mod_1786445667.blockentity.WirelessConnectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WirelessConnectorBlock extends Block implements EntityBlock {

    public WirelessConnectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WirelessConnectorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type == ModBlockEntities.WIRELESS_CONNECTOR.get()) {
            return (lvl, pos, st, be) -> WirelessConnectorBlockEntity.tick(lvl, pos, st, (WirelessConnectorBlockEntity) be);
        }
        return null;
    }
}

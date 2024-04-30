package xfacthd.framedblocks.common.block.cube;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.special.FramedChestBlockEntity;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.ChestState;
import xfacthd.framedblocks.common.util.FramedUtils;

import javax.annotation.Nullable;

public class FramedChestBlock extends FramedStorageBlock
{
    public FramedChestBlock()
    {
        super(BlockType.FRAMED_CHEST);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.CHEST_STATE, PropertyHolder.LATCH_TYPE,
                BlockStateProperties.WATERLOGGED
        );
        FramedUtils.removeProperty(builder, FramedProperties.SOLID);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withHorizontalFacing(true)
                .withWater()
                .build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        ItemStack stack = player.getMainHandItem();
        if (stack.is(Utils.FRAMED_HAMMER.value()))
        {
            if (!level.isClientSide())
            {
                state = state.setValue(PropertyHolder.LATCH_TYPE, state.getValue(PropertyHolder.LATCH_TYPE).next());
                level.setBlock(pos, state, Block.UPDATE_ALL);
            }
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = rot.rotate(state.getValue(FramedProperties.FACING_HOR));
        return state.setValue(FramedProperties.FACING_HOR, dir);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        return state.rotate(mirror.getRotation(state.getValue(FramedProperties.FACING_HOR)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedChestBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        if (level.isClientSide() || state.getValue(PropertyHolder.CHEST_STATE) != ChestState.CLOSING)
        {
            return null;
        }
        return Utils.createBlockEntityTicker(type, FBContent.BE_TYPE_FRAMED_CHEST.value(), FramedChestBlockEntity::tick);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(PropertyHolder.LATCH_TYPE, state.getValue(PropertyHolder.LATCH_TYPE));
    }
}

package xfacthd.framedblocks.common.block.slab;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoublePanelBlockEntity;
import xfacthd.framedblocks.common.data.doubleblock.CamoGetter;
import xfacthd.framedblocks.common.data.doubleblock.SolidityCheck;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockTopInteractionMode;

public class FramedDoublePanelBlock extends AbstractFramedDoubleBlock
{
    public FramedDoublePanelBlock()
    {
        super(BlockType.FRAMED_DOUBLE_PANEL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_NE);
    }

    @Override //Used by the blueprint
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction dir = context.getHorizontalDirection();
        if (dir == Direction.SOUTH || dir == Direction.WEST) { dir = dir.getOpposite(); }
        return defaultBlockState().setValue(FramedProperties.FACING_NE, dir);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        return new ItemStack(FBContent.BLOCK_FRAMED_PANEL.value());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        if (rot == Rotation.NONE || rot == Rotation.CLOCKWISE_180)
        {
            return state;
        }
        Direction dir = state.getValue(FramedProperties.FACING_NE);
        dir = dir == Direction.NORTH ? Direction.EAST : Direction.NORTH;
        return state.setValue(FramedProperties.FACING_NE, dir);
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_NE);

        BlockState defState = FBContent.BLOCK_FRAMED_PANEL.value().defaultBlockState();
        return new Tuple<>(
                defState.setValue(FramedProperties.FACING_HOR, facing),
                defState.setValue(FramedProperties.FACING_HOR, facing.getOpposite())
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_NE);
        boolean notFacingAxis = side.getAxis() != facing.getAxis();
        if (side == facing || (notFacingAxis && edge == facing))
        {
            return CamoGetter.FIRST;
        }
        if (side == facing.getOpposite() || (notFacingAxis && edge == facing.getOpposite()))
        {
            return CamoGetter.SECOND;
        }
        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        Direction facing = state.getValue(FramedProperties.FACING_NE);
        if (side == facing)
        {
            return SolidityCheck.FIRST;
        }
        else if (side == facing.getOpposite())
        {
            return SolidityCheck.SECOND;
        }
        return SolidityCheck.BOTH;
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoublePanelBlockEntity(pos, state);
    }
}
package xfacthd.framedblocks.common.block.slab;

import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleSlabBlockEntity;
import xfacthd.framedblocks.common.data.doubleblock.CamoGetter;
import xfacthd.framedblocks.common.data.doubleblock.SolidityCheck;
import xfacthd.framedblocks.common.data.doubleblock.DoubleBlockTopInteractionMode;

public class FramedDoubleSlabBlock extends AbstractFramedDoubleBlock
{
    public FramedDoubleSlabBlock()
    {
        super(BlockType.FRAMED_DOUBLE_SLAB);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        return new ItemStack(FBContent.BLOCK_FRAMED_SLAB.value());
    }

    @Override
    public Tuple<BlockState, BlockState> calculateBlockPair(BlockState state)
    {
        BlockState defState = FBContent.BLOCK_FRAMED_SLAB.value().defaultBlockState();
        return new Tuple<>(
                defState.setValue(FramedProperties.TOP, false),
                defState.setValue(FramedProperties.TOP, true)
        );
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.SECOND;
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        if (side == Direction.UP)
        {
            return CamoGetter.SECOND;
        }
        else if (side == Direction.DOWN)
        {
            return CamoGetter.FIRST;
        }
        else if (edge == Direction.UP)
        {
            return CamoGetter.SECOND;
        }
        else if (edge == Direction.DOWN)
        {
            return CamoGetter.FIRST;
        }
        return CamoGetter.NONE;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        return switch (side)
        {
            case DOWN -> SolidityCheck.FIRST;
            case UP -> SolidityCheck.SECOND;
            default -> SolidityCheck.BOTH;
        };
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoubleSlabBlockEntity(pos, state);
    }
}
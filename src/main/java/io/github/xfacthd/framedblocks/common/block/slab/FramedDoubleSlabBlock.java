package io.github.xfacthd.framedblocks.common.block.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedDoubleBlock;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class FramedDoubleSlabBlock extends FramedDoubleBlock
{
    public FramedDoubleSlabBlock(Properties props)
    {
        super(BlockType.FRAMED_DOUBLE_SLAB, props);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player)
    {
        if (includeData)
        {
            return super.getCloneItemStack(level, pos, state, true, player);
        }
        return new ItemStack(FBContent.BLOCK_FRAMED_SLAB.value());
    }

    @Override
    public DoubleBlockParts calculateParts(BlockState state)
    {
        BlockState defState = FBContent.BLOCK_FRAMED_SLAB.value().defaultBlockState();
        return new DoubleBlockParts(
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
    @Nullable
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    @Nullable
    public Direction getHorizontalOrientation(BlockState state)
    {
        return null;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState();
    }
}

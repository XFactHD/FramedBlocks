package io.github.xfacthd.framedblocks.common.block.slab;

import io.github.xfacthd.framedblocks.api.block.doubleblock.CamoGetter;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockTopInteractionMode;
import io.github.xfacthd.framedblocks.api.block.doubleblock.SolidityCheck;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.slab.FramedAdjustableDoubleBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public class FramedAdjustableDoubleSlabBlock extends FramedAdjustableDoubleBlock
{
    private FramedAdjustableDoubleSlabBlock(
            BlockType type,
            Properties props,
            Function<BlockState, DoubleBlockParts> partsBuilder,
            BlockEntityType.BlockEntitySupplier<FramedAdjustableDoubleBlockEntity> beSupplier
    )
    {
        super(type, props, _ -> Direction.UP, partsBuilder, beSupplier);
    }

    @Override
    public DoubleBlockTopInteractionMode calculateTopInteractionMode(BlockState state)
    {
        return DoubleBlockTopInteractionMode.SECOND;
    }

    @Override
    public SolidityCheck calculateSolidityCheck(BlockState state, Direction side)
    {
        return switch (side)
        {
            case DOWN -> SolidityCheck.FIRST;
            case UP -> SolidityCheck.SECOND;
            case NORTH, SOUTH, WEST, EAST -> SolidityCheck.BOTH;
        };
    }

    @Override
    public CamoGetter calculateCamoGetter(BlockState state, Direction side, @Nullable Direction edge)
    {
        return switch (side)
        {
            case DOWN -> CamoGetter.FIRST;
            case UP -> CamoGetter.SECOND;
            case NORTH, SOUTH, WEST, EAST ->
            {
                if (edge == Direction.DOWN) yield CamoGetter.FIRST;
                if (edge == Direction.UP) yield CamoGetter.SECOND;
                yield CamoGetter.NONE;
            }
        };
    }

    @Override
    @Nullable
    public Direction getHorizontalOrientation(BlockState state)
    {
        return null;
    }

    public static FramedAdjustableDoubleSlabBlock standard(Properties props)
    {
        return new FramedAdjustableDoubleSlabBlock(
                BlockType.FRAMED_ADJ_DOUBLE_SLAB,
                props,
                FramedAdjustableDoubleBlock::makeStandardParts,
                FramedAdjustableDoubleBlockEntity::standard
        );
    }

    public static FramedAdjustableDoubleSlabBlock copycat(Properties props)
    {
        return new FramedAdjustableDoubleSlabBlock(
                BlockType.FRAMED_ADJ_DOUBLE_COPYCAT_SLAB,
                props,
                FramedAdjustableDoubleBlock::makeCopycatParts,
                FramedAdjustableDoubleBlockEntity::copycat
        );
    }
}

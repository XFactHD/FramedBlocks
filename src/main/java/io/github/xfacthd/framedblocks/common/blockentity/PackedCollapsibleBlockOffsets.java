package io.github.xfacthd.framedblocks.common.blockentity;

import io.github.xfacthd.framedblocks.api.block.doubleblock.DoubleBlockParts;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelProperty;
import org.jspecify.annotations.Nullable;

public sealed interface PackedCollapsibleBlockOffsets
{
    ModelProperty<PackedCollapsibleBlockOffsets> PROPERTY = new ModelProperty<>();

    int unwrap(BlockState state);

    static int unwrap(@Nullable Object userData, BlockState partState)
    {
        return userData instanceof PackedCollapsibleBlockOffsets offsets ? offsets.unwrap(partState) : 0;
    }

    record Single(int offsets) implements PackedCollapsibleBlockOffsets
    {
        @Override
        public int unwrap(BlockState state)
        {
            return offsets;
        }
    }

    record Double(DoubleBlockParts parts, int offsetsOne, int offsetsTwo) implements PackedCollapsibleBlockOffsets
    {
        @Override
        public int unwrap(BlockState state)
        {
            return state == parts.stateTwo() ? offsetsTwo : offsetsOne;
        }
    }
}

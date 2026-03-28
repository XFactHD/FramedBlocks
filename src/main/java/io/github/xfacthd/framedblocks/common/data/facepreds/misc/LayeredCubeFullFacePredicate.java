package io.github.xfacthd.framedblocks.common.data.facepreds.misc;

import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class LayeredCubeFullFacePredicate implements FullFacePredicate {
    @Override
    public boolean test(BlockState state, Direction side) {
        int layers = state.getValue(BlockStateProperties.LAYERS);
        return layers == 8 || side == state.getValue(BlockStateProperties.FACING).getOpposite();
    }
}

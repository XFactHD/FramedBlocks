package io.github.xfacthd.framedblocks.common.data.skippreds.misc;

import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.blockentity.special.CollapsibleBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.collapsible.VertexMappings;
import io.github.xfacthd.framedblocks.common.data.collapsible.VertexPair;
import io.github.xfacthd.framedblocks.common.data.property.NullableDirection;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@CullTest(BlockType.FRAMED_COLLAPSIBLE_BLOCK)
public final class CollapsibleBlockSkipPredicate implements SideSkipPredicate {
    @Override
    @CullTest.TestTarget(BlockType.FRAMED_COLLAPSIBLE_BLOCK)
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        NullableDirection face = state.getValue(PropertyHolder.NULLABLE_FACE);
        if (face == NullableDirection.NONE || side.getAxis() == face.toDirection().getAxis()) {
            return false;
        }

        if (adjState.getBlock() == state.getBlock() && adjState.getValue(PropertyHolder.NULLABLE_FACE) == face) {
            BlockEntity be = level.getBlockEntity(pos);
            BlockEntity adjBe = level.getBlockEntity(pos.relative(side));

            if (be instanceof CollapsibleBlockEntity cbe && adjBe instanceof CollapsibleBlockEntity adjCbe) {
                Direction faceDir = face.toDirection();
                VertexPair verts = VertexMappings.getEdgeVertices(faceDir, side);
                VertexPair adjVerts = VertexMappings.getEdgeVertices(faceDir, side.getOpposite());

                int offV1 = cbe.getVertexOffset(state, verts.v1());
                int offV2 = cbe.getVertexOffset(state, verts.v2());
                int adjOffV1 = adjCbe.getVertexOffset(adjState, adjVerts.v1());
                int adjOffV2 = adjCbe.getVertexOffset(adjState, adjVerts.v2());

                return offV1 == adjOffV2 && offV2 == adjOffV1;
            }
        }
        return false;
    }
}

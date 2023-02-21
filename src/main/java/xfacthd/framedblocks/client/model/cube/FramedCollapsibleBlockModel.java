package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.blockentity.FramedCollapsibleBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedCollapsibleBlockModel extends FramedBlockModel
{
    private static final float MIN_DEPTH = .001F;

    private final Direction collapsedFace;
    private final boolean rotSplitEdge;

    public FramedCollapsibleBlockModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.collapsedFace = state.getValue(PropertyHolder.COLLAPSED_FACE).toDirection();
        this.rotSplitEdge = state.getValue(PropertyHolder.ROTATE_SPLIT_EDGE);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, ModelData data)
    {
        Direction quadDir = quad.getDirection();
        if (collapsedFace == null || quadDir == collapsedFace.getOpposite())
        {
            quadMap.get(quadDir).add(quad);
            return;
        }

        Integer offsets = data.get(FramedCollapsibleBlockEntity.OFFSETS);
        float[] vertexPos = new float[] { 1F, 1F, 1F, 1F };
        if (offsets != null && offsets != 0)
        {
            byte[] vertexOffsets = FramedCollapsibleBlockEntity.unpackOffsets(offsets);
            for (int i = 0; i < 4; i++)
            {
                vertexPos[i] = Math.max(1F - ((float) vertexOffsets[i] / 16F), MIN_DEPTH);
            }
        }

        if (quadDir == collapsedFace)
        {
            float diff02 = Math.abs(vertexPos[0] - vertexPos[2]);
            float diff13 = Math.abs(vertexPos[1] - vertexPos[3]);
            boolean rotate = (diff13 > diff02) != rotSplitEdge;

            QuadModifier.geometry(quad)
                    .apply(Modifiers.setPosition(vertexPos, true))
                    .applyIf(Modifiers.rotateVertices(), rotate)
                    .export(quadMap.get(null));
        }
        else
        {
            if (Utils.isY(collapsedFace))
            {
                boolean top = collapsedFace == Direction.UP;
                int idxOne = getYCollapsedIndexOffset(quadDir);
                int idxTwo = Math.floorMod(idxOne + (top ? 1 : -1), 4);
                float posOne = vertexPos[idxOne];
                float posTwo = vertexPos[idxTwo];

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideUpDown(!top, posOne, posTwo))
                        .export(quadMap.get(quadDir));
            }
            else if (Utils.isY(quadDir))
            {
                boolean top = quad.getDirection() == Direction.UP;
                float posOne = vertexPos[top ? 0 : 1];
                float posTwo = vertexPos[top ? 3 : 2];

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutTopBottom(collapsedFace, posOne, posTwo))
                        .export(quadMap.get(quadDir));
            }
            else
            {
                boolean right = collapsedFace == quadDir.getClockWise();
                float posTop = vertexPos[right ? 3 : 0];
                float posBot = vertexPos[right ? 2 : 1];

                QuadModifier.geometry(quad)
                        .apply(Modifiers.cutSideLeftRight(collapsedFace, posTop, posBot))
                        .export(quadMap.get(quadDir));
            }
        }
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad) { }

    @Override
    protected boolean useBaseModel() { return true; }

    @Override
    protected QuadCacheKey makeCacheKey(BlockState state, Object ctCtx, ModelData data)
    {
        Integer packedOffsets = data.get(FramedCollapsibleBlockEntity.OFFSETS);
        return new CollapsibleBlockQuadCacheKey(state, ctCtx, packedOffsets);
    }

    private int getYCollapsedIndexOffset(Direction quadFace)
    {
        boolean top = collapsedFace == Direction.UP;
        return switch (quadFace)
        {
            case NORTH -> top ? 3 : 2;
            case EAST -> top ? 2 : 3;
            case SOUTH -> top ? 1 : 0;
            case WEST -> top ? 0 : 1;
            case DOWN, UP -> throw new IllegalArgumentException("Invalid facing for y face collapse!");
        };
    }

    private record CollapsibleBlockQuadCacheKey(BlockState state, Object ctCtx, Integer packedOffsets) implements QuadCacheKey { }
}

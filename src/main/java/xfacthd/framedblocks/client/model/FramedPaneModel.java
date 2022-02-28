package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedPaneModel extends FramedBlockModel
{
    protected final boolean north;
    protected final boolean east;
    protected final boolean south;
    protected final boolean west;

    public FramedPaneModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);

        north = state.getValue(BlockStateProperties.NORTH);
        east = state.getValue(BlockStateProperties.EAST);
        south = state.getValue(BlockStateProperties.SOUTH);
        west = state.getValue(BlockStateProperties.WEST);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction face = quad.getDirection();
        if (face.getAxis() == Direction.Axis.Y)
        {
            createTopBottomCenterQuad(quadMap, quad, false);

            if (north) { createTopBottomEdgeQuad(quadMap, quad, Direction.NORTH, false); }
            if (east) { createTopBottomEdgeQuad(quadMap, quad, Direction.EAST, false); }
            if (south) { createTopBottomEdgeQuad(quadMap, quad, Direction.SOUTH, false); }
            if (west) { createTopBottomEdgeQuad(quadMap, quad, Direction.WEST, false); }
        }
        else
        {
            createSideEdgeQuad(quadMap, quad, isSideInset(face), false);

            if (face.getAxis() == Direction.Axis.X)
            {
                if (north) { createSideQuad(quadMap.get(null), quad, false); }
                if (south) { createSideQuad(quadMap.get(null), quad, true); }
            }

            if (face.getAxis() == Direction.Axis.Z)
            {
                if (east) { createSideQuad(quadMap.get(null), quad, true); }
                if (west) { createSideQuad(quadMap.get(null), quad, false); }
            }
        }
    }

    protected void createTopBottomCenterQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, boolean mirrored)
    {
        BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
        if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 7F/16F, 7F/16F, 9F/16F, 9F/16F))
        {
            if (mirrored)
            {
                BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, .001F);
                quadMap.get(null).add(topBotQuad);
            }
            else
            {
                quadMap.get(quad.getDirection()).add(topBotQuad);
            }
        }
    }

    protected void createTopBottomEdgeQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, Direction dir, boolean mirrored)
    {
        if (dir.getAxis() == Direction.Axis.Y) { throw new IllegalArgumentException(String.format("Invalid direction: %s!", dir)); }

        boolean positive = dir.getAxisDirection() == Direction.AxisDirection.POSITIVE;

        float minX;
        float minZ;
        float maxX;
        float maxZ;

        if (dir.getAxis() == Direction.Axis.X)
        {
            minX = positive ? 9F/16F : 0;
            maxX = positive ? 1 : 7F/16F;
            minZ = 7F/16F;
            maxZ = 9F/16F;
        }
        else
        {
            minX = 7F/16F;
            maxX = 9F/16F;
            minZ = positive ? 9F/16F : 0;
            maxZ = positive ? 1 : 7F/16F;
        }

        BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
        if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, minX, minZ, maxX, maxZ))
        {
            if (mirrored)
            {
                BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, .001F);
                quadMap.get(null).add(topBotQuad);
            }
            else
            {
                quadMap.get(quad.getDirection()).add(topBotQuad);
            }
        }
    }

    protected void createSideEdgeQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, boolean inset, boolean mirrored)
    {
        if (inset && mirrored) { throw new IllegalArgumentException("Quad can't be mirrored and inset!"); }

        BakedQuad edgeQuad = ModelUtils.duplicateQuad(quad);
        if (BakedQuadTransformer.createSideQuad(edgeQuad, 7F/16F, 0, 9F/16F, 1))
        {
            if (inset)
            {
                BakedQuadTransformer.setQuadPosInFacingDir(edgeQuad, 9F/16F);
                quadMap.get(null).add(edgeQuad);
            }
            else
            {
                if (mirrored)
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(edgeQuad, .001F);
                    quadMap.get(quad.getDirection().getOpposite()).add(edgeQuad);
                }
                else
                {
                    quadMap.get(quad.getDirection()).add(edgeQuad);
                }
            }
        }
    }

    private void createSideQuad(List<BakedQuad> quadList, BakedQuad quad, boolean positive)
    {
        float minXZ = positive ? 9F/16F : 0;
        float maxXZ = positive ? 1 : 7F/16F;

        BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
        if (BakedQuadTransformer.createSideQuad(sideQuad, minXZ, 0, maxXZ, 1))
        {
            BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 9F/16F);
            quadList.add(sideQuad);
        }
    }

    protected boolean isSideInset(Direction face)
    {
        if (face == Direction.NORTH) { return !north; }
        if (face == Direction.EAST) { return !east; }
        if (face == Direction.SOUTH) { return !south; }
        if (face == Direction.WEST) { return !west; }
        throw new IllegalArgumentException(String.format("Invalid face: %s!", face));
    }
}
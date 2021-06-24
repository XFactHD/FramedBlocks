package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;

import fuzs.diagonalfences.api.IDiagonalBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedDiagonalFenceModel extends FramedFenceModel
{
    private final boolean northEast;
    private final boolean southEast;
    private final boolean northWest;
    private final boolean southWest;

    public FramedDiagonalFenceModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);

        boolean hasProps = state.getBlock() instanceof IDiagonalBlock && ((IDiagonalBlock)state.getBlock()).hasProperties();
        northEast = hasProps && state.get(IDiagonalBlock.NORTH_EAST);
        southEast = hasProps && state.get(IDiagonalBlock.SOUTH_EAST);
        northWest = hasProps && state.get(IDiagonalBlock.NORTH_WEST);
        southWest = hasProps && state.get(IDiagonalBlock.SOUTH_WEST);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        super.transformQuad(quadMap, quad);

        createDiagonalFenceBars(quadMap, quad, Direction.NORTH, northEast);
        createDiagonalFenceBars(quadMap, quad, Direction.EAST, southEast);
        createDiagonalFenceBars(quadMap, quad, Direction.SOUTH, southWest);
        createDiagonalFenceBars(quadMap, quad, Direction.WEST, northWest);
    }

    private void createDiagonalFenceBars(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, Direction dir, boolean active)
    {
        if (active)
        {
            if (quad.getFace().getAxis() == Direction.Axis.Y)
            {
                BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), 7F/16F) &&
                        BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.rotateY(), 9F/16F) &&
                        BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.rotateYCCW(), 9F/16F)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, quad.getFace() == Direction.UP ? 15F/16F : 4F/16F);
                    rotateQuad(topBotQuad, dir);
                    quadMap.get(null).add(topBotQuad);

                    topBotQuad = ModelUtils.duplicateQuad(topBotQuad);
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, quad.getFace() == Direction.UP ? 9F/16F : 10F/16F);
                    quadMap.get(null).add(topBotQuad);
                }
            }
            else if (quad.getFace() == dir.rotateY() || quad.getFace() == dir.rotateYCCW())
            {
                boolean neg = dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE;
                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(sideQuad, neg ? 0F : 9F/16F, 6F/16F, neg ? 7F/16F : 1F, 9F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 9F/16F);
                    rotateQuad(sideQuad, dir);
                    quadMap.get(null).add(sideQuad);
                }

                sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(sideQuad, neg ? 0F : 9F/16F, 12F/16F, neg ? 7F/16F : 1F, 15F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 9F/16F);
                    rotateQuad(sideQuad, dir);
                    quadMap.get(null).add(sideQuad);
                }
            }
            else if (quad.getFace() == dir)
            {
                BakedQuad frontQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(frontQuad, 7F/16F, 6F/16F, 9F/16F, 9F/16F))
                {
                    rotateQuad(frontQuad, dir);
                    quadMap.get(null).add(frontQuad);
                }

                frontQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(frontQuad, 7F/16F, 12F/16F, 9F/16F, 15F/16F))
                {
                    rotateQuad(frontQuad, dir);
                    quadMap.get(null).add(frontQuad);
                }
            }
        }
    }

    private void rotateQuad(BakedQuad quad, Direction dir)
    {
        BakedQuadTransformer.rotateQuadAroundAxisCentered(quad, Direction.Axis.Y, -45F, true, new Vector3f(dir.getXOffset(), 1, dir.getZOffset()));
    }
}
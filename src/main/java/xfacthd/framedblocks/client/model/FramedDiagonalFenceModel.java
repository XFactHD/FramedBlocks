package xfacthd.framedblocks.client.model;

import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.BakedQuadTransformer;
import xfacthd.framedblocks.api.util.client.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedDiagonalFenceModel extends FramedFenceModel
{
    private final boolean northEast;
    private final boolean southEast;
    private final boolean northWest;
    private final boolean southWest;

    public FramedDiagonalFenceModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);

        //TODO: reactivate when DiagonalFences is ported
        //boolean hasProps = state.getBlock() instanceof IDiagonalBlock && ((IDiagonalBlock)state.getBlock()).hasProperties();
        northEast = false;//hasProps && state.getValue(IDiagonalBlock.NORTH_EAST);
        southEast = false;//hasProps && state.getValue(IDiagonalBlock.SOUTH_EAST);
        northWest = false;//hasProps && state.getValue(IDiagonalBlock.NORTH_WEST);
        southWest = false;//hasProps && state.getValue(IDiagonalBlock.SOUTH_WEST);
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

    private static void createDiagonalFenceBars(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, Direction dir, boolean active)
    {
        if (active)
        {
            if (Utils.isY(quad.getDirection()))
            {
                BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), 7F/16F) &&
                        BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getClockWise(), 9F/16F) &&
                        BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getCounterClockWise(), 9F/16F)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, quad.getDirection() == Direction.UP ? 15F/16F : 4F/16F);
                    rotateQuad(topBotQuad, dir);
                    quadMap.get(null).add(topBotQuad);

                    topBotQuad = ModelUtils.duplicateQuad(topBotQuad);
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, quad.getDirection() == Direction.UP ? 9F/16F : 10F/16F);
                    quadMap.get(null).add(topBotQuad);
                }
            }
            else if (quad.getDirection() == dir.getClockWise() || quad.getDirection() == dir.getCounterClockWise())
            {
                boolean neg = !Utils.isPositive(dir);
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
            else if (quad.getDirection() == dir)
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

    private static void rotateQuad(BakedQuad quad, Direction dir)
    {
        BakedQuadTransformer.rotateQuadAroundAxisCentered(quad, Direction.Axis.Y, -45F, true, new Vector3f(dir.getStepX(), 1, dir.getStepZ()));
    }
}
package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.BakedQuadTransformer;
import xfacthd.framedblocks.api.util.client.ModelUtils;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedLatticeModel extends FramedBlockModel
{
    private final boolean xAxis;
    private final boolean yAxis;
    private final boolean zAxis;

    public FramedLatticeModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        xAxis = state.getValue(PropertyHolder.X_AXIS);
        yAxis = state.getValue(PropertyHolder.Y_AXIS);
        zAxis = state.getValue(PropertyHolder.Z_AXIS);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getDirection() == Direction.UP || quad.getDirection() == Direction.DOWN)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 6F/16F, 6F/16F, 10F/16F, 10F/16F))
            {
                if (yAxis)
                {
                    quadMap.get(quad.getDirection()).add(topBotQuad);
                }
                else
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, 10F/16F);
                    quadMap.get(null).add(topBotQuad);
                }
            }

            if (xAxis)
            {
                topBotQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 0, 6F/16F, 6F/16F, 10F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, 10F/16F);
                    quadMap.get(null).add(topBotQuad);
                }

                topBotQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 10F/16F, 6F/16F, 1, 10F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, 10F/16F);
                    quadMap.get(null).add(topBotQuad);
                }
            }

            if (zAxis)
            {
                topBotQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 6F/16F, 0, 10F/16F, 6F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, 10F/16F);
                    quadMap.get(null).add(topBotQuad);
                }

                topBotQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 6F/16F, 10F/16F, 10F/16F, 1))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, 10F/16F);
                    quadMap.get(null).add(topBotQuad);
                }
            }
        }
        else if (Utils.isX(quad.getDirection()))
        {
            createHorizontalStrutSideQuads(quadMap, quad, xAxis, zAxis);
        }
        else if (Utils.isZ(quad.getDirection()))
        {
            createHorizontalStrutSideQuads(quadMap, quad, zAxis, xAxis);
        }

        if (!Utils.isY(quad.getDirection()) && yAxis)
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 6F/16F, 0, 10F/16F, 6F/16F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 10F/16F);
                quadMap.get(null).add(sideQuad);
            }

            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 6F/16F, 10F/16F, 10F/16F, 1))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 10F/16F);
                quadMap.get(null).add(sideQuad);
            }
        }
    }

    private static void createHorizontalStrutSideQuads(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, boolean frontAxis, boolean sideAxis)
    {
        BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
        if (BakedQuadTransformer.createSideQuad(sideQuad, 6F/16F, 6F/16F, 10F/16F, 10F/16F))
        {
            if (frontAxis)
            {
                quadMap.get(quad.getDirection()).add(sideQuad);
            }
            else
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 10F/16F);
                quadMap.get(null).add(sideQuad);
            }
        }

        if (sideAxis)
        {
            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 0, 6F/16F, 6F/16F, 10F/16F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 10F/16F);
                quadMap.get(null).add(sideQuad);
            }

            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 10F/16F, 6F/16F, 1, 10F/16F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 10F/16F);
                quadMap.get(null).add(sideQuad);
            }
        }
    }
}
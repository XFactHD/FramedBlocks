package xfacthd.framedblocks.client.model.v2;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.List;
import java.util.Map;

public class FramedLatticeModel extends FramedBlockModelV2
{
    private final boolean xAxis;
    private final boolean yAxis;
    private final boolean zAxis;

    public FramedLatticeModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        xAxis = state.get(PropertyHolder.X_AXIS);
        yAxis = state.get(PropertyHolder.Y_AXIS);
        zAxis = state.get(PropertyHolder.Z_AXIS);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getFace() == Direction.UP || quad.getFace() == Direction.DOWN)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 6F/16F, 6F/16F, 10F/16F, 10F/16F))
            {
                if (yAxis)
                {
                    quadMap.get(quad.getFace()).add(topBotQuad);
                }
                else
                {
                    BakedQuadTransformer.offsetQuadInDir(topBotQuad, quad.getFace().getOpposite(), 6F/16F);
                    quadMap.get(null).add(topBotQuad);
                }
            }

            if (xAxis)
            {
                topBotQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 0, 6F/16F, 6F/16F, 10F/16F))
                {
                    BakedQuadTransformer.offsetQuadInDir(topBotQuad, quad.getFace().getOpposite(), 6F/16F);
                    quadMap.get(null).add(topBotQuad);
                }

                topBotQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 10F/16F, 6F/16F, 1, 10F/16F))
                {
                    BakedQuadTransformer.offsetQuadInDir(topBotQuad, quad.getFace().getOpposite(), 6F/16F);
                    quadMap.get(null).add(topBotQuad);
                }
            }

            if (zAxis)
            {
                topBotQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 6F/16F, 0, 10F/16F, 6F/16F))
                {
                    BakedQuadTransformer.offsetQuadInDir(topBotQuad, quad.getFace().getOpposite(), 6F/16F);
                    quadMap.get(null).add(topBotQuad);
                }

                topBotQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 6F/16F, 10F/16F, 10F/16F, 1))
                {
                    BakedQuadTransformer.offsetQuadInDir(topBotQuad, quad.getFace().getOpposite(), 6F/16F);
                    quadMap.get(null).add(topBotQuad);
                }
            }
        }
        else if (quad.getFace().getAxis() == Direction.Axis.X)
        {
            createHorizontalStrutSideQuads(quadMap, quad, xAxis, zAxis);
        }
        else if (quad.getFace().getAxis() == Direction.Axis.Z)
        {
            createHorizontalStrutSideQuads(quadMap, quad, zAxis, xAxis);
        }

        if (quad.getFace().getAxis() != Direction.Axis.Y && yAxis)
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 6F/16F, 0, 10F/16F, 6F/16F))
            {
                BakedQuadTransformer.offsetQuadInDir(sideQuad, quad.getFace().getOpposite(), 6F/16F);
                quadMap.get(null).add(sideQuad);
            }

            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 6F/16F, 10F/16F, 10F/16F, 1))
            {
                BakedQuadTransformer.offsetQuadInDir(sideQuad, quad.getFace().getOpposite(), 6F/16F);
                quadMap.get(null).add(sideQuad);
            }
        }
    }

    private void createHorizontalStrutSideQuads(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, boolean frontAxis, boolean sideAxis)
    {
        BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
        if (BakedQuadTransformer.createSideQuad(sideQuad, 6F/16F, 6F/16F, 10F/16F, 10F/16F))
        {
            if (frontAxis)
            {
                quadMap.get(quad.getFace()).add(sideQuad);
            }
            else
            {
                BakedQuadTransformer.offsetQuadInDir(sideQuad, quad.getFace().getOpposite(), 6F/16F);
                quadMap.get(null).add(sideQuad);
            }
        }

        if (sideAxis)
        {
            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 0, 6F/16F, 6F/16F, 10F/16F))
            {
                BakedQuadTransformer.offsetQuadInDir(sideQuad, quad.getFace().getOpposite(), 6F/16F);
                quadMap.get(null).add(sideQuad);
            }

            sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 10F/16F, 6F/16F, 1, 10F/16F))
            {
                BakedQuadTransformer.offsetQuadInDir(sideQuad, quad.getFace().getOpposite(), 6F/16F);
                quadMap.get(null).add(sideQuad);
            }
        }
    }
}
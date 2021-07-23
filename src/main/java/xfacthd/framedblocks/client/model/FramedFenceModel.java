package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraftforge.fml.ModList;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedFenceModel extends FramedBlockModel
{
    private final boolean north;
    private final boolean east;
    private final boolean south;
    private final boolean west;

    public FramedFenceModel(BlockState state, BakedModel baseModel)
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
        if (quad.getDirection().getAxis() == Direction.Axis.Y)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 6F/16F, 6F/16F, 10F/16F, 10F/16F))
            {
                quadMap.get(quad.getDirection()).add(topBotQuad);
            }
        }
        else
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createVerticalSideQuad(sideQuad, quad.getDirection().getClockWise(), 10F/16F) &&
                BakedQuadTransformer.createVerticalSideQuad(sideQuad, quad.getDirection().getCounterClockWise(), 10F/16F)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 10F/16F);
                quadMap.get(null).add(sideQuad);
            }
        }

        createFenceBars(quadMap, quad, Direction.NORTH, north);
        createFenceBars(quadMap, quad, Direction.EAST, east);
        createFenceBars(quadMap, quad, Direction.SOUTH, south);
        createFenceBars(quadMap, quad, Direction.WEST, west);
    }

    private void createFenceBars(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, Direction dir, boolean active)
    {
        if (active)
        {
            if (quad.getDirection().getAxis() == Direction.Axis.Y)
            {
                BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getOpposite(), 6F/16F) &&
                    BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getClockWise(), 9F/16F) &&
                    BakedQuadTransformer.createTopBottomQuad(topBotQuad, dir.getCounterClockWise(), 9F/16F)
                )
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, quad.getDirection() == Direction.UP ? 15F/16F : 4F/16F);
                    quadMap.get(null).add(topBotQuad);

                    topBotQuad = ModelUtils.duplicateQuad(topBotQuad);
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, quad.getDirection() == Direction.UP ? 9F/16F : 10F/16F);
                    quadMap.get(null).add(topBotQuad);
                }
            }
            else if (quad.getDirection() == dir.getClockWise() || quad.getDirection() == dir.getCounterClockWise())
            {
                boolean neg = dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE;
                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(sideQuad, neg ? 0F : 10F/16F, 6F/16F, neg ? 6F/16F : 1F, 9F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 9F/16F);
                    quadMap.get(null).add(sideQuad);
                }

                sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(sideQuad, neg ? 0F : 10F/16F, 12F/16F, neg ? 6F/16F : 1F, 15F/16F))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 9F/16F);
                    quadMap.get(null).add(sideQuad);
                }
            }
            else if (quad.getDirection() == dir)
            {
                BakedQuad frontQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(frontQuad, 7F/16F, 6F/16F, 9F/16F, 9F/16F))
                {
                    quadMap.get(quad.getDirection()).add(frontQuad);
                }

                frontQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(frontQuad, 7F/16F, 12F/16F, 9F/16F, 15F/16F))
                {
                    quadMap.get(quad.getDirection()).add(frontQuad);
                }
            }
        }
    }



    public static BakedModel createFenceModel(BlockState state, BakedModel baseModel)
    {
        if (ModList.get().isLoaded("diagonalfences"))
        {
            return new FramedDiagonalFenceModel(state, baseModel);
        }
        return new FramedFenceModel(state, baseModel);
    }
}
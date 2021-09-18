package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;

import java.util.*;

public class FramedLeverModel extends FramedBlockModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(FramedBlocks.MODID, "block/framed_block");

    private final Direction dir;
    private final AttachFace face;

    public FramedLeverModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.get(BlockStateProperties.HORIZONTAL_FACING);
        face = state.get(BlockStateProperties.FACE);
    }

    @Override
    protected boolean hasAdditionalQuadsInLayer(RenderType layer)
    {
        return RenderTypeLookup.canRenderInLayer(Blocks.LEVER.getDefaultState(), layer);
    }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, Random rand, IModelData data, RenderType layer)
    {
        List<BakedQuad> quads = baseModel.getQuads(state, null, rand, data);
        for (BakedQuad quad : quads)
        {
            if (!quad.getSprite().getName().equals(TEXTURE))
            {
                quadMap.get(null).add(quad);
            }
        }
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction facing = dir;
        if (face == AttachFace.CEILING) { facing = Direction.DOWN; }
        else if (face == AttachFace.FLOOR) { facing = Direction.UP; }

        if (facing.getAxis() == Direction.Axis.Y)
        {
            boolean rotX = dir.getAxis() == Direction.Axis.X;
            float minX = rotX ? 4F/16F : 5F/16F;
            float minZ = rotX ? 5F/16F : 4F/16F;
            float maxX = rotX ? 12F/16F : 11F/16F;
            float maxZ = rotX ? 11F/16F : 12F/16F;

            if (quad.getFace() == facing || quad.getFace() == facing.getOpposite())
            {
                BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, minX, minZ, maxX, maxZ))
                {
                    if (quad.getFace() == facing)
                    {
                        BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, 3F/16F);
                        quadMap.get(null).add(topBotQuad);
                    }
                    else
                    {
                        quadMap.get(quad.getFace()).add(topBotQuad);
                    }
                }
            }
            else
            {
                boolean smallSide = rotX == (quad.getFace().getAxis() == Direction.Axis.X);
                float minXZ = smallSide ? 5F/16F : 4F/16F;
                float maxXZ = smallSide ? 11F/16F : 12F/16F;
                float minY = (facing == Direction.DOWN) ? 13F/16F : 0F;
                float maxY = (facing == Direction.DOWN) ? 1F : 3F/16F;

                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(sideQuad, minXZ, minY, maxXZ, maxY))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, smallSide ? 12F/16F : 11F/16F);
                    quadMap.get(null).add(sideQuad);
                }
            }
        }
        else
        {
            if (quad.getFace() == facing || quad.getFace() == facing.getOpposite())
            {
                BakedQuad faceQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(faceQuad, 5F/16F, 4F/16F, 11F/16F, 12F/16F))
                {
                    if (quad.getFace() == facing)
                    {
                        BakedQuadTransformer.setQuadPosInFacingDir(faceQuad, 3F/16F);
                        quadMap.get(null).add(faceQuad);
                    }
                    else
                    {
                        quadMap.get(quad.getFace()).add(faceQuad);
                    }
                }
            }
            else
            {
                boolean xAxis = facing.getAxis() == Direction.Axis.X;
                boolean negative = facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE;

                float minX = xAxis ? (negative ? 13F/16F : 0F) :  5F/16F;
                float maxX = xAxis ? (negative ? 1F :  3F/16F) : 11F/16F;
                float minZ = xAxis ?  5F/16F : (negative ? 13F/16F : 0F);
                float maxZ = xAxis ? 11F/16F : (negative ? 1F :  3F/16F);

                if (quad.getFace().getAxis() == Direction.Axis.Y)
                {
                    BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
                    if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, minX, minZ, maxX, maxZ))
                    {
                        BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, 12F / 16F);
                        quadMap.get(null).add(topBotQuad);
                    }
                }
                else
                {
                    float minXZ = xAxis ? minX : minZ;
                    float maxXZ = xAxis ? maxX : maxZ;

                    BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                    if (BakedQuadTransformer.createSideQuad(sideQuad, minXZ, 4F/16F, maxXZ, 12F/16F))
                    {
                        BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 11F/16F);
                        quadMap.get(null).add(sideQuad);
                    }
                }
            }
        }
    }
}
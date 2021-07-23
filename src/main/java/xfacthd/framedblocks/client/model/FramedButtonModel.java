package xfacthd.framedblocks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;

import java.util.List;
import java.util.Map;

public class FramedButtonModel extends FramedBlockModel
{
    private final Direction dir;
    private final AttachFace face;
    private final boolean pressed;

    public FramedButtonModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        face = state.getValue(BlockStateProperties.ATTACH_FACE);
        pressed = state.getValue(BlockStateProperties.POWERED);
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
            float minX = rotX ? 6F/16F : 5F/16F;
            float minZ = rotX ? 5F/16F : 6F/16F;
            float maxX = rotX ? 10F/16F : 11F/16F;
            float maxZ = rotX ? 11F/16F : 10F/16F;

            if (quad.getDirection() == facing || quad.getDirection() == facing.getOpposite())
            {
                BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, minX, minZ, maxX, maxZ))
                {
                    if (quad.getDirection() == facing)
                    {
                        BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, pressed ? 1F / 16F : 2F / 16F);
                        quadMap.get(null).add(topBotQuad);
                    }
                    else
                    {
                        quadMap.get(quad.getDirection()).add(topBotQuad);
                    }
                }
            }
            else
            {
                boolean largeSide = rotX == (quad.getDirection().getAxis() == Direction.Axis.X);
                float minXZ = largeSide ? 5F/16F : 6F/16F;
                float maxXZ = largeSide ? 11F/16F : 10F/16F;
                float minY = (facing == Direction.DOWN) ? (pressed ? 15F/16F : 14F/16F) : 0F;
                float maxY = (facing == Direction.DOWN) ? 1F : (pressed ? 1F/16F : 2F/16F);

                BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(sideQuad, minXZ, minY, maxXZ, maxY))
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, largeSide ? 10F/16F : 11F/16F);
                    quadMap.get(null).add(sideQuad);
                }
            }
        }
        else
        {
            if (quad.getDirection() == facing || quad.getDirection() == facing.getOpposite())
            {
                BakedQuad faceQuad = ModelUtils.duplicateQuad(quad);
                if (BakedQuadTransformer.createSideQuad(faceQuad, 5F/16F, 6F/16F, 11F/16F, 10F/16F))
                {
                    if (quad.getDirection() == facing)
                    {
                        BakedQuadTransformer.setQuadPosInFacingDir(faceQuad, pressed ? 1F/16F : 2F/16F);
                        quadMap.get(null).add(faceQuad);
                    }
                    else
                    {
                        quadMap.get(quad.getDirection()).add(faceQuad);
                    }
                }
            }
            else
            {
                boolean xAxis = facing.getAxis() == Direction.Axis.X;
                boolean negative = facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE;
                float minX;
                float maxX;
                float minZ;
                float maxZ;
                if (pressed)
                {
                    minX = xAxis ? (negative ? 15F/16F : 0F) :  5F/16F;
                    maxX = xAxis ? (negative ? 1F :  1F/16F) : 11F/16F;
                    minZ = xAxis ?  5F/16F : (negative ? 15F/16F : 0F);
                    maxZ = xAxis ? 11F/16F : (negative ? 1F :  1F/16F);
                }
                else
                {
                    minX = xAxis ? (negative ? 14F/16F : 0F) :  5F/16F;
                    maxX = xAxis ? (negative ? 1F :  2F/16F) : 11F/16F;
                    minZ = xAxis ?  5F/16F : (negative ? 14F/16F : 0F);
                    maxZ = xAxis ? 11F/16F : (negative ? 1F :  2F/16F);
                }

                if (quad.getDirection().getAxis() == Direction.Axis.Y)
                {
                    BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
                    if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, minX, minZ, maxX, maxZ))
                    {
                        BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, 10F / 16F);
                        quadMap.get(null).add(topBotQuad);
                    }
                }
                else
                {
                    float minXZ = xAxis ? minX : minZ;
                    float maxXZ = xAxis ? maxX : maxZ;

                    BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
                    if (BakedQuadTransformer.createSideQuad(sideQuad, minXZ, 6F/16F, maxXZ, 10F/16F))
                    {
                        BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 11F/16F);
                        quadMap.get(null).add(sideQuad);
                    }
                }
            }
        }
    }
}
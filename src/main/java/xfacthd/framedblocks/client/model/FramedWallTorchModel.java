package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.BakedQuadTransformer;
import xfacthd.framedblocks.client.util.ModelUtils;

import java.util.*;

public class FramedWallTorchModel extends FramedBlockModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(FramedBlocks.MODID, "block/framed_torch");
    private static final Vector3f ROTATION_ORIGIN = new Vector3f(0, 3.5F/16F, 8F/16F);

    private final Direction dir;

    public FramedWallTorchModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.get(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, Random rand, IModelData extraData)
    {
        List<BakedQuad> quads = baseModel.getQuads(state, null, rand, extraData);
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
        /*
        "from": [-1, 3.5, 7],
		"to": [1, 11.5, 9],
        "rotation": {"angle": -22.5, "axis": "z", "origin": [0, 3.5, 8]},
        */

        if (quad.getFace().getAxis() == Direction.Axis.Y)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 7F/16F, 7F/16F, 9F/16F, 9F/16F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(
                        topBotQuad,
                        quad.getFace() == Direction.UP ? 11.5F/16F : 12.5F/16F
                );
                BakedQuadTransformer.offsetQuadInDir(topBotQuad, Direction.WEST, 8F/16F);
                quadMap.get(null).add(topBotQuad);
            }
        }
        else
        {
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 7F/16F, 0F, 9F/16F, 8F/16F))
            {
                if (quad.getFace() == Direction.EAST)
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 1F/16F);
                }
                else if (quad.getFace() == Direction.WEST)
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 17F/16F);
                }
                else
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 9F/16F);
                    BakedQuadTransformer.offsetQuadInDir(sideQuad, Direction.WEST, 8F/16F);
                }
                BakedQuadTransformer.offsetQuadInDir(sideQuad, Direction.UP, 3.5F/16F);
                quadMap.get(null).add(sideQuad);
            }
        }
    }

    @Override
    protected void postProcessQuads(Map<Direction, List<BakedQuad>> quadMap)
    {
        float yAngle = 270F - dir.getHorizontalAngle();
        quadMap.get(null).forEach(q ->
        {
            BakedQuadTransformer.rotateQuadAroundAxis(q, Direction.Axis.Z, ROTATION_ORIGIN, -22.5F, false);
            BakedQuadTransformer.rotateQuadAroundAxisCentered(q, Direction.Axis.Y, yAngle, false);
        });
    }

    @Override
    public boolean isAmbientOcclusion() { return false; }
}
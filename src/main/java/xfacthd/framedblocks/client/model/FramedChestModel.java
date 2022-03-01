package xfacthd.framedblocks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.client.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;

import java.util.*;

public class FramedChestModel extends FramedBlockModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(FramedBlocks.MODID, "block/framed_chest_lock");

    private final Direction facing;
    private final boolean closed;
    private final LatchType latch;

    public FramedChestModel(BlockState state, IBakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(PropertyHolder.FACING_HOR);
        this.closed = state.getValue(PropertyHolder.CHEST_STATE) == ChestState.CLOSED || ClientUtils.OPTIFINE_LOADED.get();
        this.latch = state.getValue(PropertyHolder.LATCH_TYPE);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (quad.getDirection().getAxis() == Direction.Axis.Y)
        {
            BakedQuad topBotQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createTopBottomQuad(topBotQuad, 1F/16F, 1F/16F, 15F/16F, 15F/16F))
            {
                if (topBotQuad.getDirection() == Direction.UP)
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(topBotQuad, closed ? 14F/16F : 10F/16F);
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
            BakedQuad sideQuad = ModelUtils.duplicateQuad(quad);
            if (BakedQuadTransformer.createSideQuad(sideQuad, 1F/16F, 0, 15F/16F, closed ? 14F/16F : 10F/16F))
            {
                BakedQuadTransformer.setQuadPosInFacingDir(sideQuad, 15F/16F);
                quadMap.get(null).add(sideQuad);
            }
        }

        if (latch == LatchType.CAMO)
        {
            makeChestLatch(quadMap, quad, facing);
        }
    }

    public static void makeChestLatch(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, Direction facing)
    {
        Direction face = quad.getDirection();
        BakedQuad copy = ModelUtils.duplicateQuad(quad);

        if (face == facing || face == facing.getOpposite())
        {
            if (BakedQuadTransformer.createSideQuad(copy, 7F/16F, 7F/16F, 9F/16F, 11F/16F))
            {
                if (face == facing)
                {
                    quadMap.get(facing).add(copy);
                }
                else
                {
                    BakedQuadTransformer.setQuadPosInFacingDir(copy, 1F/16F);
                    quadMap.get(null).add(copy);
                }
            }
        }
        else if (face.getAxis() == Direction.Axis.Y)
        {
            if (BakedQuadTransformer.createTopBottomQuad(copy, facing.getOpposite(), 1F/16F) &&
                    BakedQuadTransformer.createTopBottomQuad(copy, facing.getClockWise(), 9F/16F) &&
                    BakedQuadTransformer.createTopBottomQuad(copy, facing.getCounterClockWise(), 9F/16F)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(copy, face == Direction.UP ? 11F/16F : 9F/16F);
                quadMap.get(null).add(copy);
            }
        }
        else
        {
            if (BakedQuadTransformer.createSideQuad(copy, 0, 7F/16F, 1, 11F/16F) &&
                    BakedQuadTransformer.createVerticalSideQuad(copy, facing.getOpposite(), 1F/16F)
            )
            {
                BakedQuadTransformer.setQuadPosInFacingDir(copy, 9F/16F);
                quadMap.get(null).add(copy);
            }
        }
    }

    @Override
    protected boolean hasAdditionalQuadsInLayer(RenderType layer) { return latch == LatchType.DEFAULT && layer == RenderType.cutout(); }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, Random rand, IModelData data, RenderType layer)
    {
        if (!closed || latch != LatchType.DEFAULT) { return; }

        List<BakedQuad> quads = baseModel.getQuads(state, null, rand, data);
        for (BakedQuad quad : quads)
        {
            if (quad.getSprite().getName().equals(TEXTURE))
            {
                quadMap.get(null).add(quad);
            }
        }
    }



    public static BlockState itemSource() { return FBContent.blockFramedChest.get().defaultBlockState(); }
}
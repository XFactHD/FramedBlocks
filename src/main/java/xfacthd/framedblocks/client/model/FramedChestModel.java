package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.util.client.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.ChestState;
import xfacthd.framedblocks.common.data.property.LatchType;

import java.util.*;

public class FramedChestModel extends FramedBlockModel
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(FramedConstants.MOD_ID, "block/framed_chest_lock");

    private final Direction facing;
    private final boolean closed;
    private final LatchType latch;
    private final ChunkRenderTypeSet addLayers;

    public FramedChestModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.closed = state.getValue(PropertyHolder.CHEST_STATE) == ChestState.CLOSED || ClientUtils.OPTIFINE_LOADED.get();
        this.latch = state.getValue(PropertyHolder.LATCH_TYPE);
        this.addLayers = latch == LatchType.DEFAULT ? ModelUtils.CUTOUT : ChunkRenderTypeSet.none();
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        if (Utils.isY(quad.getDirection()))
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
        else if (Utils.isY(face))
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
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return addLayers;
    }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, RandomSource rand, ModelData data, RenderType renderType)
    {
        if (!closed || latch != LatchType.DEFAULT) { return; }

        List<BakedQuad> quads = baseModel.getQuads(state, null, rand, data, renderType);
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
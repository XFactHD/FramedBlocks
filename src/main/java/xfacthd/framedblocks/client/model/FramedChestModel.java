package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.util.client.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.ChestState;
import xfacthd.framedblocks.common.data.property.LatchType;

import java.util.*;

public class FramedChestModel extends FramedBlockModel
{
    private final Direction facing;
    private final boolean closed;
    private final LatchType latch;

    public FramedChestModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.facing = state.getValue(FramedProperties.FACING_HOR);
        this.closed = state.getValue(PropertyHolder.CHEST_STATE) == ChestState.CLOSED || ClientUtils.OPTIFINE_LOADED.get();
        this.latch = state.getValue(PropertyHolder.LATCH_TYPE);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (Utils.isY(quad.getDirection()))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(1F/16F, 1F/16F, 15F/16F, 15F/16F))
                    .applyIf(Modifiers.setPosition(closed ? 14F/16F : 10F/16F), quadDir == Direction.UP)
                    .export(quadMap.get(quadDir == Direction.UP ? null : quadDir));
        }
        else
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(1F/16F, 0, 15F/16F, closed ? 14F/16F : 10F/16F))
                    .apply(Modifiers.setPosition(15F/16F))
                    .export(quadMap.get(null));
        }

        if (latch == LatchType.CAMO)
        {
            makeChestLatch(quadMap, quad, facing);
        }
    }

    public static void makeChestLatch(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, Direction facing)
    {
        Direction face = quad.getDirection();

        if (face == facing || face == facing.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(7F/16F, 7F/16F, 9F/16F, 11F/16F))
                    .applyIf(Modifiers.setPosition(1F/16F), face != facing)
                    .export(quadMap.get(face == facing ? facing : null));
        }
        else if (Utils.isY(face))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(facing.getOpposite(), 1F/16F))
                    .apply(Modifiers.cutTopBottom(facing.getClockWise(), 9F/16F))
                    .apply(Modifiers.cutTopBottom(facing.getCounterClockWise(), 9F/16F))
                    .apply(Modifiers.setPosition(face == Direction.UP ? 11F/16F : 9F/16F))
                    .export(quadMap.get(null));
        }
        else
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(0, 7F/16F, 1, 11F/16F))
                    .apply(Modifiers.cutSideLeftRight(facing.getOpposite(), 1F/16F))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadMap.get(null));
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
            quadMap.get(null).add(quad);
        }
    }



    public static BlockState itemSource() { return FBContent.blockFramedChest.get().defaultBlockState(); }
}
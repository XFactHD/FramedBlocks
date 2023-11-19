package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.ChestState;
import xfacthd.framedblocks.common.data.property.LatchType;

import java.util.List;

public class FramedChestGeometry implements Geometry
{
    private final BakedModel baseModel;
    private final Direction facing;
    private final boolean closed;
    private final LatchType latch;
    private final ChunkRenderTypeSet addLayers;

    public FramedChestGeometry(GeometryFactory.Context ctx)
    {
        this.baseModel = ctx.baseModel();
        this.facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.closed = ctx.state().getValue(PropertyHolder.CHEST_STATE) == ChestState.CLOSED || ClientUtils.OPTIFINE_LOADED.get();
        this.latch = ctx.state().getValue(PropertyHolder.LATCH_TYPE);
        this.addLayers = latch == LatchType.DEFAULT ? ModelUtils.CUTOUT : ChunkRenderTypeSet.none();
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
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

        if (latch == LatchType.CAMO && closed)
        {
            makeChestLatch(quadMap, quad, facing);
        }
    }

    public static void makeChestLatch(QuadMap quadMap, BakedQuad quad, Direction facing)
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
    public ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return addLayers;
    }

    @Override
    public void getAdditionalQuads(
            QuadMap quadMap,
            BlockState state,
            RandomSource rand,
            ModelData data,
            RenderType renderType
    )
    {
        if (!closed || latch != LatchType.DEFAULT)
        {
            return;
        }

        List<BakedQuad> quads = baseModel.getQuads(state, null, rand, data, renderType);
        for (BakedQuad quad : quads)
        {
            quadMap.get(null).add(quad);
        }
    }



    public static BlockState itemSource()
    {
        return FBContent.BLOCK_FRAMED_CHEST.value().defaultBlockState();
    }
}
package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.LatchType;

import java.util.List;

public class FramedChestLidGeometry extends Geometry
{
    private final BlockState state;
    private final BakedModel baseModel;
    private final Direction facing;
    private final ChestType type;
    private final LatchType latch;
    private final ChunkRenderTypeSet addLayers;

    public FramedChestLidGeometry(GeometryFactory.Context ctx)
    {
        this.state = ctx.state();
        this.baseModel = ctx.baseModel();
        this.facing = ctx.state().getValue(FramedProperties.FACING_HOR);
        this.type = ctx.state().getValue(BlockStateProperties.CHEST_TYPE);
        this.latch = ctx.state().getValue(PropertyHolder.LATCH_TYPE);
        this.addLayers = latch == LatchType.DEFAULT ? ModelUtils.CUTOUT : ChunkRenderTypeSet.none();
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir))
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(facing.getAxis(), 15F/16F))
                    .applyIf(Modifiers.cutTopBottom(facing.getClockWise(), 15F/16F), type != ChestType.LEFT)
                    .applyIf(Modifiers.cutTopBottom(facing.getCounterClockWise(), 15F/16F), type != ChestType.RIGHT)
                    .apply(Modifiers.setPosition(quadDir == Direction.UP ? 14F/16F : 7F/16F))
                    .export(quadMap.get(quadDir == Direction.UP ? null : quadDir));
        }
        else if (quadDir.getAxis() == facing.getAxis())
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideUpDown(false, 14F/16F))
                    .apply(Modifiers.cutSideUpDown(true, 7F/16F))
                    .applyIf(Modifiers.cutSideLeftRight(facing.getClockWise(), 15F/16F), type != ChestType.LEFT)
                    .applyIf(Modifiers.cutSideLeftRight(facing.getCounterClockWise(), 15F/16F), type != ChestType.RIGHT)
                    .apply(Modifiers.setPosition(15F/16F))
                    .export(quadMap.get(null));
        }
        else
        {
            boolean offset = (type != ChestType.RIGHT || quadDir != facing.getCounterClockWise()) && (type != ChestType.LEFT || quadDir != facing.getClockWise());
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSideUpDown(false, 14F/16F))
                    .apply(Modifiers.cutSideUpDown(true, 7F/16F))
                    .apply(Modifiers.cutSideLeftRight(15F/16F))
                    .applyIf(Modifiers.setPosition(15F/16F), offset)
                    .export(quadMap.get(offset ? null : quadDir));
        }

        if (latch == LatchType.CAMO)
        {
            FramedChestGeometry.makeChestLatch(quadMap, quad, facing, type);
        }
    }

    @Override
    public ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return addLayers;
    }

    @Override
    public void getAdditionalQuads(QuadMap quadMap, RandomSource rand, ModelData data, RenderType layer)
    {
        List<BakedQuad> quads = baseModel.getQuads(state, null, rand, data, layer);
        for (BakedQuad quad : quads)
        {
            quadMap.get(null).add(quad);
        }
    }
}

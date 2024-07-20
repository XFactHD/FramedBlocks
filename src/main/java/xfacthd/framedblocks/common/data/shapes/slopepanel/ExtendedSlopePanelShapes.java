package xfacthd.framedblocks.common.data.shapes.slopepanel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.common.block.slopepanel.SlopePanelShape;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;
import xfacthd.framedblocks.common.data.shapes.SplitShapeGenerator;

public final class ExtendedSlopePanelShapes implements SplitShapeGenerator
{
    public static final ExtendedSlopePanelShapes INSTANCE = new ExtendedSlopePanelShapes();
    public static final ShapeCache<HorizontalRotation> SHAPES = makeCache(SlopePanelShapes.SHAPES);
    public static final ShapeCache<HorizontalRotation> OCCLUSION_SHAPES = makeCache(SlopePanelShapes.OCCLUSION_SHAPES);
    private static final ShapeCache<ShapeKey> FINAL_SHAPES = makeFinalCache(SHAPES);
    private static final ShapeCache<ShapeKey> FINAL_OCCLUSION_SHAPES = makeFinalCache(OCCLUSION_SHAPES);

    private ExtendedSlopePanelShapes() { }

    @Override
    public ShapeProvider generate(ImmutableList<BlockState> states)
    {
        return generate(states, FINAL_SHAPES);
    }

    @Override
    public ShapeProvider generateOcclusionShapes(ImmutableList<BlockState> states)
    {
        return generate(states, FINAL_OCCLUSION_SHAPES);
    }

    private static ShapeProvider generate(ImmutableList<BlockState> states, ShapeCache<ShapeKey> cache)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            builder.put(state, cache.get(new ShapeKey(dir, rot)));
        }

        return ShapeProvider.of(builder.build());
    }

    private static ShapeCache<HorizontalRotation> makeCache(ShapeCache<SlopePanelShape> slopePanelCache)
    {
        return ShapeCache.createEnum(HorizontalRotation.class, map ->
        {
            VoxelShape shapePanel = CommonShapes.PANEL.get(Direction.NORTH);
            for (HorizontalRotation rot : HorizontalRotation.values())
            {
                VoxelShape shape = ShapeUtils.orUnoptimized(shapePanel, slopePanelCache.get(SlopePanelShape.get(rot, true)));
                map.put(rot, shape);
            }
        });
    }

    private static ShapeCache<ShapeKey> makeFinalCache(ShapeCache<HorizontalRotation> cache)
    {
        return ShapeCache.create(map ->
        {
            for (HorizontalRotation rot : HorizontalRotation.values())
            {
                ShapeUtils.makeHorizontalRotations(cache.get(rot), Direction.NORTH, map, rot, ShapeKey::new);
            }
        });
    }



    private record ShapeKey(Direction dir, HorizontalRotation rot) { }
}

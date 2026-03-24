package io.github.xfacthd.framedblocks.common.data.shapes.slopepanel;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.shapes.CommonShapes;
import io.github.xfacthd.framedblocks.api.shapes.ShapeCache;
import io.github.xfacthd.framedblocks.api.shapes.ShapeContainer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.common.block.slopepanel.SlopePanelShape;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class ExtendedSlopePanelShapes implements ShapeGenerator
{
    public static final ExtendedSlopePanelShapes INSTANCE = new ExtendedSlopePanelShapes();
    public static final ShapeCache<HorizontalRotation> SHAPES = makeCache(SlopePanelShapes.SHAPES);
    public static final ShapeCache<HorizontalRotation> OCCLUSION_SHAPES = makeCache(SlopePanelShapes.OCCLUSION_SHAPES);
    private static final ShapeCache<ShapeKey> FINAL_SHAPES = makeFinalCache(SHAPES);
    private static final ShapeCache<ShapeKey> FINAL_OCCLUSION_SHAPES = makeFinalCache(OCCLUSION_SHAPES);

    private ExtendedSlopePanelShapes() { }

    @Override
    public ShapeContainer generatePrimary(List<BlockState> states)
    {
        return generate(states, FINAL_SHAPES);
    }

    @Override
    public ShapeContainer generateOcclusion(List<BlockState> states)
    {
        return generate(states, FINAL_OCCLUSION_SHAPES);
    }

    private static ShapeContainer generate(List<BlockState> states, ShapeCache<ShapeKey> cache)
    {
        Map<BlockState, VoxelShape> map = new IdentityHashMap<>(states.size());

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
            map.put(state, cache.get(new ShapeKey(dir, rot)));
        }

        return ShapeContainer.of(map);
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

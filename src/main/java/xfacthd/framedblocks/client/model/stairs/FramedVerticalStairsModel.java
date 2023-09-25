package xfacthd.framedblocks.client.model.stairs;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.StairsType;

public class FramedVerticalStairsModel extends FramedBlockModel
{
    private final StairsType type;
    private final Direction dir;

    public FramedVerticalStairsModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        type = state.getValue(PropertyHolder.STAIRS_TYPE);
        dir = state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    protected void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (type == StairsType.VERTICAL && (quadDir == dir.getOpposite() || quadDir == dir.getClockWise()))
        {
            Direction cutDir = quadDir == dir.getOpposite() ? dir.getClockWise() : dir.getOpposite();

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir, .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir.getOpposite(), .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }

        if ((quadDir == Direction.UP && !type.isTop()) || (quadDir == Direction.DOWN && !type.isBottom()))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir, .5F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .export(quadMap.get(quadDir));
        }

        if ((quadDir == dir.getOpposite() || quadDir == dir.getClockWise()) && type != StairsType.VERTICAL)
        {
            boolean opposite = quadDir == dir.getOpposite();
            Direction cutDir = opposite ? dir.getClockWise() : dir.getOpposite();

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir, .5F))
                    .apply(Modifiers.cutSideUpDown(!type.isTop(), .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir, .5F))
                    .apply(Modifiers.cutSideUpDown(type.isTop(), .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));

            cutDir = opposite ? dir.getCounterClockWise() : dir;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(cutDir, .5F))
                    .apply(Modifiers.cutSideUpDown(!type.isTop(), .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }

        if ((quadDir == Direction.UP && type.isTop()) || (quadDir == Direction.DOWN && type.isBottom()))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), .5F))
                    .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir, .5F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), .5F))
                    .apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }

        if ((quadDir == dir || quadDir == dir.getCounterClockWise()) && type != StairsType.VERTICAL)
        {
            boolean ccw = quadDir == dir.getCounterClockWise();

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(!type.isTop(), .5F))
                    .export(quadMap.get(quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(type.isTop(), .5F))
                    .apply(Modifiers.cutSideLeftRight(ccw ? dir.getOpposite() : dir.getClockWise(), .5F))
                    .export(quadMap.get(quadDir));
        }
    }
}
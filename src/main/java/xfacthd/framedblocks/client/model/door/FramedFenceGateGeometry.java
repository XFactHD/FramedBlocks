package xfacthd.framedblocks.client.model.door;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.geometry.Geometry;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;

public class FramedFenceGateGeometry extends Geometry
{
    private final Direction dir;
    private final boolean inWall;
    private final boolean open;

    public FramedFenceGateGeometry(GeometryFactory.Context ctx)
    {
        this.dir = ctx.state().getValue(BlockStateProperties.HORIZONTAL_FACING);
        this.inWall = ctx.state().getValue(BlockStateProperties.IN_WALL);
        this.open = ctx.state().getValue(BlockStateProperties.OPEN);
    }

    @Override
    public void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        float yOff = inWall ? 3F/16F : 0F;
        if (Utils.isY(quadDir))
        {
            float quadInset = quadDir == Direction.UP ? 1F - yOff : 11F/16F + yOff;

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), 2F/16F))
                    .apply(Modifiers.cutTopBottom(dir, 9F/16F))
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), 9F/16F))
                    .apply(Modifiers.setPosition(quadInset))
                    .export(quadMap.get(inWall || quadDir == Direction.DOWN ? null : quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), 2F/16F))
                    .apply(Modifiers.cutTopBottom(dir, 9F/16F))
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), 9F/16F))
                    .apply(Modifiers.setPosition(quadInset))
                    .export(quadMap.get(inWall || quadDir == Direction.DOWN ? null : quadDir));
        }
        else if (quadDir == dir || quadDir == dir.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getClockWise(), 2F/16F))
                    .apply(Modifiers.cutSideUpDown(true, 11F/16F + yOff))
                    .apply(Modifiers.cutSideUpDown(false, 1F - yOff))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getCounterClockWise(), 2F/16F))
                    .apply(Modifiers.cutSideUpDown(true, 11F/16F + yOff))
                    .apply(Modifiers.cutSideUpDown(false, 1F - yOff))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadMap.get(null));
        }
        else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise())
        {
            QuadModifier mod = QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(7F/16F, 5F/16F - yOff, 9F/16F, 1F - yOff));

            mod.export(quadMap.get(quadDir));

            mod.derive()
                    .apply(Modifiers.setPosition(2F/16F))
                    .export(quadMap.get(null));
        }

        if (open)
        {
            createGateOpen(quadMap, quad, yOff);
        }
        else
        {
            createGateClosed(quadMap.get(null), quad, yOff);
        }
    }

    private void createGateClosed(List<BakedQuad> quadList, BakedQuad quad, float yOff)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir == dir || quadDir == dir.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(2F/16F, 12F/16F - yOff, 14F/16F, 15F/16F - yOff))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadList);

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(2F/16F, 6F/16F - yOff, 14F/16F, 9F/16F - yOff))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadList);

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(6F/16F, 9F/16F - yOff, 10F/16F, 12F/16F - yOff))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadList);
        }
        else if (Utils.isY(quadDir))
        {
            QuadModifier mod = QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir, 9F/16F))
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), 9F/16F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), 14F/16F))
                    .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), 14F/16F));

            if (mod.hasFailed())
            {
                return;
            }

            boolean up = quadDir == Direction.UP;
            float height = up ? 9F / 16F - yOff : 4F / 16F + yOff;

            mod.derive().apply(Modifiers.setPosition(up ? 15F/16F - yOff : 10F/16F + yOff))
                    .export(quadList);

            mod.derive().apply(Modifiers.cutTopBottom(dir.getClockWise(), 6F/16F))
                    .apply(Modifiers.setPosition(height))
                    .export(quadList);

            mod.apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), 6F/16F))
                    .apply(Modifiers.setPosition(height))
                    .export(quadList);
        }
        else if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(7F/16F, 9F/16F - yOff, 9F/16F, 12F/16F - yOff))
                    .apply(Modifiers.setPosition(10F/16F))
                    .export(quadList);
        }
    }

    private void createGateOpen(QuadMap quadMap, BakedQuad quad, float yOff)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir == dir.getClockWise() || quadDir == dir.getCounterClockWise())
        {
            QuadModifier mod = QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), 7F/16F))
                    .apply(Modifiers.cutSideLeftRight(dir, 15F/16F))
                    .apply(Modifiers.cutSideUpDown(false, 15F/16F - yOff))
                    .apply(Modifiers.cutSideUpDown(true, 10F/16F + yOff));

            QuadModifier topMod = mod.derive()
                    .apply(Modifiers.cutSideUpDown(true, 4F/16F + yOff));

            topMod.derive().export(quadMap.get(null));
            topMod.apply(Modifiers.setPosition(2F/16F))
                    .export(quadMap.get(null));

            QuadModifier botMod = mod.derive()
                    .apply(Modifiers.cutSideUpDown(false, 9F/16F - yOff));

            botMod.derive().export(quadMap.get(null));
            botMod.apply(Modifiers.setPosition(2F/16F))
                    .export(quadMap.get(null));

            mod.apply(Modifiers.cutSideUpDown(false, 12F/16F - yOff))
                    .apply(Modifiers.cutSideUpDown(true, 7F/16F + yOff))
                    .apply(Modifiers.cutSideLeftRight(dir.getOpposite(), 3F/16F))
                    .derive()
                    .export(quadMap.get(null));

            mod.apply(Modifiers.setPosition(2F/16F))
                    .export(quadMap.get(null));
        }
        else if (Utils.isY(quadDir))
        {
            boolean up = quadDir == Direction.UP;
            float heightOuter = up ? 15F/16F - yOff : 10F/16F + yOff;
            float heightInner = up ? 9F/16F - yOff : 4F/16F + yOff;

            QuadModifier mod = QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir, 15F/16F))
                    .apply(Modifiers.cutTopBottom(dir.getOpposite(), 7F/16F));

            QuadModifier leftMod = mod.derive()
                    .apply(Modifiers.cutTopBottom(dir.getClockWise(), 2F/16F))
                    .apply(Modifiers.setPosition(heightOuter));

            leftMod.derive().export(quadMap.get(null));
            leftMod.apply(Modifiers.cutTopBottom(dir, 13F/16F))
                    .apply(Modifiers.setPosition(heightInner))
                    .export(quadMap.get(null));

            QuadModifier rightMod = mod
                    .apply(Modifiers.cutTopBottom(dir.getCounterClockWise(), 2F/16F))
                    .apply(Modifiers.setPosition(heightOuter));

            rightMod.derive().export(quadMap.get(null));
            rightMod.apply(Modifiers.cutTopBottom(dir, 13F/16F))
                    .apply(Modifiers.setPosition(heightInner))
                    .export(quadMap.get(null));
        }
        else if (quadDir == dir)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(0F, 6F/16F - yOff, 2F/16F, 15F/16F - yOff))
                    .apply(Modifiers.setPosition(15F/16F))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(14F/16F, 6F/16F - yOff, 1F, 15F/16F - yOff))
                    .apply(Modifiers.setPosition(15F/16F))
                    .export(quadMap.get(null));
        }
        else if (quadDir == dir.getOpposite())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(0F, 9F/16F - yOff, 2F/16F, 12F/16F - yOff))
                    .apply(Modifiers.setPosition(3F/16F))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(14F/16F, 9F/16F - yOff, 1F, 12F/16F - yOff))
                    .apply(Modifiers.setPosition(3F/16F))
                    .export(quadMap.get(null));
        }
    }

    @Override
    public boolean useSolidNoCamoModel()
    {
        return true;
    }
}
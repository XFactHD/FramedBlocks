package xfacthd.framedblocks.client.model.interactive;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

public class FramedButtonModel extends FramedBlockModel
{
    protected final Direction dir;
    protected final AttachFace face;
    protected final Direction facing;
    protected final boolean pressed;

    public FramedButtonModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        face = state.getValue(BlockStateProperties.ATTACH_FACE);
        facing = getFacing(dir, face);
        pressed = state.getValue(BlockStateProperties.POWERED);
    }

    @Override
    protected void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (Utils.isY(facing))
        {
            generateVerticalButton(quadMap, quad, quadDir);
        }
        else
        {
            generateHorizontalButton(quadMap, quad, quadDir);
        }
    }

    private void generateVerticalButton(QuadMap quadMap, BakedQuad quad, Direction quadDir)
    {
        if (quadDir.getAxis() == facing.getAxis())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getAxis(), 10F/16F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise().getAxis(), 11F/16F))
                    .applyIf(Modifiers.setPosition(pressed ? 1F/16F : 2F/16F), quadDir == facing)
                    .export(quadMap.get(quadDir == facing ? null : quadDir));
        }
        else
        {
            boolean largeSide = dir.getAxis() == quadDir.getAxis();

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(facing == Direction.DOWN, pressed ? 1F/16F : 2F/16F))
                    .apply(Modifiers.cutSideLeftRight(largeSide ? 11F/16F : 10F/16F))
                    .apply(Modifiers.setPosition(largeSide ? 10F/16F : 11F/16F))
                    .export(quadMap.get(null));
        }
    }

    private void generateHorizontalButton(QuadMap quadMap, BakedQuad quad, Direction quadDir)
    {
        float height = pressed ? 1F/16F : 2F/16F;
        if (quadDir.getAxis() == facing.getAxis())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(5F/16F, 6F/16F, 11F/16F, 10F/16F))
                    .applyIf(Modifiers.setPosition(height), quadDir == facing)
                    .export(quadMap.get(quadDir == facing ? null : quadDir));
        }
        else if (Utils.isY(quadDir))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir, height))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise().getAxis(), 11F/16F))
                    .apply(Modifiers.setPosition(10F / 16F))
                    .export(quadMap.get(null));
        }
        else
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir, height))
                    .apply(Modifiers.cutSideUpDown(10F/16F))
                    .apply(Modifiers.setPosition(11F / 16F))
                    .export(quadMap.get(null));
        }
    }



    private static Direction getFacing(Direction dir, AttachFace face)
    {
        return switch (face)
        {
            case FLOOR -> Direction.UP;
            case CEILING -> Direction.DOWN;
            case WALL -> dir;
        };
    }
}
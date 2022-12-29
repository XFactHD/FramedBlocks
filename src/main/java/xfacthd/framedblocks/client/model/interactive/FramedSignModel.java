package xfacthd.framedblocks.client.model.interactive;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.Map;

public class FramedSignModel extends FramedBlockModel
{
    private static final float Y_OFF = 1.75F/16F;
    private static final float POS = 9F/16F;
    private final Direction dir;
    private final float rotDegrees;

    public FramedSignModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        int rotation = state.getValue(BlockStateProperties.ROTATION_16);
        dir = Direction.from2DDataValue(rotation / 4);
        rotDegrees = (float)(rotation % 4) * -22.5F;
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir.getAxis() == dir.getAxis())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(true, .5F))
                    .apply(Modifiers.setPosition(POS))
                    .apply(Modifiers.offset(Direction.UP, Y_OFF))
                    .apply(Modifiers.rotateCentered(Direction.Axis.Y, rotDegrees, false))
                    .export(quadMap.get(null));
        }
        else if (Utils.isY(quadDir))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getAxis(), 9F / 16F))
                    .applyIf(Modifiers.setPosition(.5F), quadDir == Direction.DOWN)
                    .apply(Modifiers.offset(Direction.UP, Y_OFF))
                    .apply(Modifiers.rotateCentered(Direction.Axis.Y, rotDegrees, false))
                    .export(quadMap.get(null));
        }
        else
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(7F / 16F, .5F, 9F / 16F, 1F))
                    .apply(Modifiers.offset(Direction.UP, Y_OFF))
                    .apply(Modifiers.rotateCentered(Direction.Axis.Y, rotDegrees, false))
                    .export(quadMap.get(null));
        }

        if (!Utils.isY(quadDir))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(7F / 16F, 0F, 9F / 16F, 9.75F / 16F))
                    .apply(Modifiers.setPosition(POS))
                    .apply(Modifiers.rotateCentered(Direction.Axis.Y, rotDegrees, false))
                    .export(quadMap.get(null));
        }
        else if (quadDir == Direction.DOWN)
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(7F / 16F, 7F / 16F, 9F / 16F, 9F / 16F))
                    .apply(Modifiers.rotateCentered(Direction.Axis.Y, rotDegrees, false))
                    .export(quadMap.get(Direction.DOWN));
        }
    }
}
package xfacthd.framedblocks.client.model.cube;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Vector3f;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.Map;

public class FramedMiniCubeModel extends FramedBlockModel
{
    private static final Vector3f ORIGIN = new Vector3f(.5F, 0, .5F);

    private final float rotAngle;

    public FramedMiniCubeModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        int rot = state.getValue(BlockStateProperties.ROTATION_16);
        this.rotAngle = (4 - (rot % 4)) * 22.5F;
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();


        QuadModifier.geometry(quad)
                .apply(Modifiers.scaleFace(.5F, ORIGIN))
                .applyIf(Modifiers.setPosition(.5F), quadDir == Direction.UP)
                .applyIf(Modifiers.setPosition(.75F), !Utils.isY(quadDir))
                .apply(Modifiers.rotate(Direction.Axis.Y, ORIGIN, rotAngle, false))
                .export(quadMap.get(quadDir == Direction.DOWN ? quadDir : null));
    }
}

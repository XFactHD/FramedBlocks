package xfacthd.framedblocks.client.model.interactive;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.Map;

public class FramedCeilingHangingSignModel extends FramedBlockModel
{
    private final Direction dir;
    private final float rotDegrees;
    private final boolean attached;

    public FramedCeilingHangingSignModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        int rotation = state.getValue(BlockStateProperties.ROTATION_16);
        this.dir = Direction.from2DDataValue(rotation / 4);
        this.rotDegrees = (float) (rotation % 4) * -22.5F;
        this.attached = state.getValue(BlockStateProperties.ATTACHED);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir.getAxis() == dir.getAxis())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(false, 10F/16F))
                    .apply(Modifiers.cutSideLeftRight(15F/16F))
                    .apply(Modifiers.setPosition(9F/16F))
                    .applyIf(Modifiers.rotateCentered(Direction.Axis.Y, rotDegrees, false), attached)
                    .export(quadMap.get(null));
        }
        else if (quadDir.getAxis() == dir.getClockWise().getAxis())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(false, 10F/16F))
                    .apply(Modifiers.cutSideLeftRight(9F/16F))
                    .apply(Modifiers.setPosition(15F/16F))
                    .applyIf(Modifiers.rotateCentered(Direction.Axis.Y, rotDegrees, false), attached)
                    .export(quadMap.get(null));
        }
        else
        {
            boolean up = quadDir == Direction.UP;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getAxis(), 9F/16F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise().getAxis(), 15F/16F))
                    .applyIf(Modifiers.setPosition(10F/16F), up)
                    .applyIf(Modifiers.rotateCentered(Direction.Axis.Y, rotDegrees, false), attached)
                    .export(quadMap.get(up ? null : quadDir));
        }
    }

    @Override
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return ModelUtils.CUTOUT;
    }

    @Override
    protected void getAdditionalQuads(
            Map<Direction, List<BakedQuad>> quadMap, BlockState state, RandomSource rand, ModelData data, RenderType renderType
    )
    {
        if (renderType == RenderType.cutout())
        {
            Utils.forAllDirections(dir ->
            {
                List<BakedQuad> quads = quadMap.get(dir);
                for (BakedQuad quad : baseModel.getQuads(state, dir, rand, data, renderType))
                {
                    QuadModifier.geometry(quad)
                            .applyIf(Modifiers.rotateCentered(Direction.Axis.Y, rotDegrees, false), attached)
                            .export(quads);
                }
            });
        }
    }
}

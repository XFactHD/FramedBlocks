package xfacthd.framedblocks.client.model.interactive;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;

import java.util.List;
import java.util.Map;

public class FramedPressurePlateModel extends FramedBlockModel
{
    private final boolean pressed;

    public FramedPressurePlateModel(BlockState state, BakedModel baseModel)
    {
        this(state, baseModel, state.getValue(BlockStateProperties.POWERED));
    }

    protected FramedPressurePlateModel(BlockState state, BakedModel baseModel, boolean powered)
    {
        super(state, baseModel);
        this.pressed = powered;
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        float height = pressed ? .5F / 16F : 1F / 16F;

        if (Utils.isY(quadDir))
        {
            boolean up = quadDir == Direction.UP;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(1F/16F, 1F/16F, 15F/16F, 15F/16F))
                    .applyIf(Modifiers.setPosition(height), up)
                    .export(quadMap.get(up ? null : Direction.DOWN));
        }
        else
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(1F/16F, 0F, 15F/16F, height))
                    .apply(Modifiers.setPosition(15F/16F))
                    .export(quadMap.get(null));
        }
    }

    @Override
    protected boolean useBaseModel()
    {
        return !state.is(FBContent.BLOCK_FRAMED_PRESSURE_PLATE.get()) && !state.is(FBContent.BLOCK_FRAMED_WATERLOGGABLE_PRESSURE_PLATE.get());
    }
}
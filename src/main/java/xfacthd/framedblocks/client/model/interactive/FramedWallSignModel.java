package xfacthd.framedblocks.client.model.interactive;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

public class FramedWallSignModel extends FramedBlockModel
{
    private static final float DEPTH = 2F/16F;
    private final Direction dir;

    public FramedWallSignModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    protected void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir.getAxis() == dir.getAxis())
        {
            boolean inset = quadDir == dir;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(0F, 4.5F/16F, 1F, 12.5F/16F))
                    .applyIf(Modifiers.setPosition(DEPTH), inset)
                    .export(quadMap.get(inset ? null : quadDir));
        }
        else if (Utils.isY(quadDir))
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir, DEPTH))
                    .apply(Modifiers.setPosition(quadDir == Direction.UP ? 12.5F/16F : 11.5F/16F))
                    .export(quadMap.get(null));
        }
        else
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideLeftRight(dir, DEPTH))
                    .apply(Modifiers.cutSideUpDown(false, 12.5F/16F))
                    .apply(Modifiers.cutSideUpDown(true, 11.5F/16F))
                    .export(quadMap.get(quadDir));
        }
    }
}
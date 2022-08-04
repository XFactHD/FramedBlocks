package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.Map;

public class FramedHalfPillarModel extends FramedBlockModel
{
    private final Direction face;

    public FramedHalfPillarModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        face = state.getValue(BlockStateProperties.FACING);
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
    {
        QuadModifier mod = FramedPillarModel.createPillarQuad(quad, face.getAxis(), 4F / 16F, 12F / 16F, 12F / 16F);
        if (mod.hasFailed()) { return; }

        Direction quadDir = quad.getDirection();
        if (quadDir == face)
        {
            mod.export(quadMap.get(face));
        }
        else if (quadDir == face.getOpposite())
        {
            mod.apply(Modifiers.setPosition(.5F))
                    .export(quadMap.get(null));
        }
        else if (Utils.isY(face))
        {
            mod.apply(Modifiers.cutSideUpDown(face == Direction.UP, .5F))
                    .export(quadMap.get(null));
        }
        else if (Utils.isY(quadDir))
        {
            mod.apply(Modifiers.cutTopBottom(face.getOpposite(), .5F))
                    .export(quadMap.get(null));
        }
        else
        {
            mod.apply(Modifiers.cutSideLeftRight(face.getOpposite(), .5F))
                    .export(quadMap.get(null));
        }
    }
}
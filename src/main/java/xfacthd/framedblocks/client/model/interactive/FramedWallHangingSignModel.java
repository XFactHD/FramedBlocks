package xfacthd.framedblocks.client.model.interactive;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.model.util.ModelUtils;
import xfacthd.framedblocks.api.util.Utils;

public class FramedWallHangingSignModel extends FramedBlockModel
{
    private final Direction dir;

    public FramedWallHangingSignModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.dir = state.getValue(FramedProperties.FACING_HOR);
    }

    @Override
    protected void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (quadDir.getAxis() == dir.getAxis())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(false, 10F/16F))
                    .apply(Modifiers.cutSideLeftRight(15F/16F))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(true, 2F/16F))
                    .apply(Modifiers.setPosition(10F/16F))
                    .export(quadMap.get(null));
        }
        else if (quadDir.getAxis() == dir.getClockWise().getAxis())
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(false, 10F/16F))
                    .apply(Modifiers.cutSideLeftRight(9F/16F))
                    .apply(Modifiers.setPosition(15F/16F))
                    .export(quadMap.get(null));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSideUpDown(true, 2F/16F))
                    .apply(Modifiers.cutSideLeftRight(10F/16F))
                    .export(quadMap.get(quadDir));
        }
        else
        {
            boolean up = quadDir == Direction.UP;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getAxis(), 9F/16F))
                    .apply(Modifiers.cutTopBottom(dir.getClockWise().getAxis(), 15F/16F))
                    .applyIf(Modifiers.setPosition(10F/16F), up)
                    .export(quadMap.get(up ? null : quadDir));

            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(dir.getAxis(), 10F/16F))
                    .applyIf(Modifiers.setPosition(2F/16F), !up)
                    .export(quadMap.get(up ? quadDir : null));
        }
    }

    @Override
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return ModelUtils.CUTOUT;
    }

    @Override
    protected void getAdditionalQuads(
            QuadMap quadMap, BlockState state, RandomSource rand, ModelData data, RenderType renderType
    )
    {
        if (renderType == RenderType.cutout())
        {
            Utils.forAllDirections(dir -> quadMap.get(dir).addAll(baseModel.getQuads(state, dir, rand, data, renderType)));
        }
    }
}

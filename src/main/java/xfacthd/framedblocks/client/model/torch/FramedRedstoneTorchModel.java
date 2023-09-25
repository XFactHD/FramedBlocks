package xfacthd.framedblocks.client.model.torch;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.data.QuadMap;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.api.model.util.ModelCache;

import java.util.List;

public class FramedRedstoneTorchModel extends FramedBlockModel
{
    private static final float MIN = 7F/16F;
    private static final float MAX = 9F/16F;
    private static final float TOP = 8F/16F;
    private static final float TOP_LIT = 7F/16F;

    private final boolean lit;

    public FramedRedstoneTorchModel(BlockState state, BakedModel baseModel)
    {
        super(state, baseModel);
        this.lit = state.getValue(BlockStateProperties.LIT);
    }

    @Override
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData)
    {
        return ModelCache.getRenderTypes(Blocks.REDSTONE_TORCH.defaultBlockState(), rand, ModelData.EMPTY);
    }

    @Override
    protected void getAdditionalQuads(
            QuadMap quadMap,
            BlockState state,
            RandomSource rand,
            ModelData extraData,
            RenderType layer
    )
    {
        List<BakedQuad> quads = baseModel.getQuads(state, null, rand, extraData, layer);
        for (BakedQuad quad : quads)
        {
            if (!ClientUtils.isDummyTexture(quad))
            {
                quadMap.get(null).add(quad);
            }
        }
    }

    @Override
    protected void transformQuad(QuadMap quadMap, BakedQuad quad)
    {
        Direction quadDir = quad.getDirection();
        if (Utils.isY(quadDir))
        {
            boolean top = quadDir == Direction.UP;
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutTopBottom(MIN, MIN, MAX, MAX))
                    .applyIf(Modifiers.setPosition(lit ? TOP_LIT : TOP), top)
                    .export(quadMap.get(top ? null : quadDir));
        }
        else
        {
            QuadModifier.geometry(quad)
                    .apply(Modifiers.cutSide(MIN, 0, MAX, lit ? TOP_LIT : TOP))
                    .apply(Modifiers.setPosition(MAX))
                    .export(quadMap.get(null));
        }
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return false;
    }
}
package xfacthd.framedblocks.client.model;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.data.IModelData;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.model.quad.Modifiers;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.ClientUtils;

import java.util.*;

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
    protected boolean hasAdditionalQuadsInLayer(RenderType layer)
    {
        return ItemBlockRenderTypes.canRenderInLayer(Blocks.REDSTONE_TORCH.defaultBlockState(), layer);
    }

    @Override
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, Random rand, IModelData extraData, RenderType layer)
    {
        List<BakedQuad> quads = baseModel.getQuads(state, null, rand, extraData);
        for (BakedQuad quad : quads)
        {
            if (!quad.getSprite().getName().equals(ClientUtils.DUMMY_TEXTURE))
            {
                quadMap.get(null).add(quad);
            }
        }
    }

    @Override
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad)
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
    public boolean useAmbientOcclusion() { return false; }
}
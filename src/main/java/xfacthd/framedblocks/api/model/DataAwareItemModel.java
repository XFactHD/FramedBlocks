package xfacthd.framedblocks.api.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public final class DataAwareItemModel extends BakedModelWrapper<BakedModel>
{
    private final ModelData itemData;
    private final RenderType renderType;
    private final List<RenderType> renderTypeList;

    public DataAwareItemModel(BakedModel baseModel, ModelData itemData, RenderType renderType, boolean cull)
    {
        super(baseModel);
        this.itemData = itemData;
        this.renderType = renderType;
        this.renderTypeList = List.of(RenderTypeHelper.getEntityRenderType(renderType, cull));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType)
    {
        if (this.renderType == renderType)
        {
            try
            {
                return originalModel.getQuads(state, side, rand, itemData, renderType);
            }
            catch (Throwable t)
            {
                return ErrorModel.get().getQuads(state, side, rand, data, renderType);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand)
    {
        return getQuads(state, side, rand, itemData, renderType);
    }

    @Override
    public List<RenderType> getRenderTypes(ItemStack stack, boolean fabulous)
    {
        return renderTypeList;
    }
}

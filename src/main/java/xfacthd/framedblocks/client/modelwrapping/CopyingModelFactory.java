package xfacthd.framedblocks.client.modelwrapping;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.common.util.Lazy;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.wrapping.ModelFactory;
import xfacthd.framedblocks.api.util.Utils;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class CopyingModelFactory implements ModelFactory
{
    private final Holder<Block> srcBlock;
    private final Lazy<ModelWrappingHandler> sourceWrapper;

    public CopyingModelFactory(Holder<Block> srcBlock)
    {
        this.srcBlock = srcBlock;
        this.sourceWrapper = Lazy.of(() -> ModelWrappingManager.getHandler(srcBlock.value()));
    }

    @Override
    public BakedModel create(GeometryFactory.Context ctx)
    {
        BlockState state = ctx.state();
        BlockState srcState = srcBlock.value().defaultBlockState();
        for (Property prop : state.getProperties())
        {
            if (srcState.hasProperty(prop))
            {
                srcState = srcState.setValue(prop, state.getValue(prop));
            }
        }
        ResourceLocation baseLoc = StateLocationCache.getLocationFromState(
                srcState, Utils.getKeyOrThrow(srcBlock).location()
        );
        BakedModel baseModel = ctx.modelAccessor().get(baseLoc);
        return sourceWrapper.get().wrapBlockModel(
                baseModel, srcState, ctx.modelAccessor(), null
        );
    }
}

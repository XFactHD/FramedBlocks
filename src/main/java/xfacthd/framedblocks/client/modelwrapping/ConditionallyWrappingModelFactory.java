package xfacthd.framedblocks.client.modelwrapping;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import xfacthd.framedblocks.api.model.wrapping.ModelFactory;
import xfacthd.framedblocks.client.model.FramedBlockModel;

import java.util.function.BooleanSupplier;

public final class ConditionallyWrappingModelFactory implements ModelFactory
{
    private final BooleanSupplier wrapToggle;
    private final ModelFactory wrappingFactory;
    private final CopyingModelFactory copyingFactory;
    private Boolean shouldWrap = null;

    public ConditionallyWrappingModelFactory(
            BooleanSupplier wrapToggle,
            GeometryFactory geometryFactory,
            Holder<Block> srcBlock
    )
    {
        this.wrapToggle = wrapToggle;
        this.wrappingFactory = ctx -> new FramedBlockModel(ctx, geometryFactory.create(ctx));
        this.copyingFactory = new CopyingModelFactory(srcBlock);
    }

    @Override
    public BakedModel create(GeometryFactory.Context ctx)
    {
        return shouldWrap() ? wrappingFactory.create(ctx) : copyingFactory.create(ctx);
    }

    private boolean shouldWrap()
    {
        if (shouldWrap == null)
        {
            shouldWrap = wrapToggle.getAsBoolean();
        }
        return shouldWrap;
    }

    @Override
    public void reset()
    {
        shouldWrap = null;
    }
}

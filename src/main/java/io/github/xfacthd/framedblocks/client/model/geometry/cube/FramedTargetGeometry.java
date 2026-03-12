package io.github.xfacthd.framedblocks.client.model.geometry.cube;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.geometry.PartConsumer;
import io.github.xfacthd.framedblocks.api.model.item.ItemModelInfo;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

public class FramedTargetGeometry extends Geometry
{
    public static final Identifier OVERLAY_LOCATION = Utils.id("block/target_overlay");
    public static final String OVERLAY_KEY = "overlay";
    public static final int OVERLAY_TINT_IDX = 1024;
    private static final ItemModelInfo ITEM_MODEL_INFO = new TargetItemModelInfo();

    private final BlockState state;
    private final BlockStateModel overlayModel;

    public FramedTargetGeometry(GeometryFactory.Context ctx)
    {
        this.state = ctx.state();
        this.overlayModel = ctx.auxModels().getModel(OVERLAY_KEY);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData) { }

    @Override
    public boolean hasAdditionalUncachedParts()
    {
        return true;
    }

    @Override
    public void collectAdditionalPartsUncached(PartConsumer consumer, BlockAndTintGetter level, BlockPos pos, RandomSource random, ModelData data)
    {
        consumer.acceptAll(overlayModel, level, pos, random, state, false, false, true, false, null, null);
    }

    @Override
    public ItemModelInfo getItemModelInfo()
    {
        return ITEM_MODEL_INFO;
    }

    private static final class TargetItemModelInfo implements ItemModelInfo
    {
        @Override
        public boolean isDataRequired()
        {
            return true;
        }

        @Override
        @Nullable
        public Object computeCacheKey(ItemStack stack)
        {
            return stack.get(FBContent.DC_TYPE_TARGET_COLOR);
        }
    }
}

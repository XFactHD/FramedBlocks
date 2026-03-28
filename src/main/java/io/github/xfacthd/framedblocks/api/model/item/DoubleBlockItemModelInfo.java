package io.github.xfacthd.framedblocks.api.model.item;

import io.github.xfacthd.framedblocks.api.block.IFramedDoubleBlock;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedDoubleBlockData;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jspecify.annotations.Nullable;

public class DoubleBlockItemModelInfo implements ItemModelInfo {
    public static final DoubleBlockItemModelInfo INSTANCE = new DoubleBlockItemModelInfo();

    @Override
    public final ModelData buildItemModelData(BlockState state, CamoList camos, @Nullable Holder<BlockOverlay> overlay) {
        AbstractFramedBlockData fbData = new FramedDoubleBlockData(
                ((IFramedDoubleBlock) state.getBlock()).getCache(state).getParts(),
                new FramedBlockData(state, camos.getCamo(0), false, overlay),
                new FramedBlockData(state, camos.getCamo(1), true, overlay)
        );

        ModelData.Builder builder = ModelData.builder().with(AbstractFramedBlockData.PROPERTY, fbData);
        appendItemModelData(builder, state);
        return builder.build();
    }

    protected void appendItemModelData(ModelData.Builder builder, BlockState state) { }
}

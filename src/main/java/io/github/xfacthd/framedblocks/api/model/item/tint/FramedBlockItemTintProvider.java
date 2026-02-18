package io.github.xfacthd.framedblocks.api.model.item.tint;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.model.util.ModelUtils;
import io.github.xfacthd.framedblocks.api.util.ClientUtils;
import io.github.xfacthd.framedblocks.api.util.ConfigView;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.Optionull;
import net.minecraft.core.Holder;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class FramedBlockItemTintProvider implements DynamicItemTintProvider
{
    public static final FramedBlockItemTintProvider INSTANCE_SINGLE = new FramedBlockItemTintProvider(false);
    public static final FramedBlockItemTintProvider INSTANCE_DOUBLE = new FramedBlockItemTintProvider(true);

    private final boolean doubleBlock;

    protected FramedBlockItemTintProvider(boolean doubleBlock)
    {
        this.doubleBlock = doubleBlock;
    }

    @Override
    public int getColor(ItemStack stack, CamoList camos, int tintIndex)
    {
        if (!ConfigView.Client.INSTANCE.shouldRenderItemModelsWithCamo()) return -1;

        if (tintIndex < -1 && doubleBlock)
        {
            tintIndex = ModelUtils.decodeSecondaryTintIndex(tintIndex);
            return ARGB.opaque(camos.getCamo(1).getTintColor(stack, tintIndex));
        }
        else if (tintIndex == BlockOverlay.OVERLAY_TINT_INDEX)
        {
            Holder<BlockOverlay> overlay = stack.get(Utils.DC_TYPE_BLOCK_OVERLAY);
            if (overlay != null)
            {
                Block tintSource = Optionull.map(overlay.value().tintSource(), Holder::value);
                if (tintSource != null)
                {
                    return ARGB.opaque(ClientUtils.getBlockColor(null, null, tintSource.defaultBlockState(), 0));
                }
            }
            return -1;
        }
        else if (tintIndex >= 0)
        {
            return ARGB.opaque(camos.getCamo(0).getTintColor(stack, tintIndex));
        }
        return -1;
    }



    public static FramedBlockItemTintProvider of(IFramedBlock block)
    {
        return block.getBlockType().isDoubleBlock() ? INSTANCE_DOUBLE : INSTANCE_SINGLE;
    }
}

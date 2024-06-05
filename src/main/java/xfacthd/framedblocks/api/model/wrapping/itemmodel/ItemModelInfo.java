package xfacthd.framedblocks.api.model.wrapping.itemmodel;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.util.CamoList;

public interface ItemModelInfo
{
    ItemModelInfo DEFAULT = new ItemModelInfo() {};

    /**
     * {@return whether the associated item model requires data even when no camos are present}
     */
    default boolean isDataRequired()
    {
        return false;
    }

    /**
     * {@return the {@link ModelData} containing the camos from the item data in the format required for the associated item's model}
     */
    default ModelData buildItemModelData(BlockState state, CamoList camos)
    {
        FramedBlockData fbData = new FramedBlockData(camos.getCamo(0).getContent(), false);
        return ModelData.builder().with(FramedBlockData.PROPERTY, fbData).build();
    }

    /**
     * Apply additional transforms to the item when it's rendered in the given display context
     */
    default void applyItemTransform(PoseStack poseStack, ItemDisplayContext ctx, boolean leftHand) { }
}

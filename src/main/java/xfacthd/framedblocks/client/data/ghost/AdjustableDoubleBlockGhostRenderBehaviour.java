package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.blockentity.doubled.slab.FramedAdjustableDoubleBlockEntity;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleBlockEntity;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleCopycatBlockEntity;
import xfacthd.framedblocks.common.data.component.AdjustableDoubleBlockData;

import java.util.Objects;

public final class AdjustableDoubleBlockGhostRenderBehaviour extends DoubleBlockGhostRenderBehaviour
{
    private final ModelProperty<Integer> offsetProperty;
    private final FramedAdjustableDoubleBlockEntity.OffsetPacker offsetPacker;

    private AdjustableDoubleBlockGhostRenderBehaviour(
            ModelProperty<Integer> offsetProperty,
            FramedAdjustableDoubleBlockEntity.OffsetPacker offsetPacker
    )
    {
        this.offsetProperty = offsetProperty;
        this.offsetPacker = offsetPacker;
    }

    @Override
    public ModelData appendModelData(ItemStack stack, @Nullable ItemStack proxiedStack, BlockPlaceContext ctx, BlockState renderState, int renderPass, ModelData data)
    {
        AdjustableDoubleBlockData blockData = stack.get(FBContent.DC_TYPE_ADJ_DOUBLE_BLOCK_DATA);
        int firstHeight = blockData != null ? blockData.firstHeight() : FramedAdjustableDoubleBlockEntity.CENTER_PART_HEIGHT;
        int offsetsLeft = offsetPacker.pack(renderState, firstHeight, false);
        int offsetsRight = offsetPacker.pack(renderState, firstHeight, true);

        ModelData dataLeft = Objects.requireNonNullElse(data.get(FramedDoubleBlockEntity.DATA_LEFT), ModelData.EMPTY)
                .derive()
                .with(offsetProperty, offsetsLeft)
                .build();
        ModelData dataRight = Objects.requireNonNullElse(data.get(FramedDoubleBlockEntity.DATA_RIGHT), ModelData.EMPTY)
                .derive()
                .with(offsetProperty, offsetsRight)
                .build();
        return data.derive().with(FramedDoubleBlockEntity.DATA_LEFT, dataLeft).with(FramedDoubleBlockEntity.DATA_RIGHT, dataRight).build();
    }



    public static AdjustableDoubleBlockGhostRenderBehaviour standard()
    {
        return new AdjustableDoubleBlockGhostRenderBehaviour(
                FramedCollapsibleBlockEntity.OFFSETS,
                FramedAdjustableDoubleBlockEntity::getPackedOffsetsStandard
        );
    }

    public static AdjustableDoubleBlockGhostRenderBehaviour copycat()
    {
        return new AdjustableDoubleBlockGhostRenderBehaviour(
                FramedCollapsibleCopycatBlockEntity.OFFSETS,
                FramedAdjustableDoubleBlockEntity::getPackedOffsetsCopycat
        );
    }
}

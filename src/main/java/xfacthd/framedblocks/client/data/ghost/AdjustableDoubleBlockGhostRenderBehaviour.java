package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.blockentity.doubled.FramedAdjustableDoubleBlockEntity;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleBlockEntity;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleCopycatBlockEntity;

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
    public ModelData appendModelData(ItemStack stack, @Nullable ItemStack proxiedStack, BlockPlaceContext ctx, BlockState renderState, boolean secondPass, ModelData data)
    {
        int offsetsLeft = offsetPacker.pack(renderState, FramedAdjustableDoubleBlockEntity.CENTER_PART_HEIGHT, false);
        int offsetsRight = offsetPacker.pack(renderState, FramedAdjustableDoubleBlockEntity.CENTER_PART_HEIGHT, true);

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
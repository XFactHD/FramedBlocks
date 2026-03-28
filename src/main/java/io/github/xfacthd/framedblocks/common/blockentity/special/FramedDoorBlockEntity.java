package io.github.xfacthd.framedblocks.common.blockentity.special;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jspecify.annotations.Nullable;

public class FramedDoorBlockEntity extends FramedBlockEntity {
    @Nullable
    private CamoContainer<?, ?> otherHalfCamoToDrop = null;

    public FramedDoorBlockEntity(BlockPos pos, BlockState state) {
        super(FBContent.BE_TYPE_FRAMED_DOOR.value(), pos, state);
    }

    @Override
    public boolean canTriviallyDropAllCamos() {
        return super.canTriviallyDropAllCamos() && getOtherCamo(isTopHalf(getBlockState())).canTriviallyConvertToItemStack();
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        // Both BEs must be aware of both camos due to how only dropping the BlockItem from one block is achieved
        BlockPos otherPos = isTopHalf(state) ? pos.below() : pos.above();
        if (level != null && level.getBlockEntity(otherPos) instanceof FramedDoorBlockEntity other) {
            otherHalfCamoToDrop = other.getCamo();
            other.otherHalfCamoToDrop = getCamo();
        }
    }

    @Override
    protected void collectCamoComponents(DataComponentMap.Builder builder) {
        boolean top = isTopHalf(getBlockState());

        CamoContainer<?, ?> otherCamo = getOtherCamo(top);

        CamoContainer<?, ?> camoOne = top ? otherCamo : getCamo();
        CamoContainer<?, ?> camoTwo = top ? getCamo() : otherCamo;
        builder.set(FBContent.DC_TYPE_CAMO_LIST, CamoList.of(camoOne, camoTwo));
    }

    @Override
    protected void applyCamoComponents(DataComponentGetter input) {
        CamoList camoList = input.getOrDefault(FBContent.DC_TYPE_CAMO_LIST, CamoList.EMPTY);
        setCamo(camoList.getCamo(isTopHalf(getBlockState()) ? 1 : 0), false);
    }

    private CamoContainer<?, ?> getOtherCamo(boolean topHalf) {
        if (otherHalfCamoToDrop != null) {
            return otherHalfCamoToDrop;
        }

        BlockPos otherPos = topHalf ? worldPosition.below() : worldPosition.above();
        if (level().getBlockEntity(otherPos) instanceof FramedDoorBlockEntity be) {
            return be.getCamo();
        }
        return EmptyCamoContainer.EMPTY;
    }

    public static boolean isTopHalf(BlockState state) {
        return state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER;
    }
}

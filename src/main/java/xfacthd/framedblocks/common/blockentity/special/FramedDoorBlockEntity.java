package xfacthd.framedblocks.common.blockentity.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import xfacthd.framedblocks.api.util.CamoList;
import xfacthd.framedblocks.common.FBContent;

public class FramedDoorBlockEntity extends FramedBlockEntity
{
    public FramedDoorBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_DOOR.value(), pos, state);
    }

    @Override
    protected void collectCamoComponents(DataComponentMap.Builder builder)
    {
        boolean top = getBlockState().getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER;

        CamoContainer<?, ?> otherCamo = EmptyCamoContainer.EMPTY;
        BlockPos otherPos = top ? worldPosition.below() : worldPosition.above();
        if (level().getBlockEntity(otherPos) instanceof FramedDoorBlockEntity be)
        {
            otherCamo = be.getCamo();
        }

        CamoContainer<?, ?> camoOne = top ? otherCamo : getCamo();
        CamoContainer<?, ?> camoTwo = top ? getCamo() : otherCamo;
        builder.set(FBContent.DC_TYPE_CAMO_LIST, CamoList.of(camoOne, camoTwo));
    }

    @Override
    protected void applyCamoComponents(DataComponentInput input)
    {
        CamoList camoList = input.getOrDefault(FBContent.DC_TYPE_CAMO_LIST, CamoList.EMPTY);
        boolean top = getBlockState().getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER;
        setCamo(camoList.getCamo(top ? 1 : 0), false);
    }
}

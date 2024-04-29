package xfacthd.framedblocks.common.data.blueprint;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import xfacthd.framedblocks.api.blueprint.BlueprintData;
import xfacthd.framedblocks.api.util.CamoList;
import xfacthd.framedblocks.common.block.door.FramedDoorBlock;
import xfacthd.framedblocks.common.data.blueprint.auxdata.DoorAuxBlueprintData;

public final class DoorCopyBehaviour implements BlueprintCopyBehaviour
{
    @Override
    public BlueprintData writeToBlueprint(Level level, BlockPos pos, BlockState state, FramedBlockEntity be)
    {
        boolean top = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER;
        BlockPos posTwo = top ? pos.below() : pos.above();

        BlueprintData dataOne = be.writeToBlueprint();
        BlueprintData dataTwo = BlueprintData.EMPTY;
        if (getSecondBlockEntity(level, posTwo) instanceof FramedBlockEntity beTwo)
        {
            dataTwo = beTwo.writeToBlueprint();
        }

        BlueprintData mainData = top ? dataTwo : dataOne;
        BlueprintData secData = top ? dataOne : dataTwo;
        return mainData.withAuxData(new DoorAuxBlueprintData(secData));
    }

    @Override
    public CamoList getCamos(BlueprintData data)
    {
        BlueprintData secData = getSecondData(data);
        return data.camos().concat(secData.camos());
    }

    @Override
    public int getGlowstoneCount(BlueprintData data)
    {
        int count = BlueprintCopyBehaviour.super.getGlowstoneCount(data);
        if (getSecondData(data).glowing())
        {
            count++;
        }
        return count;
    }

    @Override
    public int getIntangibleCount(BlueprintData data)
    {
        // Doors don't support intangibility
        return 0;
    }

    @Override
    public int getReinforcementCount(BlueprintData data)
    {
        int count = BlueprintCopyBehaviour.super.getReinforcementCount(data);
        if (getSecondData(data).reinforced())
        {
            count++;
        }
        return count;
    }

    @Override
    public void postProcessPaste(Level level, BlockPos pos, Player player, BlueprintData data, ItemStack dummyStack)
    {
        BlueprintData secData = getSecondData(data);
        if (secData.isEmpty()) return;

        BlockPos topPos = pos.above();
        if (getSecondBlockEntity(level, topPos) instanceof FramedBlockEntity be)
        {
            be.applyBlueprintData(secData);
        }
    }

    private static BlockEntity getSecondBlockEntity(Level level, BlockPos pos)
    {
        if (level.getBlockState(pos).getBlock() instanceof FramedDoorBlock)
        {
            return level.getBlockEntity(pos);
        }
        return null;
    }

    public static BlueprintData getSecondData(BlueprintData blueprintData)
    {
        return blueprintData.getAuxDataOrDefault(DoorAuxBlueprintData.EMPTY).data();
    }
}

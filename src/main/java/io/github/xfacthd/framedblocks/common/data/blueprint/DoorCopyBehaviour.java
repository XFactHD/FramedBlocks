package io.github.xfacthd.framedblocks.common.data.blueprint;

import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.door.FramedDoorBlock;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedDoorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class DoorCopyBehaviour implements BlueprintCopyBehaviour {
    @Override
    public BlueprintData writeToBlueprint(Level level, BlockPos pos, BlockState state, IFramedBlockEntity be) {
        boolean top = FramedDoorBlockEntity.isTopHalf(state);
        BlockPos posTwo = top ? pos.below() : pos.above();

        BlueprintData dataOne = be.writeToBlueprint();
        BlueprintData dataTwo = BlueprintData.EMPTY;
        if (getSecondBlockEntity(level, posTwo) instanceof IFramedBlockEntity beTwo) {
            dataTwo = beTwo.writeToBlueprint();
        }

        BlueprintData mainData = top ? dataTwo : dataOne;
        BlueprintData secData = top ? dataOne : dataTwo;
        return mainData.withCustomData(FBContent.DC_TYPE_BLUEPRINT_DATA, secData);
    }

    @Override
    public CamoList getCamos(BlueprintData data) {
        BlueprintData secData = getSecondData(data);
        return data.camos().concat(secData.camos());
    }

    @Override
    public int getGlowstoneCount(BlueprintData data) {
        int count = BlueprintCopyBehaviour.super.getGlowstoneCount(data);
        if (getSecondData(data).glowing()) {
            count++;
        }
        return count;
    }

    @Override
    public int getIntangibleCount(BlueprintData data) {
        // Doors don't support intangibility
        return 0;
    }

    @Override
    public int getReinforcementCount(BlueprintData data) {
        int count = BlueprintCopyBehaviour.super.getReinforcementCount(data);
        if (getSecondData(data).reinforced()) {
            count++;
        }
        return count;
    }

    @Override
    public int getEmissiveCount(BlueprintData data) {
        int count = BlueprintCopyBehaviour.super.getEmissiveCount(data);
        if (getSecondData(data).emissive()) {
            count++;
        }
        return count;
    }

    @Override
    public void postProcessPaste(Level level, BlockPos pos, @Nullable Player player, BlueprintData data, ItemStack dummyStack) {
        BlueprintData secData = getSecondData(data);
        if (secData.isEmpty()) {
            return;
        }

        BlockPos topPos = pos.above();
        if (getSecondBlockEntity(level, topPos) instanceof IFramedBlockEntity be) {
            be.applyBlueprintData(secData);
        }
    }

    private static @Nullable BlockEntity getSecondBlockEntity(Level level, BlockPos pos) {
        if (level.getBlockState(pos).getBlock() instanceof FramedDoorBlock) {
            return level.getBlockEntity(pos);
        }
        return null;
    }

    public static BlueprintData getSecondData(BlueprintData blueprintData) {
        return blueprintData.getCustomDataOrDefault(FBContent.DC_TYPE_BLUEPRINT_DATA, BlueprintData.EMPTY);
    }
}

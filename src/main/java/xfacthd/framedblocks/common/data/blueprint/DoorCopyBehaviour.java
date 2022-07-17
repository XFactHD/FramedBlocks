package xfacthd.framedblocks.common.data.blueprint;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;

import java.util.*;

public class DoorCopyBehaviour implements BlueprintCopyBehaviour
{
    String SECOND_CAMO_KEY = "camo_data_two";

    @Override
    public boolean writeToBlueprint(Level level, BlockPos pos, BlockState state, FramedBlockEntity be, CompoundTag blueprintData)
    {
        boolean top = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER;
        BlockPos posTwo = top ? pos.below() : pos.above();

        CompoundTag nbtOne = be.writeToBlueprint();
        CompoundTag nbtTwo = level.getBlockEntity(posTwo) instanceof FramedBlockEntity beTwo ? beTwo.writeToBlueprint() : new CompoundTag();

        blueprintData.put(MAIN_CAMO_KEY, top ? nbtTwo : nbtOne);
        blueprintData.put(SECOND_CAMO_KEY, top ? nbtOne : nbtTwo);

        return true;
    }

    @Override
    public Optional<Set<Pair<BlockState, ItemStack>>> getCamos(CompoundTag blueprintData)
    {
        Set<Pair<BlockState, ItemStack>> camos = new ObjectArraySet<>(2);
        camos.add(Pair.of(
                NbtUtils.readBlockState(blueprintData.getCompound(MAIN_CAMO_KEY).getCompound(CAMO_STATE_KEY)),
                ItemStack.of(blueprintData.getCompound(MAIN_CAMO_KEY).getCompound(CAMO_STACK_KEY))
        ));
        camos.add(Pair.of(
                NbtUtils.readBlockState(blueprintData.getCompound(SECOND_CAMO_KEY).getCompound(CAMO_STATE_KEY)),
                ItemStack.of(blueprintData.getCompound(SECOND_CAMO_KEY).getCompound(CAMO_STACK_KEY))
        ));
        return Optional.of(camos);
    }

    @Override
    public int getGlowstoneCount(CompoundTag blueprintData)
    {
        int count = BlueprintCopyBehaviour.super.getGlowstoneCount(blueprintData);
        if (blueprintData.getCompound(SECOND_CAMO_KEY).getBoolean(GLOWSTONE_KEY))
        {
            count++;
        }
        return count;
    }

    @Override
    public int getIntangibleCount(CompoundTag blueprintData)
    {
        // Doors don't support intangibility
        return 0;
    }

    @Override
    public void postProcessPaste(Level level, BlockPos pos, Player player, CompoundTag blueprintData, ItemStack dummyStack)
    {
        if (!blueprintData.contains(SECOND_CAMO_KEY, Tag.TAG_COMPOUND)) { return; }

        BlockPos topPos = pos.above();
        if (level.getBlockEntity(topPos) instanceof FramedBlockEntity)
        {
            //noinspection ConstantConditions
            dummyStack.getOrCreateTag().put("BlockEntityTag", blueprintData.get(SECOND_CAMO_KEY));
            BlockItem.updateCustomBlockEntityTag(level, player, topPos, dummyStack);
        }
    }
}

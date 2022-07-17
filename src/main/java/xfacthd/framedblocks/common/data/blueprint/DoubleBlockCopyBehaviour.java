package xfacthd.framedblocks.common.data.blueprint;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;

import java.util.*;

public class DoubleBlockCopyBehaviour implements BlueprintCopyBehaviour
{
    String CAMO_STATE_TWO_KEY = "camo_state_two";
    String CAMO_STACK_TWO_KEY = "camo_stack_two";

    @Override
    public Optional<Set<Pair<BlockState, ItemStack>>> getCamos(CompoundTag blueprintData)
    {
        Set<Pair<BlockState, ItemStack>> camos = new ObjectArraySet<>(2);
        camos.add(Pair.of(
                NbtUtils.readBlockState(blueprintData.getCompound(MAIN_CAMO_KEY).getCompound(CAMO_STATE_KEY)),
                ItemStack.of(blueprintData.getCompound(MAIN_CAMO_KEY).getCompound(CAMO_STACK_KEY))
        ));
        camos.add(Pair.of(
                NbtUtils.readBlockState(blueprintData.getCompound(MAIN_CAMO_KEY).getCompound(CAMO_STATE_TWO_KEY)),
                ItemStack.of(blueprintData.getCompound(MAIN_CAMO_KEY).getCompound(CAMO_STACK_TWO_KEY))
        ));
        return Optional.of(camos);
    }
}

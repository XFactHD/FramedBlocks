package io.github.xfacthd.framedblocks.common.compat.create.schematic.requirements;

import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.compat.create.FramedBlockEntityItemRequirement;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedFlowerPotBlockEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class FramedFlowerPotBlockEntityItemRequirement extends FramedBlockEntityItemRequirement {
    @Override
    protected void collectAdditionalRequirements(IFramedBlockEntity blockEntity, List<ItemRequirement.StackRequirement> requirements) {
        if (blockEntity instanceof FramedFlowerPotBlockEntity pot && pot.hasFlowerBlock()) {
            requirements.add(consume(new ItemStack(pot.getFlowerBlock())));
        }
    }
}

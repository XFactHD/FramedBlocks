package io.github.xfacthd.framedblocks.common.compat.create.schematic.requirements;

import com.simibubi.create.api.schematic.requirement.SchematicRequirementRegistries;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedDoorBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class FramedDoorBlockItemRequirement implements SchematicRequirementRegistries.BlockRequirement {
    public static final FramedDoorBlockItemRequirement INSTANCE = new FramedDoorBlockItemRequirement();

    private FramedDoorBlockItemRequirement() { }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, @Nullable BlockEntity blockEntity) {
        if (!FramedDoorBlockEntity.isTopHalf(state)) {
            return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, new ItemStack(state.getBlock()));
        }
        return ItemRequirement.NONE;
    }
}

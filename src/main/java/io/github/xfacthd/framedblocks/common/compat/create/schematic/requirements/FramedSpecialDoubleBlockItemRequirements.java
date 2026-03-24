package io.github.xfacthd.framedblocks.common.compat.create.schematic.requirements;

import com.simibubi.create.api.schematic.requirement.SchematicRequirementRegistries;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class FramedSpecialDoubleBlockItemRequirements implements SchematicRequirementRegistries.BlockRequirement
{
    private final Holder<Block> itemBlock;

    public FramedSpecialDoubleBlockItemRequirements(Holder<Block> itemBlock)
    {
        this.itemBlock = itemBlock;
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, @Nullable BlockEntity blockEntity)
    {
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, new ItemStack(itemBlock.value(), 2));
    }
}

package io.github.xfacthd.framedblocks.api.compat.create;

import com.simibubi.create.api.schematic.requirement.SchematicRequirementRegistries;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import io.github.xfacthd.framedblocks.api.block.blockentity.FrameModifier;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/// Base implementation of [SchematicRequirementRegistries.BlockEntityRequirement] informing Create's Schematicannon
/// of additional items required for copying camos applied to a framed block and any items required for a specific
/// framed block-
public class FramedBlockEntityItemRequirement implements SchematicRequirementRegistries.BlockEntityRequirement {
    public static final FramedBlockEntityItemRequirement INSTANCE = new FramedBlockEntityItemRequirement();
    private static final FrameModifier[] MODIFIERS = FrameModifier.values();

    protected FramedBlockEntityItemRequirement() { }

    @Override
    public final ItemRequirement getRequiredItems(BlockEntity blockEntity, BlockState state) {
        if (blockEntity instanceof IFramedBlockEntity fbe) {
            List<ItemRequirement.StackRequirement> requirements = new ArrayList<>();

            CamoContainer<?, ?> camoOne = fbe.getCamo();
            if (!camoOne.isEmpty() && camoOne.canTriviallyConvertToItemStack()) {
                requirements.add(consume(CamoContainerHelper.dropCamo(camoOne)));
            }
            if (fbe instanceof FramedDoubleBlockEntity fdbe) {
                CamoContainer<?, ?> camoTwo = fdbe.getCamoTwo();
                if (!camoTwo.isEmpty() && camoTwo.canTriviallyConvertToItemStack()) {
                    requirements.add(consume(CamoContainerHelper.dropCamo(camoTwo)));
                }
            }

            for (FrameModifier modifier : MODIFIERS) {
                if (modifier.isActive(fbe)) {
                    requirements.add(consume(modifier.getDefaultStack()));
                }
            }

            collectAdditionalRequirements(fbe, requirements);

            return new ItemRequirement(requirements);
        }
        return ItemRequirement.NONE;
    }

    /// Append additional non-camo item requirements.
    ///
    /// @param blockEntity  The BE being copied
    /// @param requirements The list of requirements to append to
    protected void collectAdditionalRequirements(IFramedBlockEntity blockEntity, List<ItemRequirement.StackRequirement> requirements) { }

    /// {@return a requirement consuming the given item}
    ///
    /// @param item The item to consume
    protected static ItemRequirement.StackRequirement consume(ItemLike item) {
        return consume(new ItemStack(item));
    }

    /// {@return a requirement consuming the given stack}
    ///
    /// @param stack The stack to consume
    protected static ItemRequirement.StackRequirement consume(ItemStack stack) {
        return new ItemRequirement.StackRequirement(stack, ItemRequirement.ItemUseType.CONSUME);
    }

    /// {@return a requirement consuming the given stack with strict NBT matching}
    ///
    /// @param stack The stack to consume
    protected static ItemRequirement.StackRequirement consumeStrict(ItemStack stack) {
        return new ItemRequirement.StrictNbtStackRequirement(stack, ItemRequirement.ItemUseType.CONSUME);
    }
}

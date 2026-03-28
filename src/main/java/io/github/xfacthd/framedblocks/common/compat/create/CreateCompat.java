package io.github.xfacthd.framedblocks.common.compat.create;

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.schematic.nbt.SafeNbtWriterRegistry;
import com.simibubi.create.api.schematic.requirement.SchematicRequirementRegistries;
import com.simibubi.create.api.schematic.state.SchematicStateFilterRegistry;
import com.simibubi.create.content.contraptions.behaviour.DoorMovingInteraction;
import com.simibubi.create.content.contraptions.behaviour.FenceGateMovingInteraction;
import com.simibubi.create.content.contraptions.behaviour.LeverMovingInteraction;
import com.simibubi.create.content.contraptions.behaviour.TrapdoorMovingInteraction;
import io.github.xfacthd.framedblocks.FramedBlocks;
import io.github.xfacthd.framedblocks.api.compat.create.FramedBlockEntityItemRequirement;
import io.github.xfacthd.framedblocks.api.compat.create.FramedBlockSafeNbtWriter;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedHopperBlockEntity;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedStorageBlockEntity;
import io.github.xfacthd.framedblocks.common.capability.fluid.TankFluidResourceHandler;
import io.github.xfacthd.framedblocks.common.compat.create.schematic.nbt.FramedChiseledBookshelfSafeNbtWriter;
import io.github.xfacthd.framedblocks.common.compat.create.schematic.nbt.FramedItemFrameSafeNbtWriter;
import io.github.xfacthd.framedblocks.common.compat.create.schematic.requirements.FramedDoorBlockItemRequirement;
import io.github.xfacthd.framedblocks.common.compat.create.schematic.requirements.FramedFlowerPotBlockEntityItemRequirement;
import io.github.xfacthd.framedblocks.common.compat.create.schematic.requirements.FramedItemFrameBlockEntityItemRequirement;
import io.github.xfacthd.framedblocks.common.compat.create.schematic.requirements.FramedSpecialDoubleBlockItemRequirements;
import io.github.xfacthd.framedblocks.common.compat.create.schematic.state.FramedChiseledBookshelfStateFilter;
import net.minecraft.core.Holder;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.fml.ModList;

import java.util.Map;

public final class CreateCompat {
    public static void commonSetup() {
        if (ModList.get().isLoaded("create")) {
            try {
                GuardedAccess.init();
            } catch (Throwable e) {
                FramedBlocks.LOGGER.warn("An error occured while initializing Create integration!", e);
            }
        }
    }

    private static final class GuardedAccess {
        private static final Map<Holder<BlockEntityType<?>>, SchematicRequirementRegistries.BlockEntityRequirement> SPECIAL_REQUIREMENT_BLOCK_ENTITIES = Map.of(
                FBContent.BE_TYPE_FRAMED_FLOWER_POT, new FramedFlowerPotBlockEntityItemRequirement(),
                FBContent.BE_TYPE_FRAMED_ITEM_FRAME, new FramedItemFrameBlockEntityItemRequirement()
        );
        private static final Map<Holder<BlockEntityType<?>>, SafeNbtWriterRegistry.SafeNbtWriter> SPECIAL_NBT_BLOCK_ENTITIES = Map.of(
                FBContent.BE_TYPE_FRAMED_CHEST, new FramedBlockSafeNbtWriter(FramedStorageBlockEntity.INVENTORY_NBT_KEY),
                FBContent.BE_TYPE_FRAMED_SECRET_STORAGE, new FramedBlockSafeNbtWriter(FramedStorageBlockEntity.INVENTORY_NBT_KEY),
                FBContent.BE_TYPE_FRAMED_TANK, new FramedBlockSafeNbtWriter(TankFluidResourceHandler.FLUID_NBT_KEY),
                FBContent.BE_TYPE_FRAMED_ITEM_FRAME, new FramedItemFrameSafeNbtWriter(),
                FBContent.BE_TYPE_FRAMED_CHISELED_BOOKSHELF, new FramedChiseledBookshelfSafeNbtWriter(),
                FBContent.BE_TYPE_FRAMED_HOPPER, new FramedBlockSafeNbtWriter(ContainerHelper.TAG_ITEMS, FramedHopperBlockEntity.COOLDOWN_NBT_KEY)
        );

        public static void init() {
            // The interaction behavior implementations are not exposed as API
            try {
                registerInteractionBehaviour(FBContent.BLOCK_FRAMED_LEVER, new LeverMovingInteraction());
                registerInteractionBehaviour(FBContent.BLOCK_FRAMED_DOOR, new DoorMovingInteraction());
                registerInteractionBehaviour(FBContent.BLOCK_FRAMED_TRAP_DOOR, new TrapdoorMovingInteraction());
                registerInteractionBehaviour(FBContent.BLOCK_FRAMED_FENCE_GATE, new FenceGateMovingInteraction());
            } catch (Throwable t) {
                FramedBlocks.LOGGER.warn("An error occured while registering MovingInteractions for Create contraptions!", t);
            }

            FramedBlockMovementChecks movementChecks = new FramedBlockMovementChecks();
            BlockMovementChecks.registerMovementNecessaryCheck(movementChecks);
            BlockMovementChecks.registerMovementAllowedCheck(movementChecks);
            BlockMovementChecks.registerBrittleCheck(movementChecks);
            BlockMovementChecks.registerAttachedCheck(movementChecks);
            BlockMovementChecks.registerNotSupportiveCheck(movementChecks);

            registerBlockItemRequirement(
                    FBContent.BLOCK_FRAMED_DOUBLE_SLAB,
                    new FramedSpecialDoubleBlockItemRequirements(FBContent.BLOCK_FRAMED_SLAB)
            );
            registerBlockItemRequirement(
                    FBContent.BLOCK_FRAMED_DOUBLE_PANEL,
                    new FramedSpecialDoubleBlockItemRequirements(FBContent.BLOCK_FRAMED_PANEL)
            );
            // TODO: camo of upper door block is not copied
            registerBlockItemRequirement(FBContent.BLOCK_FRAMED_DOOR, FramedDoorBlockItemRequirement.INSTANCE);
            registerBlockItemRequirement(FBContent.BLOCK_FRAMED_IRON_DOOR, FramedDoorBlockItemRequirement.INSTANCE);

            FBContent.getBlockEntities().forEach(blockEntity -> {
                registerBlockEntityItemRequirement(blockEntity, SPECIAL_REQUIREMENT_BLOCK_ENTITIES.getOrDefault(
                        blockEntity, FramedBlockEntityItemRequirement.INSTANCE
                ));
                registerSafeNbtWriter(blockEntity, SPECIAL_NBT_BLOCK_ENTITIES.getOrDefault(
                        blockEntity, FramedBlockSafeNbtWriter.INSTANCE
                ));
            });

            registerStateFilter(FBContent.BLOCK_FRAMED_CHISELED_BOOKSHELF, new FramedChiseledBookshelfStateFilter());
        }

        private static void registerInteractionBehaviour(Holder<Block> block, MovingInteractionBehaviour behaviour) {
            MovingInteractionBehaviour.REGISTRY.register(block.value(), behaviour);
        }

        private static void registerBlockItemRequirement(Holder<Block> type, SchematicRequirementRegistries.BlockRequirement itemRequirement) {
            SchematicRequirementRegistries.BLOCKS.register(type.value(), itemRequirement);
        }

        private static void registerBlockEntityItemRequirement(Holder<BlockEntityType<?>> type, SchematicRequirementRegistries.BlockEntityRequirement itemRequirement) {
            SchematicRequirementRegistries.BLOCK_ENTITIES.register(type.value(), itemRequirement);
        }

        private static void registerSafeNbtWriter(Holder<BlockEntityType<?>> type, SafeNbtWriterRegistry.SafeNbtWriter writer) {
            SafeNbtWriterRegistry.REGISTRY.register(type.value(), writer);
        }

        private static void registerStateFilter(Holder<Block> block, SchematicStateFilterRegistry.StateFilter filter) {
            SchematicStateFilterRegistry.REGISTRY.register(block.value(), filter);
        }
    }

    private CreateCompat() { }
}

package io.github.xfacthd.framedblocks.api.block;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.block.blockentity.FrameModifier;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.mixin.InvokerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

import java.util.Set;

/// Various helpers for creating framed blocks and handling in-world modifications to them.
public final class BlockUtils {
    /// Set of blockstate properties every framed block is required to have.
    public static final Set<Property<?>> REQUIRED_STATE_PROPERTIES = Set.of(
            FramedProperties.GLOWING,
            FramedProperties.PROPAGATES_SKYLIGHT
    );
    private static final FrameModifier[] MODIFIERS = FrameModifier.values();

    /// Adds the [Property]s which are required to be present on all blocks implementing [IFramedBlock]
    /// and properties that depend on the [IBlockType]'s configuration to the given [StateDefinition.Builder]
    ///
    /// @param block   The block to add the properties to
    /// @param builder The state definition builder for the block
    /// @apiNote This method must only be used by blocks which return a constant value from [IFramedBlock#getBlockType()]
    ///          or initialize the returned field before the super constructor.
    public static <T extends Block & IFramedBlock> void addStandardProperties(T block, StateDefinition.Builder<Block, BlockState> builder) {
        REQUIRED_STATE_PROPERTIES.forEach(builder::add);

        if (block.getBlockType().canOccludeWithSolidCamo()) {
            builder.add(FramedProperties.SOLID);
        }

        boolean hasWaterlogging = hasProperty(builder, BlockStateProperties.WATERLOGGED);
        boolean needsWaterlogging = block.getBlockType().supportsWaterLogging();
        if (needsWaterlogging && !hasWaterlogging) {
            builder.add(BlockStateProperties.WATERLOGGED);
        } else if (!needsWaterlogging && hasWaterlogging) {
            removeProperty(builder, BlockStateProperties.WATERLOGGED);
        }

        if (block instanceof ShapeLockableBlock) {
            builder.add(FramedProperties.STATE_LOCKED);
        }

        if (block instanceof SlopeToggleBlock) {
            builder.add(FramedProperties.ALT_SLOPE);
        }
    }

    /// Configures the default [BlockState] of the given [IFramedBlock].
    ///
    /// @param block The block to configure the default state for
    public static <T extends Block & IFramedBlock> void configureStandardProperties(T block) {
        BlockState state = block.defaultBlockState()
                .setValue(FramedProperties.GLOWING, false)
                .setValue(FramedProperties.PROPAGATES_SKYLIGHT, false);
        if (block.getBlockType().canOccludeWithSolidCamo()) {
            state = state.setValue(FramedProperties.SOLID, false);
        }
        if (block.getBlockType().supportsWaterLogging()) {
            state = state.setValue(BlockStateProperties.WATERLOGGED, false);
        }
        if (block instanceof ShapeLockableBlock) {
            state = state.setValue(FramedProperties.STATE_LOCKED, false);
        }
        if (block instanceof SlopeToggleBlock) {
            state = state.setValue(FramedProperties.ALT_SLOPE, false);
        }
        ((InvokerBlock) block).framedblocks$callRegisterDefaultState(state);
    }

    /// Copies all standard [Property]s between two [BlockState]s of the same [IFramedBlock].
    ///
    /// @param block            The block owning the two states
    /// @param from             The state to copy the properties from
    /// @param to               The state to apply the properties to
    /// @param copyWaterlogging Whether the [BlockStateProperties#WATERLOGGED] property should be copied
    public static <T extends Block & IFramedBlock> BlockState copyStandardProperties(T block, BlockState from, BlockState to, boolean copyWaterlogging) {
        Preconditions.checkArgument(from.getBlock() == block, "The provided states must be owned by the provided block");

        for (Property<?> property : REQUIRED_STATE_PROPERTIES) {
            to = Block.copyProperty(from, to, property);
        }
        if (block.getBlockType().canOccludeWithSolidCamo()) {
            to = Block.copyProperty(from, to, FramedProperties.SOLID);
        }
        if (copyWaterlogging && block.getBlockType().supportsWaterLogging()) {
            to = Block.copyProperty(from, to, BlockStateProperties.WATERLOGGED);
        }
        return to;
    }

    /// Check whether the given [StateDefinition.Builder] contains the given [Property].
    ///
    /// @param builder  The builder to check
    /// @param property The property to check for
    /// @return whether the builder contains the property
    public static boolean hasProperty(StateDefinition.Builder<Block, BlockState> builder, Property<?> property) {
        return builder.framedblocks$hasProperty(property);
    }

    /// Removes the given [Property] from the given [StateDefinition.Builder].
    ///
    /// @param builder The builder to remove the property from
    /// @param property The property to remove, if present
    public static void removeProperty(StateDefinition.Builder<Block, BlockState> builder, Property<?> property) {
        builder.framedblocks$removeProperty(property);
    }

    /// Create a [BlockEntityTicker] for
    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> @Nullable BlockEntityTicker<A> createBlockEntityTicker(
            BlockEntityType<A> type, BlockEntityType<E> actualType, BlockEntityTicker<? super E> ticker
    ) {
        return actualType == type ? (BlockEntityTicker<A>) ticker : null;
    }

    /// Rotate the [horizontal facing property][FramedProperties#FACING_HOR] of the given state by the given rotation.
    ///
    /// @param state    The state to rotate
    /// @param rotation The rotation to apply to the state
    /// @return the new state with its horizontal rotation adjusted
    public static BlockState rotate(BlockState state, Rotation rotation) {
        return rotate(state, FramedProperties.FACING_HOR, rotation);
    }

    /// Rotate the given direction property of the given state by the given rotation.
    ///
    /// The given direction property must support at least all four horizontal directions.
    ///
    /// @param state    The state to rotate
    /// @param property The direction property to rotate on the state
    /// @param rotation The rotation to apply to the state
    /// @return the new state with its horizontal rotation adjusted
    public static BlockState rotate(BlockState state, EnumProperty<Direction> property, Rotation rotation) {
        return state.setValue(property, rotation.rotate(state.getValue(property)));
    }

    /// Mirror the [horizontal facing property][FramedProperties#FACING_HOR] of the given state of a block
    /// that is oriented towards a face of the block space.
    ///
    /// @param state  The state to mirror
    /// @param mirror The mirroring to apply to the state
    /// @return the mirrored state
    public static BlockState mirrorFaceBlock(BlockState state, Mirror mirror) {
        return mirrorFaceBlock(state, FramedProperties.FACING_HOR, mirror);
    }

    /// Mirror the given direction property of the given state of a block
    /// that is oriented towards a face of the block space.
    ///
    /// The given direction property must support at least all four horizontal directions.
    ///
    /// @param state    The state to mirror
    /// @param property The direction property to mirror on the state
    /// @param mirror   The mirroring to apply to the state
    /// @return the mirrored state
    public static BlockState mirrorFaceBlock(BlockState state, EnumProperty<Direction> property, Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return state;
        }

        Direction dir = state.getValue(property);
        //Y directions are inherently ignored
        if ((mirror == Mirror.FRONT_BACK && DirUtils.isX(dir)) || (mirror == Mirror.LEFT_RIGHT && DirUtils.isZ(dir))) {
            return state.setValue(property, dir.getOpposite());
        }
        return state;
    }

    /// Mirrors the [horizontal facing property][FramedProperties#FACING_HOR] of the given state of a block
    /// that is oriented into a corner of the block space.
    ///
    /// @param state  The state to mirror
    /// @param mirror The mirroring to apply to the state
    /// @return the mirrored state
    public static BlockState mirrorCornerBlock(BlockState state, Mirror mirror) {
        return mirrorCornerBlock(state, FramedProperties.FACING_HOR, mirror);
    }

    /// Mirrors the given direction property of the given state of a block
    /// that is oriented into a corner of the block space.
    ///
    /// The given direction property must support at least all four horizontal directions.
    ///
    /// @param state    The state to mirror
    /// @param property The direction property to mirror on the state
    /// @param mirror   The mirroring to apply to the state
    /// @return the mirrored state
    public static BlockState mirrorCornerBlock(BlockState state, EnumProperty<Direction> property, Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return state;
        }

        Direction dir = state.getValue(property);
        if (DirUtils.isY(dir)) {
            return state;
        }

        if (mirror == Mirror.LEFT_RIGHT) {
            dir = switch (dir) {
                case NORTH -> Direction.WEST;
                case EAST -> Direction.SOUTH;
                case SOUTH -> Direction.EAST;
                case WEST -> Direction.NORTH;
                default -> throw new IllegalArgumentException("Unreachable!");
            };
        } else {
            dir = switch (dir) {
                case NORTH -> Direction.EAST;
                case EAST -> Direction.NORTH;
                case SOUTH -> Direction.WEST;
                case WEST -> Direction.SOUTH;
                default -> throw new IllegalArgumentException("Unreachable!");
            };
        }
        return state.setValue(property, dir);
    }

    /// Wrap an action which modifies a block in the world such that its BE is replaced in a copy operation that
    /// copies the data from the old BE to the new BE.
    ///
    /// Primarily intended for block placements which replace an existing framed block (i.e. combining two framed
    /// slabs into a double slab in-world) instead of placing a new one in an empty block space.
    ///
    /// @param level       The level the block is in
    /// @param pos         The position of the block
    /// @param player      The player triggering the action
    /// @param stack       The item used in the interaction triggering the action
    /// @param mirrorCamos Whether the camos should be switched if the target BE is a two-camo block
    /// @param consumeItem Whether the held item should be consumed
    /// @param action      The action to perform
    public static void wrapInStateCopy(
            LevelAccessor level,
            BlockPos pos,
            Player player,
            ItemStack stack,
            boolean mirrorCamos,
            boolean consumeItem,
            Runnable action
    ) {
        CamoContainer<?, ?> camoOne = EmptyCamoContainer.EMPTY;
        CamoContainer<?, ?> camoTwo = EmptyCamoContainer.EMPTY;
        Holder<BlockOverlay> overlay = null;
        boolean[] modifiers = new boolean[MODIFIERS.length];

        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            camoOne = be.getCamo();
            if (be instanceof FramedDoubleBlockEntity dbe) {
                camoTwo = dbe.getCamoTwo();
            }
            overlay = be.getOverlay();
            for (FrameModifier modifier : MODIFIERS) {
                modifiers[modifier.ordinal()] = modifier.isActive(be);
            }
        }

        action.run();

        if (consumeItem && !player.isCreative()) {
            stack.shrink(1);
            player.getInventory().setChanged();
        }

        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity be) {
            be.setCamo(camoOne, mirrorCamos);
            if (be instanceof FramedDoubleBlockEntity dbe) {
                dbe.setCamo(camoTwo, !mirrorCamos);
            } else if (mirrorCamos) {
                throw new IllegalArgumentException("Cannot mirror camos on single-camo target BEs: " + be.getType());
            }
            be.setOverlay(overlay);
            for (FrameModifier modifier : MODIFIERS) {
                modifier.setActive(be, modifiers[modifier.ordinal()]);
            }
        }
    }

    private BlockUtils() { }
}

package io.github.xfacthd.framedblocks.api.block;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.block.blockentity.FrameModifier;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.mixin.InvokerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public final class BlockUtils
{
    public static final Set<Property<?>> REQUIRED_STATE_PROPERTIES = Set.of(
            FramedProperties.GLOWING,
            FramedProperties.PROPAGATES_SKYLIGHT
    );
    private static final FrameModifier[] MODIFIERS = FrameModifier.values();

    /**
     * Adds the {@link Property}s which are required to be present on all blocks implementing {@link IFramedBlock}
     * to the given {@link StateDefinition.Builder}
     */
    public static void addRequiredProperties(StateDefinition.Builder<Block, BlockState> builder)
    {
        REQUIRED_STATE_PROPERTIES.forEach(builder::add);
    }

    /**
     * Adds the {@link Property}s which are required to be present on all blocks implementing {@link IFramedBlock}
     * and properties that depend on the {@link IBlockType}'s configuration to the given {@link StateDefinition.Builder}
     *
     * @apiNote This method must only be used by blocks which return a constant value from {@link IFramedBlock#getBlockType()}
     */
    public static <T extends Block & IFramedBlock> void addStandardProperties(T block, StateDefinition.Builder<Block, BlockState> builder)
    {
        addRequiredProperties(builder);

        if (block.getBlockType().canOccludeWithSolidCamo())
        {
            builder.add(FramedProperties.SOLID);
        }

        boolean hasWaterlogging = hasProperty(builder, BlockStateProperties.WATERLOGGED);
        boolean needsWaterlogging = block.getBlockType().supportsWaterLogging();
        if (needsWaterlogging && !hasWaterlogging)
        {
            builder.add(BlockStateProperties.WATERLOGGED);
        }
        else if (!needsWaterlogging && hasWaterlogging)
        {
            removeProperty(builder, BlockStateProperties.WATERLOGGED);
        }

        if (block.getBlockType().canLockState())
        {
            builder.add(FramedProperties.STATE_LOCKED);
        }
    }

    /**
     * Configures the default {@link BlockState} of the given {@link IFramedBlock}
     *
     * @param block The block to configure the default state for
     */
    public static <T extends Block & IFramedBlock> void configureStandardProperties(T block)
    {
        BlockState state = block.defaultBlockState()
                .setValue(FramedProperties.GLOWING, false)
                .setValue(FramedProperties.PROPAGATES_SKYLIGHT, false);
        if (block.getBlockType().canOccludeWithSolidCamo())
        {
            state = state.setValue(FramedProperties.SOLID, false);
        }
        if (block.getBlockType().supportsWaterLogging())
        {
            state = state.setValue(BlockStateProperties.WATERLOGGED, false);
        }
        if (block.getBlockType().canLockState())
        {
            state = state.setValue(FramedProperties.STATE_LOCKED, false);
        }
        ((InvokerBlock) block).framedblocks$callRegisterDefaultState(state);
    }

    /**
     * Copies the {@link Property}s which are required to be present on all blocks implementing {@link IFramedBlock}
     * between two {@link BlockState}s of the same {@link Block}
     */
    public static BlockState copyRequiredProperties(BlockState from, BlockState to)
    {
        Preconditions.checkArgument(from.getBlock() == to.getBlock(), "Both states must be from the same block");

        for (Property<?> property : REQUIRED_STATE_PROPERTIES)
        {
            to = Block.copyProperty(from, to, property);
        }
        return to;
    }

    /**
     * Copies all standard {@link Property}s between two {@link BlockState}s of the same {@link IFramedBlock}
     *
     * @param block The block owning the two states
     * @param from The state to copy the properties from
     * @param to The state to apply the properties to
     * @param copyWaterlogging Whether the {@link BlockStateProperties#WATERLOGGED} property should be copied
     */
    public static <T extends Block & IFramedBlock> BlockState copyStandardProperties(T block, BlockState from, BlockState to, boolean copyWaterlogging)
    {
        Preconditions.checkArgument(from.getBlock() == block, "The provided states must be owned by the provided block");

        to = copyRequiredProperties(from, to);
        if (block.getBlockType().canOccludeWithSolidCamo())
        {
            to = Block.copyProperty(from, to, FramedProperties.SOLID);
        }
        if (copyWaterlogging && block.getBlockType().supportsWaterLogging())
        {
            to = Block.copyProperty(from, to, BlockStateProperties.WATERLOGGED);
        }
        return to;
    }

    /**
     * Check whether the given {@link StateDefinition.Builder} contains the given {@link Property}
     *
     * @param builder The builder to check
     * @param property The property to check for
     * @return whether the builder contains the property
     */
    public static boolean hasProperty(StateDefinition.Builder<Block, BlockState> builder, Property<?> property)
    {
        return builder.framedblocks$hasProperty(property);
    }

    /**
     * Removes the given {@link Property} from the given {@link StateDefinition.Builder}
     *
     * @param builder The builder to remove the property from
     * @param property The property to remove, if present
     */
    public static void removeProperty(StateDefinition.Builder<Block, BlockState> builder, Property<?> property)
    {
        builder.framedblocks$removeProperty(property);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createBlockEntityTicker(
            BlockEntityType<A> type, BlockEntityType<E> actualType, BlockEntityTicker<? super E> ticker
    )
    {
        return actualType == type ? (BlockEntityTicker<A>) ticker : null;
    }

    public static BlockState rotate(BlockState state, Rotation rotation)
    {
        return rotate(state, FramedProperties.FACING_HOR, rotation);
    }

    public static BlockState rotate(BlockState state, EnumProperty<Direction> property, Rotation rotation)
    {
        return state.setValue(property, rotation.rotate(state.getValue(property)));
    }

    /**
     * Mirrors a block that is oriented towards a face of the block space.
     * @param state The {@link BlockState} to mirror
     * @param mirror The {@link Mirror} to apply to the state
     * @apiNote The given state must have the {@link FramedProperties#FACING_HOR} property
     */
    public static BlockState mirrorFaceBlock(BlockState state, Mirror mirror)
    {
        return mirrorFaceBlock(state, FramedProperties.FACING_HOR, mirror);
    }

    /**
     * Mirrors a block that is oriented towards a face of the block space
     * @param state The {@link BlockState} to mirror
     * @param property The {@link EnumProperty < Direction >} that should be mirrored on the given state
     * @param mirror The {@link Mirror} to apply to the state
     * @apiNote The given property must support at least all four cardinal directions
     */
    public static BlockState mirrorFaceBlock(BlockState state, EnumProperty<Direction> property, Mirror mirror)
    {
        if (mirror == Mirror.NONE)
        {
            return state;
        }

        Direction dir = state.getValue(property);
        //Y directions are inherently ignored
        if ((mirror == Mirror.FRONT_BACK && Utils.isX(dir)) || (mirror == Mirror.LEFT_RIGHT && Utils.isZ(dir)))
        {
            return state.setValue(property, dir.getOpposite());
        }
        return state;
    }

    /**
     * Mirrors a block that is oriented into a corner of the block space.
     * @param state The {@link BlockState} to mirror
     * @param mirror The {@link Mirror} to apply to the state
     * @apiNote The given state must have the {@link FramedProperties#FACING_HOR} property
     */
    public static BlockState mirrorCornerBlock(BlockState state, Mirror mirror)
    {
        return mirrorCornerBlock(state, FramedProperties.FACING_HOR, mirror);
    }

    /**
     * Mirrors a block that is oriented into a corner of the block space
     * @param state The {@link BlockState} to mirror
     * @param property The {@link EnumProperty<Direction>} that should be mirrored on the given state
     * @param mirror The {@link Mirror} to apply to the state
     * @apiNote The given property must support at least all four cardinal directions
     */
    public static BlockState mirrorCornerBlock(BlockState state, EnumProperty<Direction> property, Mirror mirror)
    {
        if (mirror == Mirror.NONE)
        {
            return state;
        }

        Direction dir = state.getValue(property);
        if (Utils.isY(dir))
        {
            return state;
        }

        if (mirror == Mirror.LEFT_RIGHT)
        {
            dir = switch (dir)
            {
                case NORTH -> Direction.WEST;
                case EAST -> Direction.SOUTH;
                case SOUTH -> Direction.EAST;
                case WEST -> Direction.NORTH;
                default -> throw new IllegalArgumentException("Unreachable!");
            };
        }
        else
        {
            dir = switch (dir)
            {
                case NORTH -> Direction.EAST;
                case EAST -> Direction.NORTH;
                case SOUTH -> Direction.WEST;
                case WEST -> Direction.SOUTH;
                default -> throw new IllegalArgumentException("Unreachable!");
            };
        }
        return state.setValue(property, dir);
    }

    public static void wrapInStateCopy(
            LevelAccessor level,
            BlockPos pos,
            Player player,
            ItemStack stack,
            boolean writeToCamoTwo,
            boolean consumeItem,
            Runnable action
    )
    {
        CamoContainer<?, ?> camo = EmptyCamoContainer.EMPTY;
        boolean[] modifiers = new boolean[MODIFIERS.length];

        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            camo = be.getCamo();
            for (FrameModifier modifier : MODIFIERS)
            {
                modifiers[modifier.ordinal()] = modifier.isActive(be);
            }
        }

        action.run();

        if (consumeItem && !player.isCreative())
        {
            stack.shrink(1);
            player.getInventory().setChanged();
        }

        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            be.setCamo(camo, writeToCamoTwo);
            for (FrameModifier modifier : MODIFIERS)
            {
                modifier.setActive(be, modifiers[modifier.ordinal()]);
            }
        }
    }

    private BlockUtils() { }
}

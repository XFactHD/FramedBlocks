package io.github.xfacthd.framedblocks.api.compat.jade;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.UnaryOperator;

/// Describes how a framed block should be displayed in the Jade tooltip.
///
/// @param owner         The block owning this display config
/// @param targetClass   The class under which this block should be registered to the Jade `BlockComponentProvider` to prevent
///                      duplicate provider attachment for blocks which extend a class that is instantiated for other blocks
/// @param renderAsBlock Whether this block should be rendered as a block or as the item
/// @param displayState  Function for computing the state to be drawn from the state being looked at
/// @param scale   The scale value at which this block should be drawn
public record JadeDisplayConfig(IFramedBlock owner, Class<? extends Block> targetClass, boolean renderAsBlock, UnaryOperator<BlockState> displayState, float scale, Vec3 offset) {
    @ApiStatus.Internal
    public JadeDisplayConfig {}

    /// {@return a copy of this display config with the target class adjusted to the class of the given block}
    ///
    /// @param block The block whose class to use as the target
    public <B extends Block & IFramedBlock> JadeDisplayConfig withTargetClass(B block) {
        return withTargetClass(block.getClass());
    }

    /// {@return a copy of this display config with the target class adjusted to the given one}
    ///
    /// @param targetClass The target class to use
    public JadeDisplayConfig withTargetClass(Class<? extends Block> targetClass) {
        return new JadeDisplayConfig(owner, targetClass, renderAsBlock, displayState, scale, offset);
    }

    /// {@return a copy of this display config with the scale adjusted to the given one}
    ///
    /// @param scale The render scale to use
    public JadeDisplayConfig withScale(float scale) {
        return new JadeDisplayConfig(owner, targetClass, renderAsBlock, displayState, scale, offset);
    }

    /// {@return a copy of this display config with the offset adjusted to the given one}
    ///
    /// @param offX The X offset to use
    /// @param offY The Y offset to use
    /// @param offZ The Z offset to use
    public JadeDisplayConfig withOffset(float offX, float offY, float offZ) {
        return new JadeDisplayConfig(owner, targetClass, renderAsBlock, displayState, scale, new Vec3(offX, offY, offZ));
    }

    /// {@return the display state based on the given in-world state}
    ///
    /// @param state The state of the block being looked at
    /// @return a new display config for the given block
    public BlockState getDisplayState(BlockState state) {
        return displayState.apply(state);
    }

    /// Create a display config displaying the given block as its default state.
    ///
    /// @param block The block to display
    /// @return a new display config for the given block
    public static <B extends Block & IFramedBlock> JadeDisplayConfig defaultState(B block) {
        return fixedState(block, block.defaultBlockState());
    }

    /// Create a display config displaying the given block as the given state.
    ///
    /// @param block The block to display
    /// @param state The state to display the block as
    /// @return a new display config for the given block
    public static <B extends Block & IFramedBlock> JadeDisplayConfig fixedState(B block, BlockState state) {
        return dynamicState(block, _ -> state);
    }

    /// Create a display config displaying the given block as the exact state placed in the world.
    ///
    /// @param block The block to display
    /// @return a new display config for the given block
    public static <B extends Block & IFramedBlock> JadeDisplayConfig worldState(B block) {
        return dynamicState(block, UnaryOperator.identity());
    }

    /// Create a display config displaying the given block as the state determined by the given operator.
    ///
    /// @param block The block to display
    /// @param state The function for determining the state to display the block as
    /// @return a new display config for the given block
    public static <B extends Block & IFramedBlock> JadeDisplayConfig dynamicState(B block, UnaryOperator<BlockState> state) {
        return new JadeDisplayConfig(block, block.getClass(), true, state, 1F, Vec3.ZERO);
    }

    /// Create a display config displaying the given block as its item.
    ///
    /// @param block The block to display
    /// @return a new display config for the given block
    public static <B extends Block & IFramedBlock> JadeDisplayConfig asItem(B block) {
        return new JadeDisplayConfig(block, block.getClass(), false, _ -> block.defaultBlockState(), 1F, Vec3.ZERO);
    }
}

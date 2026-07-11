package io.github.xfacthd.framedblocks.api.model.template;

import com.mojang.math.Quadrant;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.function.Function;
import java.util.function.UnaryOperator;

/// Provides helpers for creating template specs.
public final class TemplateUtils {
    /// Create a "unit" template spec with a single source file for the given block.
    ///
    /// @param block The block to create the spec for
    /// @param type  The type of the source file
    /// @param file  The source file to use for the block
    /// @return a new template spec
    public static GeometryTemplateSpec createUnitSpec(Holder<Block> block, SourceType type, Identifier file) {
        return GeometryTemplateSpec.create(block, (_, builder) -> builder.addSourceFile(type, file));
    }

    /// Create a template spec for the given block residing in the top or bottom half of the block.
    /// The given source file must be the bottom half and is mirrored for the top half.
    ///
    /// @param block The block to create the spec for
    /// @param type  The type of the source file
    /// @param file  The source file to use for the block
    /// @return a new template spec
    public static GeometryTemplateSpec createTopBottomSpec(Holder<Block> block, SourceType type, Identifier file) {
        return GeometryTemplateSpec.create(block, (state, builder) -> {
            builder.addSourceFile(type, file);
            if (state.getValue(FramedProperties.TOP)) {
                builder.transform(xform -> xform.mirrorY(true));
            }
        });
    }

    /// Create a template spec for the given block residing in the top or bottom half of the block
    /// with a horizontal orientation.
    /// The given source file must be the north-facing bottom half and is mirrored for the top half.
    ///
    /// @param block The block to create the spec for
    /// @param type  The type of the source file
    /// @param file  The source file to use for the block
    /// @return a new template spec
    public static GeometryTemplateSpec createTopBottomHorFacingSpec(Holder<Block> block, SourceType type, Identifier file) {
        return GeometryTemplateSpec.create(block, (state, builder) ->
                builder.addSourceFile(type, file).transform(xform ->
                        xform.rotationY(getHorizontalQuadrant(state, false))
                                .mirrorY(state.getValue(FramedProperties.TOP))
                )
        );
    }

    /// Create a template spec for the given block oriented in any of the six directions.
    /// The given source file must be the north-facing orientation.
    ///
    /// @param block The block to create the spec for
    /// @param type  The type of the source file
    /// @param file  The source file to use for the block
    /// @return a new template spec
    public static GeometryTemplateSpec createFacingSpec(Holder<Block> block, SourceType type, Identifier file) {
        return createFacingSpec(block, BlockStateProperties.FACING, type, file);
    }

    /// Create a template spec for the given block oriented in any direction allowed by the given blockstate property.
    /// The given source file must be the north-facing orientation.
    ///
    /// @param block    The block to create the spec for
    /// @param property The blockstate property determining the block's orientation
    /// @param type     The type of the source file
    /// @param file     The source file to use for the block
    /// @return a new template spec
    public static GeometryTemplateSpec createFacingSpec(Holder<Block> block, EnumProperty<Direction> property, SourceType type, Identifier file) {
        return GeometryTemplateSpec.create(block, (state, builder) ->
                builder.addSourceFile(type, file).transform(xform ->
                        xform.rotationX(getVerticalQuadrant(state, property, false))
                                .rotationY(getHorizontalQuadrant(state, property, false))
                )
        );
    }

    /// Create a template spec for the given block oriented along any of the three axes.
    /// The given source file must be the Y-axis orientation.
    ///
    /// @param block The block to create the spec for
    /// @param type  The type of the source file
    /// @param file  The source file to use for the block
    /// @return a new template spec
    public static GeometryTemplateSpec createAxisSpec(Holder<Block> block, SourceType type, Identifier file) {
        return createAxisSpec(block, type, _ -> file);
    }

    /// Create a template spec for the given block oriented along any of the three axes.
    /// The source file provided by the given function must be the Y-axis orientation.
    ///
    /// @param block      The block to create the spec for
    /// @param type       The type of the source file
    /// @param fileGetter A function providing the source file to use for the given state of the block
    /// @return a new template spec
    public static GeometryTemplateSpec createAxisSpec(Holder<Block> block, SourceType type, Function<BlockState, Identifier> fileGetter) {
        return GeometryTemplateSpec.create(block, (state, builder) ->
                builder.addSourceFile(type, fileGetter.apply(state)).transform(applyAxisRotation(state))
        );
    }

    /// {@return a function applying the horizontal orientation of the given state to a transform builder}
    ///
    /// @param state  The state of the block
    /// @param invert Whether the orientation should be inverted
    public static UnaryOperator<TemplateTransformBuilder> applyHorizontalRotation(BlockState state, boolean invert) {
        return applyHorizontalRotation(state, FramedProperties.FACING_HOR, invert);
    }

    /// {@return a function applying the horizontal orientation of the given state to a transform builder}
    ///
    /// @param state    The state of the block
    /// @param property The blockstate property determining the block's orientation
    /// @param invert   Whether the orientation should be inverted
    public static UnaryOperator<TemplateTransformBuilder> applyHorizontalRotation(BlockState state, EnumProperty<Direction> property, boolean invert) {
        Direction dir = state.getValue(property);
        return applyHorizontalRotation(invert ? dir.getOpposite() : dir);
    }

    /// {@return a function applying the given horizontal orientation to a transform builder}
    ///
    /// @param dir The orientation of the block
    public static UnaryOperator<TemplateTransformBuilder> applyHorizontalRotation(Direction dir) {
        return xform -> xform.rotationY(getHorizontalQuadrant(dir));
    }

    /// {@return a function applying the axis orientation of the given state to a transform builder}
    ///
    /// @param state The state of the block
    public static UnaryOperator<TemplateTransformBuilder> applyAxisRotation(BlockState state) {
        return xform -> switch (state.getValue(BlockStateProperties.AXIS)) {
            case X -> xform.rotationZ(Quadrant.R90);
            case Z -> xform.rotationX(Quadrant.R90);
            default -> xform;
        };
    }

    /// {@return the quadrant representing the given state's horizontal orientation}
    ///
    /// @param state  The state of the block
    /// @param invert Whether the orientation should be inverted
    public static Quadrant getHorizontalQuadrant(BlockState state, boolean invert) {
        return getHorizontalQuadrant(state, FramedProperties.FACING_HOR, invert);
    }

    /// {@return the quadrant representing the given state's horizontal orientation}
    ///
    /// @param state    The state of the block
    /// @param property The blockstate property determining the block's orientation
    /// @param invert   Whether the orientation should be inverted
    public static Quadrant getHorizontalQuadrant(BlockState state, EnumProperty<Direction> property, boolean invert) {
        Direction dir = state.getValue(property);
        return getHorizontalQuadrant(invert ? dir.getOpposite() : dir);
    }

    /// {@return the quadrant representing the given horizontal orientation}
    ///
    /// @param dir The orientation of the block
    public static Quadrant getHorizontalQuadrant(Direction dir) {
        return switch (dir) {
            case NORTH, DOWN, UP -> Quadrant.R0;
            case EAST -> Quadrant.R90;
            case SOUTH -> Quadrant.R180;
            case WEST -> Quadrant.R270;
        };
    }

    /// {@return the quadrant around the X axis representing the state's vertical orienation}
    ///
    /// @param state  The state of the block
    /// @param invert Whether the orientation should be inverted
    private static Quadrant getVerticalQuadrant(BlockState state, boolean invert) {
        return getVerticalQuadrant(state, BlockStateProperties.FACING, invert);
    }

    /// {@return the quadrant around the X axis representing the state's vertical orienation}
    ///
    /// @param state    The state of the block
    /// @param property The blockstate property determining the block's orientation
    /// @param invert   Whether the orientation should be inverted
    public static Quadrant getVerticalQuadrant(BlockState state, EnumProperty<Direction> property, boolean invert) {
        Direction facing = state.getValue(property);
        if (invert) {
            facing = facing.getOpposite();
        }
        return switch (facing) {
            case NORTH, EAST, SOUTH, WEST -> Quadrant.R0;
            case DOWN -> Quadrant.R90;
            case UP -> Quadrant.R270;
        };
    }

    private TemplateUtils() { }
}

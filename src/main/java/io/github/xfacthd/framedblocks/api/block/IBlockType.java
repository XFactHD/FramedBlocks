package io.github.xfacthd.framedblocks.api.block;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.render.FramedClientBlockExtensions;
import io.github.xfacthd.framedblocks.api.block.render.NullCullPredicate;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConTexMode;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.ApiStatus;

public interface IBlockType {
    boolean canOccludeWithSolidCamo();

    boolean hasSpecialOutline();

    @ApiStatus.OverrideOnly
    FullFacePredicate getFullFacePredicate();

    SideSkipPredicate getSideSkipPredicate();

    @ApiStatus.OverrideOnly
    ConnectionPredicate getConnectionPredicate();

    @ApiStatus.OverrideOnly
    BlockOverlayPredicate getBlockOverlayPredicate();

    /// Returns the [NullCullPredicate] used to decide whether "uncullable" faces of a double block's part may
    /// be culled if the other part's camo is solid.
    ///
    /// Only relevant for blocks returning `true` from [#isDoubleBlock()].
    @ApiStatus.OverrideOnly
    NullCullPredicate getNullCullPredicate();

    ShapeGenerator getShapeGenerator();

    boolean hasSpecialBlockEntity();

    boolean hasBlockItem();

    boolean supportsWaterLogging();

    boolean supportsConnectedTextures();

    /**
     * {@return the minimum {@link ConTexMode } required for this block to react to texture connections}
     */
    ConTexMode getMinimumConTexMode();

    /**
     * @implNote If this method returns true, then the associated block register an instance of {@link FramedClientBlockExtensions}
     * in {@link RegisterClientExtensionsEvent} to avoid crashing when the block is hit while it can be passed through
     */
    boolean allowMakingIntangible();

    /**
     * @return true if this type represents a block that combines two models into one and allows those to have separate
     * camos applied.
     *
     * @apiNote if a block's type returns true from this method, its {@link Block} must implement {@link IFramedDoubleBlock}
     * and its {@link BlockEntity} must extend {@link FramedDoubleBlockEntity}. The {@link BlockStateModel} is not required
     * to extend or implement any specific class
     */
    default boolean isDoubleBlock() {
        return false;
    }

    /**
     * {@return whether this block can consume two camo items in the camo application recipe}
     */
    default boolean consumesTwoCamosInCamoApplicationRecipe() {
        return isDoubleBlock();
    }

    boolean supportsBlockOverlays();

    String getName();

    int compareTo(IBlockType other);
}

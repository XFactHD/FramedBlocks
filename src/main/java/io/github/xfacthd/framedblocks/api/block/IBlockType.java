package io.github.xfacthd.framedblocks.api.block;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.block.render.FramedClientBlockExtensions;
import io.github.xfacthd.framedblocks.api.block.render.NullCullPredicate;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConTexMode;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.api.predicate.overlay.BlockOverlayPredicate;
import io.github.xfacthd.framedblocks.api.render.outline.OutlineRenderer;
import io.github.xfacthd.framedblocks.api.shapes.ShapeGenerator;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.ApiStatus;

/// Describes a type of framed block.
///
/// This interface must be implemented on an enum with one implementation per namespace.
public interface IBlockType {
    /// {@return whether this block can occlude when an {@linkplain BlockState#isSolidRender() opaque} camo is applied to it}
    boolean canOccludeWithSolidCamo();

    /// {@return whether this block uses an {@link OutlineRenderer} to render a non-vanilla block selection outline}
    boolean hasSpecialOutline();

    /// {@return the {@link FullFacePredicate} determining which faces of a given state of this block are considered full
    /// (cover the entire surface at the outer perimeter of the block volume)}
    @ApiStatus.OverrideOnly
    FullFacePredicate getFullFacePredicate();

    /// {@return the {@link SideSkipPredicate} to use for computing occlusion of this block against adjacent framed blocks}
    SideSkipPredicate getSideSkipPredicate();

    /// {@return the {@link ConnectionPredicate} determining which edges of faces of a given state of this block allow connected textures}
    @ApiStatus.OverrideOnly
    ConnectionPredicate getConnectionPredicate();

    /// {@return the {@link BlockOverlayPredicate} determining which faces and edges thereof support {@link BlockOverlay}s}
    @ApiStatus.OverrideOnly
    BlockOverlayPredicate getBlockOverlayPredicate();

    /// {@return the {@link NullCullPredicate} determining whether "uncullable" faces of a double block's given part may
    /// be culled if the other part's camo is solid.}
    ///
    /// Only relevant for blocks returning `true` from [#isDoubleBlock()].
    @ApiStatus.OverrideOnly
    NullCullPredicate getNullCullPredicate();

    /// {@return the {@link ShapeGenerator} to use for computing this block's general shapes and, optionally, separate occlusion shapes}
    ShapeGenerator getShapeGenerator();

    /// {@return whether this block has a non-base BE (i.e. {@link FramedBlockEntity} and {@link FramedDoubleBlockEntity}
    /// for single- and double-blocks respectively)}
    ///
    /// Primarily intended automatic registration.
    boolean hasSpecialBlockEntity();

    /// {@return whether this block has a dedicated block item}
    ///
    /// Primarily intended for automatic registration and creative tab population.
    boolean hasBlockItem();

    /// {@return whether this block can be waterlogged}
    boolean isWaterloggable();

    /// {@return whether this block supports connected textures and other level/pos-dependent camo model behavior}
    boolean supportsConnectedTextures();

    /// {@return the minimum {@link ConTexMode} required for this block to react to texture connections}
    ConTexMode getMinimumConTexMode();

    /// {@return whether this block can be made intangible when the feature is enabled in the config}
    ///
    /// @implNote If this method returns `true`, then the associated block must register an instance of [FramedClientBlockExtensions]
    /// in [RegisterClientExtensionsEvent] to avoid crashing when the block is hit while it can be passed through.
    boolean allowMakingIntangible();

    /// {@return whether this block combines two models into one and allows those to have separate camos applied.}
    ///
    /// @implNote if a block's type returns `true` from this method, its [Block] must implement [IFramedDoubleBlock]
    /// and its [BlockEntity] must extend [FramedDoubleBlockEntity]. The [BlockStateModel] is not required
    /// to extend or implement any specific class.
    boolean isDoubleBlock();

    /// {@return whether this block can consume two camo items in the camo application recipe}
    default boolean consumesTwoCamosInCamoApplicationRecipe() {
        return isDoubleBlock();
    }

    /// {@return whether this block supports {@link BlockOverlay}s}
    boolean supportsBlockOverlays();

    /// {@return the name of this type (usually the path of the block's registry ID)}
    Identifier getName();

    /// Compare the two given block types against each other.
    /// The two types are first compared by their namespace and then by their
    /// [Comparable#compareTo(Object)] implementation.
    ///
    /// @param typeOne The first type to compare
    /// @param typeTwo The second type to compare
    /// @return the comparison result according to the rules of [Comparable#compareTo(Object)]
    @SuppressWarnings("unchecked")
    static <T extends Enum<T>> int compare(IBlockType typeOne, IBlockType typeTwo) {
        String namespaceOne = typeOne.getName().getNamespace();
        String namespaceTwo = typeTwo.getName().getNamespace();

        if (!namespaceOne.equals(namespaceTwo)) {
            if (namespaceOne.equals(FramedConstants.MOD_ID)) {
                return -1;
            }
            if (namespaceTwo.equals(FramedConstants.MOD_ID)) {
                return 1;
            }
            return namespaceOne.compareTo(namespaceTwo);
        }

        return ((T) typeOne).compareTo((T) typeTwo);
    }
}

package io.github.xfacthd.framedblocks.api.block.blockentity;

import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.Connection;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.common.extensions.IBlockEntityExtension;
import net.neoforged.neoforge.model.data.ModelData;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Delegating implementation of {@link IFramedBlockEntity} to allow extending BEs from vanilla or other mods and
 * attach FramedBlocks behavior to them by storing a {@link WrappedFramedBlockEntity}.
 * <p>
 * BEs implementing this interface must override the following methods and delegate them to the {@link WrappedFramedBlockEntity}:
 * <ul>
 *     <li>{@link BlockEntity#setRemoved()}</li>
 *     <li>{@link BlockEntity#clearRemoved()}</li>
 *     <li>{@link BlockEntity#setLevel(Level)}</li>
 *     <li>{@link BlockEntity#setBlockState(BlockState)}</li>
 *     <li>{@link BlockEntity#getUpdateTag(HolderLookup.Provider)} via {@link WrappedFramedBlockEntity#getUpdateTag(HolderLookup.Provider, Function)}</li>
 *     <li>{@link IBlockEntityExtension#handleUpdateTag(ValueInput)} via {@link WrappedFramedBlockEntity#handleUpdateTag(ValueInput, Consumer)}</li>
 *     <li>{@link IBlockEntityExtension#onDataPacket(Connection, ValueInput)} via {@link WrappedFramedBlockEntity#onDataPacket(Connection, ValueInput, BiConsumer)}</li>
 *     <li>{@link IBlockEntityExtension#getModelData()}</li>
 *     <li>{@link IBlockEntityExtension#onLoad()} via {@link WrappedFramedBlockEntity#onLoadInternal()} before calling the super method</li>
 *     <li>{@link BlockEntity#collectImplicitComponents(DataComponentMap.Builder)} via {@link WrappedFramedBlockEntity#collectImplicitComponentsForDelegate(DataComponentMap.Builder)}</li>
 *     <li>{@link BlockEntity#applyImplicitComponents(DataComponentGetter)} via {@link WrappedFramedBlockEntity#applyImplicitComponentsForDelegate(DataComponentGetter)}</li>
 *     <li>{@link BlockEntity#removeComponentsFromTag(ValueOutput)}</li>
 *     <li>{@link BlockEntity#loadAdditional(ValueInput)} via {@link WrappedFramedBlockEntity#loadAdditionalInternal(ValueInput)}</li>
 *     <li>{@link BlockEntity#saveAdditional(ValueOutput)} via {@link WrappedFramedBlockEntity#saveAdditionalInternal(ValueOutput)}</li>
 * </ul>
 * If the BE being extended to implement this interface does not already override {@link BlockEntity#getUpdatePacket()} to return a non-null value,
 * then it has to be overridden to return {@link WrappedFramedBlockEntity#getUpdatePacket()} and {@link IBlockEntityExtension#onDataPacket(Connection, ValueInput)}
 * has to be overridden to only delegate to {@link WrappedFramedBlockEntity#onDataPacket(Connection, ValueInput)} without a super call.
 */
@SuppressWarnings("deprecation")
public non-sealed interface DelegatingFramedBlockEntity extends IFramedBlockEntity {
    @Override
    @ApiStatus.OverrideOnly
    WrappedFramedBlockEntity unwrap();

    @Override
    @ApiStatus.NonExtendable
    default InteractionResult handleInteraction(Player player, InteractionHand hand, BlockHitResult hit) {
        return unwrap().handleInteraction(player, hand, hit);
    }

    @Override
    @ApiStatus.Internal
    @ApiStatus.NonExtendable
    default void tryApplyCamoImmediately(Player player) {
        unwrap().tryApplyCamoImmediately(player);
    }

    @Override
    @ApiStatus.NonExtendable
    default void setCamo(CamoContainer<?, ?> camo, BlockHitResult hit, Player player) {
        unwrap().setCamo(camo, hit, player);
    }

    @Override
    @ApiStatus.NonExtendable
    default void setCamo(CamoContainer<?, ?> camo, boolean secondary) {
        unwrap().setCamo(camo, secondary);
    }

    @Override
    default void setCamo(CamoContainer<?, ?> camo, boolean secondary, CamoOrientation orientation) {
        unwrap().setCamo(camo, secondary, orientation);
    }

    @Override
    @ApiStatus.NonExtendable
    default CamoContainer<?, ?> getCamo(BlockState state) {
        return unwrap().getCamo(state);
    }

    @Override
    @ApiStatus.NonExtendable
    default CamoContainer<?, ?> getCamo(Direction side, @Nullable Direction edge) {
        return unwrap().getCamo(side, edge);
    }

    @Override
    @ApiStatus.NonExtendable
    default CamoContainer<?, ?> getCamo(BlockHitResult hit, Player player) {
        return unwrap().getCamo(hit, player);
    }

    @Override
    @ApiStatus.NonExtendable
    default CamoContainer<?, ?> getCamo(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos) {
        return unwrap().getCamo(hit, lookVec, eyePos);
    }

    @Override
    default @Nullable Direction getCamoOrientation(boolean secondary) {
        return unwrap().getCamoOrientation(secondary);
    }

    @Override
    @ApiStatus.NonExtendable
    default CamoContainer<?, ?> getCamo() {
        return unwrap().getCamo();
    }

    @Override
    @ApiStatus.NonExtendable
    default boolean hasOverlay() {
        return unwrap().hasOverlay();
    }

    @Override
    @ApiStatus.NonExtendable
    default @Nullable Holder<BlockOverlay> getOverlay() {
        return unwrap().getOverlay();
    }

    @Override
    @ApiStatus.NonExtendable
    default void setOverlay(@Nullable Holder<BlockOverlay> overlay) {
        unwrap().setOverlay(overlay);
    }

    @Override
    @ApiStatus.NonExtendable
    default void setGlowing(boolean glowing) {
        unwrap().setGlowing(glowing);
    }

    @Override
    @ApiStatus.NonExtendable
    default boolean isGlowing() {
        return unwrap().isGlowing();
    }

    @Override
    @ApiStatus.NonExtendable
    default void setIntangible(boolean intangible) {
        unwrap().setIntangible(intangible);
    }

    @Override
    @ApiStatus.NonExtendable
    default boolean isMarkedIntangible() {
        return unwrap().isMarkedIntangible();
    }

    @Override
    @ApiStatus.NonExtendable
    default boolean isIntangible(@Nullable CollisionContext ctx) {
        return unwrap().isIntangible(ctx);
    }

    @Override
    @ApiStatus.NonExtendable
    default void setReinforced(boolean reinforced) {
        unwrap().setReinforced(reinforced);
    }

    @Override
    @ApiStatus.NonExtendable
    default boolean isReinforced() {
        return unwrap().isReinforced();
    }

    @Override
    @ApiStatus.NonExtendable
    default void setEmissive(boolean emissive) {
        unwrap().setEmissive(emissive);
    }

    @Override
    @ApiStatus.NonExtendable
    default boolean isEmissive() {
        return unwrap().isEmissive();
    }

    @Override
    @ApiStatus.NonExtendable
    default void updateCulling(boolean neighbors, boolean rerender) {
        unwrap().updateCulling(neighbors, rerender);
    }

    @Override
    @ApiStatus.NonExtendable
    default float getCamoExplosionResistance(Explosion explosion) {
        return unwrap().getCamoExplosionResistance(explosion);
    }

    @Override
    @ApiStatus.NonExtendable
    default boolean isCamoFlammable(Direction face) {
        return unwrap().isCamoFlammable(face);
    }

    @Override
    @ApiStatus.NonExtendable
    default int getCamoFlammability(Direction face) {
        return unwrap().getCamoFlammability(face);
    }

    @Override
    @ApiStatus.NonExtendable
    default int getCamoFireSpreadSpeed(Direction face) {
        return unwrap().getCamoFireSpreadSpeed(face);
    }

    @Override
    @ApiStatus.NonExtendable
    default boolean isCamoIgnitedByLava(Direction face) {
        return unwrap().isCamoIgnitedByLava(face);
    }

    @Override
    @ApiStatus.NonExtendable
    default @Nullable MapColor getMapColor() {
        return unwrap().getMapColor();
    }

    @Override
    @ApiStatus.NonExtendable
    default @Nullable Integer getCamoBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return unwrap().getCamoBeaconColorMultiplier(level, pos, beaconPos);
    }

    @Override
    @ApiStatus.NonExtendable
    default boolean shouldCamoDisplayFluidOverlay(BlockAndLightGetter level, BlockPos pos, FluidState fluid) {
        return unwrap().shouldCamoDisplayFluidOverlay(level, pos, fluid);
    }

    @Override
    @ApiStatus.NonExtendable
    default float getCamoFriction(BlockState state, @Nullable Entity entity, float frameFriction) {
        return unwrap().getCamoFriction(state, entity, frameFriction);
    }

    @Override
    @ApiStatus.NonExtendable
    default TriState canCamoSustainPlant(BlockGetter level, Direction side, BlockState plant) {
        return unwrap().canCamoSustainPlant(level, side, plant);
    }

    @Override
    @ApiStatus.NonExtendable
    default boolean canEntityDestroyCamo(Entity entity) {
        return unwrap().canEntityDestroyCamo(entity);
    }

    @Override
    @ApiStatus.NonExtendable
    default IFramedBlock getBlock() {
        return unwrap().getBlock();
    }

    @Override
    @ApiStatus.NonExtendable
    default IBlockType getBlockType() {
        return unwrap().getBlockType();
    }

    @Override
    @ApiStatus.NonExtendable
    default boolean canTriviallyDropAllCamos() {
        return unwrap().canTriviallyDropAllCamos();
    }

    @Override
    @ApiStatus.NonExtendable
    default void addAdditionalDrops(Consumer<ItemStack> drops, boolean dropCamo) {
        unwrap().addAdditionalDrops(drops, dropCamo);
    }

    @Override
    @ApiStatus.NonExtendable
    default void setBlockState(BlockState state) {
        unwrap().setBlockState(state);
    }

    @Override
    @ApiStatus.NonExtendable
    default ModelData getModelData(boolean includeCullInfo, BlockState state) {
        return unwrap().getModelData(includeCullInfo, state);
    }

    @Override
    @ApiStatus.NonExtendable
    default BlueprintData writeToBlueprint() {
        return unwrap().writeToBlueprint();
    }

    @Override
    @ApiStatus.NonExtendable
    default void applyBlueprintData(BlueprintData blueprintData) {
        unwrap().applyBlueprintData(blueprintData);
    }
}

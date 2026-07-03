package io.github.xfacthd.framedblocks.api.block.blockentity;

import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.overlay.BlockOverlay;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
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
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.common.extensions.IBlockEntityExtension;
import net.neoforged.neoforge.model.data.ModelData;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public sealed interface IFramedBlockEntity extends IBlockEntityExtension permits FramedBlockEntity, DelegatingFramedBlockEntity {
    /// Handle a player interaction on this block.
    ///
    /// @param player The player interacting with this block
    /// @param hand   The hand used for the interaction
    /// @param hit    The exact location at which the player interacted with this block
    /// @return the result of the interaction
    InteractionResult handleInteraction(Player player, InteractionHand hand, BlockHitResult hit);

    /// Apply a camo from the given player's off-hand immediately after placement.
    ///
    /// @param player The player who placed this block
    @ApiStatus.Internal
    void tryApplyCamoImmediately(Player player);

    /// Update the camo of this block. Whether the primary or secondary camo will be replaced depends
    /// on the given [BlockHitResult] and [Player].
    ///
    /// @param camo The camo to apply to this block
    /// @param hit  The exact location of the interaction used to apply the camo
    void setCamo(CamoContainer<?, ?> camo, BlockHitResult hit, Player player);

    /// Update the camo of this block. The `secondary` flag is ignored by single-camo blocks.
    ///
    /// @param camo      The camo to apply
    /// @param secondary Whether the first or second camo "slot" should be modified
    void setCamo(CamoContainer<?, ?> camo, boolean secondary);

    /// Returns the camo applied to the camo "slot" related to the given "substate"
    CamoContainer<?, ?> getCamo(BlockState state);

    /// Returns the camo for the given edge of the given side or for the full face if a `null` edge is provided.
    /// Returns an empty camo if the provided combination does not uniquely identify a single camo "slot".
    ///
    /// @param side The block's side to retrieve the camo for
    /// @param edge The side's edge to retrieve the camo for or `null` for the full face
    CamoContainer<?, ?> getCamo(Direction side, @Nullable Direction edge);

    /// Returns the camo the given [BlockHitResult] points at based on the given player's
    /// eye position and look vector.
    ///
    /// @param hit    The result of the raycast against this block
    /// @param player The player from which the raycast originated
    CamoContainer<?, ?> getCamo(BlockHitResult hit, Player player);

    /// Returns the camo the given [BlockHitResult] points at based on the given
    /// eye position and look vector.
    ///
    /// @param hit     The result of the raycast against this block
    /// @param lookVec The look vector used for the raycast (usually [Player#getLookAngle()])
    /// @param eyePos  The eye position from which the raycast originated (usually [Player#getEyePosition()])
    CamoContainer<?, ?> getCamo(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos);

    /// {@return the first camo applied to this block}
    CamoContainer<?, ?> getCamo();

    /// {@return whether this block has a [BlockOverlay] applied to it}
    boolean hasOverlay();

    /// {@return the [BlockOverlay] applied to this block, if any}
    @Nullable Holder<BlockOverlay> getOverlay();

    /// Apply the given [BlockOverlay] to this block.
    ///
    /// @param overlay The block overlay to apply
    void setOverlay(@Nullable Holder<BlockOverlay> overlay);

    /// Configure whether this block emits light.
    ///
    /// @param glowing Whether this block emits light
    void setGlowing(boolean glowing);

    /// {@return whether this block emits light}
    boolean isGlowing();

    /// Configure whether this block should be intangible.
    ///
    /// @param intangible Whether this block should be intangible
    void setIntangible(boolean intangible);

    /// Returns whether this block is marked as intangible.
    ///
    /// If this method returns `true`, an entity interacting with this block may still behave as if it
    /// returned `false` depending on the context and config.
    ///
    /// @return whether this block is marked as intangible
    boolean isMarkedIntangible();

    /// Returns whether this block is intangible based on the given context and current configuration.
    ///
    /// @param ctx The collision context of the entity checking for intangibility
    /// @return whether this block is intangible
    boolean isIntangible(@Nullable CollisionContext ctx);

    /// Configure whether this block is reinforced.
    ///
    /// @param reinforced Whether this block is reinforced
    void setReinforced(boolean reinforced);

    /// {@return whether this block is reinforced}
    boolean isReinforced();

    /// Configure whether this block appears emissive (i.e. fullbright).
    ///
    /// @param emissive Whether this block is emissive
    void setEmissive(boolean emissive);

    /// {@return whether this block appears emissive}
    boolean isEmissive();

    /// Update the cached occlusion state of this block.
    ///
    /// @param neighbors Whether neighbors should update the occlusion state on their respective face pointing towards this block
    /// @param rerender  Whether this block should trigger a re-render if the occlusion state changed
    void updateCulling(boolean neighbors, boolean rerender);

    /// {@return this block's resistance against the given explosion}
    ///
    /// @param explosion The explosion to check the resistance against
    float getCamoExplosionResistance(Explosion explosion);

    /// {@return whether this block is flammable on the given side}
    ///
    /// @param face The side to check for flammability
    boolean isCamoFlammable(Direction face);

    /// {@return how likely this block is to be consumed by fire on the given side}
    ///
    /// @param face The side to query
    int getCamoFlammability(Direction face);

    /// {@return how likely fire is to spread to the given side of this block}
    ///
    /// @param face The side to query
    int getCamoFireSpreadSpeed(Direction face);

    /// {@return whether this block can be ignited by lava on the given side}
    ///
    /// @param face The side to check for ability to ignite by lava
    boolean isCamoIgnitedByLava(Direction face);

    /// {@return this block's map color}
    @Nullable MapColor getMapColor();

    /// {@return this block's beacon color multiplier}
    @Nullable Integer getCamoBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos);

    /// Return whether the given fluid should render its overlay against this block.
    ///
    /// @param level The level this block is in
    /// @param pos   The position of this block in the level
    /// @param fluid The fluid being rendered
    /// @return whether the fluid overlay should be rendered
    boolean shouldCamoDisplayFluidOverlay(BlockAndLightGetter level, BlockPos pos, FluidState fluid);

    /// Returns the friction the given entity encounters on this block.
    ///
    /// @param state         The state of this block
    /// @param entity        The entity touching this block
    /// @param frameFriction The friction of the camo-less framed block
    /// @return the friction encountered by the entity
    float getCamoFriction(BlockState state, @Nullable Entity entity, float frameFriction);

    /// Check whether the given plant blockstate can be sustained by given side of this block.
    ///
    /// @param level The level this block is in
    /// @param side  The side the plant is planted on
    /// @param plant The plant to test against
    /// @return whether the plant can be sustained
    TriState canCamoSustainPlant(BlockGetter level, Direction side, BlockState plant);

    /// {@return whether the given entity can destroy this block}
    ///
    /// @param entity The entity attempting to destroy this block
    boolean canEntityDestroyCamo(Entity entity);

    /// Apply a rotation to this block with a wrench.
    ///
    /// @param rotation     The rotation applied to this block
    /// @param stateChanged Whether the rotation changed the underlying blockstate
    void applyWrenchRotation(Rotation rotation, boolean stateChanged);

    /// {@return the [IFramedBlock] this BE is attached to}
    IFramedBlock getBlock();

    /// {@return the block type of the block this BE is attached to}
    IBlockType getBlockType();

    /// {@return whether all camos applied to this block can be trivially converted to [ItemStack]s for dropping}
    boolean canTriviallyDropAllCamos();

    /// Add additional drops to the list of items being dropped
    ///
    /// @param drops    The list of items being dropped
    /// @param dropCamo Whether the camo items should be dropped
    void addAdditionalDrops(Consumer<ItemStack> drops, boolean dropCamo);

    void setBlockState(BlockState state);

    /// Compute the [ModelData] for this BE based on the provided [BlockState].
    ///
    /// @param includeCullInfo Whether culling data should be included
    /// @param state           The [BlockState] with which the model data is used for rendering (usually [#getBlockState()])
    ModelData getModelData(boolean includeCullInfo, BlockState state);

    /// Write this BE to [BlueprintData] for storage on a Framed Blueprint.
    ///
    /// @return the packed blueprint data
    BlueprintData writeToBlueprint();

    /// Apply data from the given [BlueprintData] to this BE after being placed by a Framed Blueprint.
    ///
    /// @param blueprintData The blueprint data to apply
    void applyBlueprintData(BlueprintData blueprintData);

    /// {@return the [BlockEntityType] of this BE}
    @ApiStatus.NonExtendable
    BlockEntityType<?> getType();

    /// {@return the curent [BlockState] of the block this BE is associated to}
    @ApiStatus.NonExtendable
    BlockState getBlockState();

    /// {@return the position of this BE in the level}
    @ApiStatus.NonExtendable
    BlockPos getBlockPos();

    /// {@return the level this BE is in}
    @ApiStatus.NonExtendable
    @Nullable Level getLevel();

    /// {@return the concrete [FramedBlockEntity] represented by this [IFramedBlockEntity]}
    @ApiStatus.Internal
    FramedBlockEntity unwrap();
}

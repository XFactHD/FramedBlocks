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
    InteractionResult handleInteraction(Player player, InteractionHand hand, BlockHitResult hit);

    @ApiStatus.Internal
    void tryApplyCamoImmediately(Player player);

    void setCamo(CamoContainer<?, ?> camo, BlockHitResult hit, Player player);

    void setCamo(CamoContainer<?, ?> camo, boolean secondary);

    CamoContainer<?, ?> getCamo(BlockState state);

    CamoContainer<?, ?> getCamo(Direction side, @Nullable Direction edge);

    CamoContainer<?, ?> getCamo(BlockHitResult hit, Player player);

    CamoContainer<?, ?> getCamo(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos);

    CamoContainer<?, ?> getCamo();

    boolean hasOverlay();

    @Nullable Holder<BlockOverlay> getOverlay();

    void setOverlay(@Nullable Holder<BlockOverlay> overlay);

    void setGlowing(boolean glowing);

    boolean isGlowing();

    void setIntangible(boolean intangible);

    boolean isMarkedIntangible();

    boolean isIntangible(@Nullable CollisionContext ctx);

    void setReinforced(boolean reinforced);

    boolean isReinforced();

    void setEmissive(boolean emissive);

    boolean isEmissive();

    void updateCulling(boolean neighbors, boolean rerender);

    float getCamoExplosionResistance(Explosion explosion);

    boolean isCamoFlammable(Direction face);

    int getCamoFlammability(Direction face);

    int getCamoFireSpreadSpeed(Direction face);

    boolean isCamoIgnitedByLava(Direction face);

    @Nullable MapColor getMapColor();

    @Nullable Integer getCamoBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos);

    boolean shouldCamoDisplayFluidOverlay(BlockAndLightGetter level, BlockPos pos, FluidState fluid);

    float getCamoFriction(BlockState state, @Nullable Entity entity, float frameFriction);

    TriState canCamoSustainPlant(BlockGetter level, Direction side, BlockState plant);

    boolean canEntityDestroyCamo(Entity entity);

    float getCamoBounceRestitution(Entity entity);

    void applyWrenchRotation(Rotation rotation, boolean stateChanged);

    IFramedBlock getBlock();

    IBlockType getBlockType();

    boolean canTriviallyDropAllCamos();

    void addAdditionalDrops(Consumer<ItemStack> drops, boolean dropCamo);

    void setBlockState(BlockState state);

    ModelData getModelData(boolean includeCullInfo, BlockState state);

    BlueprintData writeToBlueprint();

    void applyBlueprintData(BlueprintData blueprintData);

    @ApiStatus.NonExtendable
    BlockEntityType<?> getType();

    @ApiStatus.NonExtendable
    BlockState getBlockState();

    @ApiStatus.NonExtendable
    BlockPos getBlockPos();

    @ApiStatus.NonExtendable
    @Nullable Level getLevel();

    @ApiStatus.Internal
    FramedBlockEntity unwrap();
}

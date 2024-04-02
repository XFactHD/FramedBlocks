package xfacthd.framedblocks.api.block.blockentity;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.cache.StateCache;
import xfacthd.framedblocks.api.block.render.CullingHelper;
import xfacthd.framedblocks.api.camo.*;
import xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import xfacthd.framedblocks.api.internal.InternalAPI;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.*;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class FramedBlockEntity extends BlockEntity
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Component MSG_BLACKLISTED = Utils.translate("msg", "camo.blacklisted");
    public static final Component MSG_BLOCK_ENTITY = Utils.translate("msg", "camo.block_entity");
    public static final Component MSG_NON_SOLID = Utils.translate("msg", "camo.non_solid");
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final int DATA_VERSION = 3;
    protected static final int FLAG_GLOWING = 1;
    protected static final int FLAG_INTANGIBLE = 1 << 1;
    protected static final int FLAG_REINFORCED = 1 << 2;

    private final boolean[] culledFaces = new boolean[6];
    private StateCache stateCache;
    private CamoContainer<?, ?> camoContainer = EmptyCamoContainer.EMPTY;
    private boolean glowing = false;
    private boolean intangible = false;
    private boolean reinforced = false;
    private boolean recheckStates = false;

    /**
     * @apiNote internal, addons must use their own {@link BlockEntityType} with the three-arg constructor
     */
    @ApiStatus.Internal
    public FramedBlockEntity(BlockPos pos, BlockState state)
    {
        this(InternalAPI.INSTANCE.getDefaultBlockEntity(), pos, state);
    }

    public FramedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.stateCache = ((IFramedBlock) state.getBlock()).getCache(state);
    }

    public final InteractionResult handleInteraction(Player player, InteractionHand hand, BlockHitResult hit)
    {
        ItemStack stack = player.getItemInHand(hand);
        boolean secondary = hitSecondary(hit);
        CamoContainer<?, ?> camo = getCamo(secondary);

        CamoContainerFactory<?> camoFactory;
        if (camo.isEmpty() && (camoFactory = CamoContainerHelper.findCamoFactory(stack)) != null)
        {
            return setCamo(player, stack, camoFactory, secondary);
        }
        else if (!camo.isEmpty() && CamoContainerHelper.isValidRemovalTool(camo, stack))
        {
            return clearCamo(player, stack, camo, secondary);
        }
        else if (stack.is(Tags.Items.DUSTS_GLOWSTONE) && !glowing)
        {
            return applyGlowstone(player, stack);
        }
        else if (!camo.isEmpty() && !player.isShiftKeyDown() && stack.is(Utils.FRAMED_SCREWDRIVER.value()))
        {
            return rotateCamo(camo, secondary);
        }
        else if (!intangible && canMakeIntangible(stack))
        {
            return applyIntangibility(player, stack);
        }
        else if (intangible && player.isShiftKeyDown() && stack.is(Utils.FRAMED_SCREWDRIVER.value()))
        {
            return removeIntangibility(player);
        }
        else if (!reinforced && stack.is(Utils.FRAMED_REINFORCEMENT.value()))
        {
            return applyReinforcement(player, stack);
        }
        else if (reinforced && canRemoveReinforcement(stack))
        {
            return removeReinforcement(player, stack, hand);
        }

        return InteractionResult.PASS;
    }

    private boolean canMakeIntangible(ItemStack stack)
    {
        if (!ConfigView.Server.INSTANCE.enableIntangibility())
        {
            return false;
        }
        return stack.is(ConfigView.Server.INSTANCE.getIntangibilityMarkerItem()) && getBlockType().allowMakingIntangible();
    }

    private static boolean canRemoveReinforcement(ItemStack stack)
    {
        if (stack.getItem().canPerformAction(stack, ToolActions.PICKAXE_DIG))
        {
            return stack.isCorrectToolForDrops(Blocks.OBSIDIAN.defaultBlockState());
        }
        return false;
    }

    private InteractionResult setCamo(Player player, ItemStack stack, CamoContainerFactory<?> factory, boolean secondary)
    {
        if (stack.getItem() instanceof BlockItem item && item.getBlock() instanceof IFramedBlock)
        {
            return InteractionResult.FAIL;
        }

        CamoContainer<?, ?> camo = factory.applyCamo(level(), worldPosition, player, stack);
        if (camo != null)
        {
            if (!level().isClientSide())
            {
                setCamo(camo, secondary);
            }
            return InteractionResult.sidedSuccess(level().isClientSide());
        }
        return InteractionResult.CONSUME;
    }

    private InteractionResult clearCamo(Player player, ItemStack stack, CamoContainer<?, ?> camo, boolean secondary)
    {
        if (CamoContainerHelper.removeCamo(camo, level(), worldPosition, player, stack))
        {
            if (!level().isClientSide())
            {
                setCamo(EmptyCamoContainer.EMPTY, secondary);
            }
            return InteractionResult.sidedSuccess(level().isClientSide());
        }
        return InteractionResult.FAIL;
    }

    private InteractionResult applyGlowstone(Player player, ItemStack stack)
    {
        if (!level().isClientSide())
        {
            if (!player.isCreative())
            {
                stack.shrink(1);
            }

            setGlowing(true);
        }
        return InteractionResult.sidedSuccess(level().isClientSide());
    }

    private InteractionResult rotateCamo(CamoContainer<?, ?> camo, boolean secondary)
    {
        if (camo.canRotateCamo())
        {
            if (!level().isClientSide())
            {
                CamoContainer<?, ?> newCamo = camo.rotateCamo();
                Objects.requireNonNull(newCamo, "CamoContainer#rotateCamo() must not return null if CamoContainer#canRotateCamo() returns true");
                setCamo(newCamo, secondary);
                setChangedWithoutSignalUpdate();
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
            return InteractionResult.sidedSuccess(level().isClientSide());
        }
        return InteractionResult.FAIL;
    }

    private InteractionResult applyIntangibility(Player player, ItemStack stack)
    {
        if (!level().isClientSide())
        {
            if (!player.isCreative())
            {
                stack.shrink(1);
            }

            setIntangible(true);
        }
        return InteractionResult.sidedSuccess(level().isClientSide());
    }

    private InteractionResult removeIntangibility(Player player)
    {
        if (!level().isClientSide())
        {
            setIntangible(false);

            ItemStack result = new ItemStack(ConfigView.Server.INSTANCE.getIntangibilityMarkerItem());
            if (!player.getInventory().add(result))
            {
                player.drop(result, false);
            }
        }
        return InteractionResult.sidedSuccess(level().isClientSide());
    }

    private InteractionResult applyReinforcement(Player player, ItemStack stack)
    {
        if (!level().isClientSide())
        {
            if (!player.isCreative())
            {
                stack.shrink(1);
            }

            setReinforced(true);
        }
        return InteractionResult.sidedSuccess(level().isClientSide());
    }

    private InteractionResult removeReinforcement(Player player, ItemStack stack, InteractionHand hand)
    {
        if (!level().isClientSide())
        {
            setReinforced(false);

            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

            ItemStack result = new ItemStack(Utils.FRAMED_REINFORCEMENT.value());
            if (!player.getInventory().add(result))
            {
                player.drop(result, false);
            }
        }
        return InteractionResult.sidedSuccess(level().isClientSide());
    }

    protected boolean hitSecondary(BlockHitResult hit)
    {
        return false;
    }

    public final void setCamo(CamoContainer<?, ?> camo, boolean secondary)
    {
        int light = getLightValue();

        setCamoInternal(camo, secondary);

        setChangedWithoutSignalUpdate();
        if (getLightValue() != light)
        {
            doLightUpdate();
        }

        if (!updateDynamicStates(true, true, true))
        {
            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    protected void setCamoInternal(CamoContainer<?, ?> camo, boolean secondary)
    {
        this.camoContainer = camo;
    }

    public boolean isSolidSide(Direction side)
    {
        if (camoContainer.isEmpty())
        {
            return false;
        }
        return stateCache.isFullFace(side) && camoContainer.getContent().isSolid(level(), worldPosition);
    }

    /**
     * Returns the camo for the given {@link BlockState}. Used for cases where different double blocks
     * with the same underlying shape(s) don't use the same side to return the camo for a given "sub-state".
     */
    public CamoContainer<?, ?> getCamo(BlockState state)
    {
        return camoContainer;
    }

    /**
     * Used to return a different camo depending on the given side
     * @param side The blocks face, can return EMPTY if the face does not pass the CTM_PREDICATE
     */
    public CamoContainer<?, ?> getCamo(Direction side)
    {
        return camoContainer;
    }

    /**
     * Returns the camo for the given edge of the given side
     */
    public CamoContainer<?, ?> getCamo(Direction side, @Nullable Direction edge)
    {
        return getCamo(side);
    }

    /**
     * Used to return a different camo depending on the exact interaction location
     */
    public CamoContainer<?, ?> getCamo(BlockHitResult hit)
    {
        return getCamo(hitSecondary(hit));
    }

    protected CamoContainer<?, ?> getCamo(boolean secondary)
    {
        return camoContainer;
    }

    public final CamoContainer<?, ?> getCamo()
    {
        return camoContainer;
    }

    protected boolean isCamoSolid()
    {
        return camoContainer.getContent().isSolid(level(), worldPosition);
    }

    protected boolean doesCamoPropagateSkylightDown()
    {
        return camoContainer.getContent().propagatesSkylightDown(level(), worldPosition);
    }

    public final void checkCamoSolid()
    {
        boolean checkSolid = getBlock().getBlockType().canOccludeWithSolidCamo();
        updateDynamicStates(checkSolid, true, true);
    }

    protected final boolean updateDynamicStates(boolean updateSolid, boolean updateLight, boolean updateSkylight)
    {
        BlockState state = getBlockState();
        boolean changed = false;

        if (updateSolid && getBlock().getBlockType().canOccludeWithSolidCamo())
        {
            boolean wasSolid = getBlockState().getValue(FramedProperties.SOLID);
            boolean solid = !intangible && isCamoSolid();

            if (solid != wasSolid)
            {
                state = state.setValue(FramedProperties.SOLID, solid);
                changed = true;
            }
        }

        if (updateLight)
        {
            boolean isGlowing = getLightValue() > 0;

            if (isGlowing != state.getValue(FramedProperties.GLOWING))
            {
                state = state.setValue(FramedProperties.GLOWING, isGlowing);
                changed = true;
            }
        }

        if (updateSkylight)
        {
            boolean propagatesSkylight = doesCamoPropagateSkylightDown();

            if (propagatesSkylight != state.getValue(FramedProperties.PROPAGATES_SKYLIGHT))
            {
                state = state.setValue(FramedProperties.PROPAGATES_SKYLIGHT, propagatesSkylight);
                changed = true;
            }
        }

        if (changed)
        {
            level().setBlock(worldPosition, state, Block.UPDATE_ALL);
        }
        return changed;
    }

    @SuppressWarnings("ConstantConditions")
    public final void updateCulling(boolean neighbors, boolean rerender)
    {
        boolean changed = false;
        for (Direction dir : DIRECTIONS)
        {
            BlockState state = getBlockState();
            changed |= updateCulling(dir, state, false);
            if (neighbors && level().getBlockEntity(worldPosition.relative(dir)) instanceof FramedBlockEntity be)
            {
                be.updateCulling(dir.getOpposite(), be.getBlockState(), true);
            }
        }

        if (changed)
        {
            requestModelDataUpdate();
            if (rerender)
            {
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    protected boolean updateCulling(Direction side, BlockState state, boolean rerender)
    {
        return updateCulling(culledFaces, state, side, rerender);
    }

    protected final boolean updateCulling(boolean[] culledFaces, BlockState testState, Direction side, boolean rerender)
    {
        boolean wasHidden = culledFaces[side.ordinal()];
        boolean hidden = CullingHelper.isSideHidden(level(), worldPosition, testState, side);
        if (wasHidden != hidden)
        {
            culledFaces[side.ordinal()] = hidden;
            requestModelDataUpdate();
            if (rerender)
            {
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
            return true;
        }
        return false;
    }

    public float getCamoExplosionResistance(Explosion explosion)
    {
        float camoRes = camoContainer.getContent().getExplosionResistance(level(), worldPosition, explosion);
        if (reinforced)
        {
            camoRes = Math.max(camoRes, Blocks.OBSIDIAN.getExplosionResistance());
        }
        return camoRes;
    }

    public boolean isCamoFlammable(Direction face)
    {
        if (reinforced)
        {
            return false;
        }
        return camoContainer.isEmpty() || camoContainer.getContent().isFlammable(level(), worldPosition, face);
    }

    public int getCamoFlammability(Direction face)
    {
        if (reinforced)
        {
            return 0;
        }
        return camoContainer.isEmpty() ? -1 : camoContainer.getContent().getFlammability(level(), worldPosition, face);
    }

    public int getCamoFireSpreadSpeed(Direction face)
    {
        if (reinforced)
        {
            return 0;
        }
        return camoContainer.isEmpty() ? -1 : camoContainer.getContent().getFireSpreadSpeed(level(), worldPosition, face);
    }

    public float getCamoShadeBrightness(float ownShade)
    {
        return camoContainer.getContent().getShadeBrightness(level(), worldPosition, ownShade);
    }

    public final void setGlowing(boolean glowing)
    {
        if (this.glowing != glowing)
        {
            int oldLight = getLightValue();
            this.glowing = glowing;
            if (oldLight != getLightValue())
            {
                doLightUpdate();
            }

            setChangedWithoutSignalUpdate();
            if (!updateDynamicStates(false, true, false))
            {
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    public final boolean isGlowing()
    {
        return glowing;
    }

    protected int getLightValue()
    {
        int baseLight = glowing ? ConfigView.Server.INSTANCE.getGlowstoneLightLevel() : 0;
        return Math.max(baseLight, camoContainer.getContent().getLightEmission());
    }

    public void setIntangible(boolean intangible)
    {
        if (this.intangible != intangible)
        {
            this.intangible = intangible;

            setChangedWithoutSignalUpdate();

            if (!updateDynamicStates(true, false, false))
            {
                level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    public boolean isIntangible(CollisionContext ctx)
    {
        if (!ConfigView.Server.INSTANCE.enableIntangibility() || !intangible)
        {
            return false;
        }

        if (ctx instanceof EntityCollisionContext ectx && ectx.getEntity() instanceof Player player)
        {
            ItemStack mainItem = player.getMainHandItem();
            return !mainItem.is(Utils.DISABLE_INTANGIBLE);
        }

        return true;
    }

    public void setReinforced(boolean reinforced)
    {
        if (this.reinforced != reinforced)
        {
            this.reinforced = reinforced;

            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            setChangedWithoutSignalUpdate();
        }
    }

    public boolean isReinforced()
    {
        return reinforced;
    }

    protected final void doLightUpdate()
    {
        AuxiliaryLightManager lightManager = level().getAuxLightManager(worldPosition);
        if (lightManager != null)
        {
            lightManager.setLightAt(worldPosition, getLightValue());
        }
    }

    public IFramedBlock getBlock()
    {
        return (IFramedBlock) getBlockState().getBlock();
    }

    public final IBlockType getBlockType()
    {
        return getBlock().getBlockType();
    }

    protected final Level level()
    {
        return Objects.requireNonNull(level, "BlockEntity#level accessed before it was set");
    }

    protected final void setChangedWithoutSignalUpdate()
    {
        level().blockEntityChanged(worldPosition);
    }

    protected StateCache getStateCache()
    {
        return stateCache;
    }

    public boolean canAutoApplyCamoOnPlacement()
    {
        return true;
    }

    /**
     * Add additional drops to the list of items being dropped
     * @param drops The list of items being dropped
     * @param dropCamo Whether the camo item should be dropped
     */
    public void addAdditionalDrops(List<ItemStack> drops, boolean dropCamo)
    {
        if (dropCamo && !camoContainer.isEmpty())
        {
            ItemStack stack = CamoContainerHelper.dropCamo(camoContainer);
            if (!stack.isEmpty())
            {
                drops.add(stack);
            }
        }
        if (reinforced)
        {
            drops.add(new ItemStack(Utils.FRAMED_REINFORCEMENT.value()));
        }
    }

    public MapColor getMapColor()
    {
        return camoContainer.getMapColor(level(), worldPosition);
    }

    public float[] getCamoBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        return camoContainer.getBeaconColorMultiplier(level, pos, beaconPos);
    }

    public boolean shouldCamoDisplayFluidOverlay(BlockAndTintGetter level, BlockPos pos, FluidState fluid)
    {
        return camoContainer.getContent().shouldDisplayFluidOverlay(level, pos, fluid);
    }

    public float getCamoFriction(BlockState state, @Nullable Entity entity, float frameFriction)
    {
        return camoContainer.getContent().getFriction(level(), worldPosition, entity, frameFriction);
    }

    public boolean canCamoSustainPlant(Direction side, IPlantable plant)
    {
        return camoContainer.getContent().canSustainPlant(level(), worldPosition, side, plant);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canEntityDestroyCamo(Entity entity)
    {
        if (reinforced && !Blocks.OBSIDIAN.defaultBlockState().canEntityDestroy(level(), worldPosition, entity))
        {
            return false;
        }
        return camoContainer.getContent().canEntityDestroy(level(), worldPosition, entity);
    }

    @Override
    public void onLoad()
    {
        if (!level().isClientSide() && recheckStates)
        {
            checkCamoSolid();
        }
        super.onLoad();
    }

    @Override
    public void setBlockState(BlockState state)
    {
        super.setBlockState(state);
        this.stateCache = ((IFramedBlock) state.getBlock()).getCache(state);
    }

    /*
     * Sync
     */

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this, be ->
        {
            CompoundTag tag = new CompoundTag();
            ((FramedBlockEntity) be).writeToDataPacket(tag);
            return tag;
        });
    }

    @Override
    public final void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
    {
        CompoundTag nbt = pkt.getTag();
        if (nbt != null && readFromDataPacket(nbt))
        {
            level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            requestModelDataUpdate();
        }
    }

    protected void writeToDataPacket(CompoundTag nbt)
    {
        nbt.put("camo", CamoContainerHelper.writeToNetwork(camoContainer));
        nbt.putByte("flags", writeFlags());
    }

    protected boolean readFromDataPacket(CompoundTag nbt)
    {
        boolean needUpdate = false;
        boolean needCullingUpdate = false;

        CamoContainer<?, ?> newCamo = CamoContainerHelper.readFromNetwork(nbt.getCompound("camo"));
        if (!newCamo.equals(camoContainer))
        {
            int oldLight = getLightValue();
            camoContainer = newCamo;
            if (oldLight != getLightValue())
            {
                doLightUpdate();
            }

            needUpdate = true;
            needCullingUpdate = true;
        }

        byte flags = nbt.getByte("flags");

        boolean newGlow = readFlag(flags, FLAG_GLOWING);
        if (newGlow != glowing)
        {
            glowing = newGlow;
            needUpdate = true;

            doLightUpdate();
        }

        boolean newIntangible = readFlag(flags, FLAG_INTANGIBLE);
        if (newIntangible != intangible)
        {
            intangible = newIntangible;
            needUpdate = true;
            needCullingUpdate = true;
        }

        boolean newReinforced = readFlag(flags, FLAG_REINFORCED);
        if (newReinforced != reinforced)
        {
            reinforced = newReinforced;
            needUpdate = true;
        }

        if (needCullingUpdate)
        {
            updateCulling(true, false);
        }

        return needUpdate;
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag nbt = super.getUpdateTag();

        nbt.put("camo", CamoContainerHelper.writeToNetwork(camoContainer));
        nbt.putByte("flags", writeFlags());

        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt)
    {
        CamoContainer<?, ?> newCamo = CamoContainerHelper.readFromNetwork(nbt.getCompound("camo"));
        if (!newCamo.equals(camoContainer))
        {
            camoContainer = newCamo;
            ClientUtils.enqueueClientTask(() -> updateCulling(true, true));
        }

        byte flags = nbt.getByte("flags");
        glowing = readFlag(flags, FLAG_GLOWING);
        intangible = readFlag(flags, FLAG_INTANGIBLE);

        boolean newReinforced = readFlag(flags, FLAG_REINFORCED);
        if (newReinforced != reinforced)
        {
            reinforced = newReinforced;
        }

        requestModelDataUpdate();
    }

    private byte writeFlags()
    {
        byte flags = 0;
        if (glowing) flags |= FLAG_GLOWING;
        if (intangible) flags |= FLAG_INTANGIBLE;
        if (reinforced) flags |= FLAG_REINFORCED;
        return flags;
    }

    protected static boolean readFlag(byte flags, int flag)
    {
        return (flags & flag) != 0;
    }

    /*
     * Model data
     */

    @Override
    public final ModelData getModelData()
    {
        return getModelData(true);
    }

    /**
     * @param includeCullInfo Whether culling data should be included
     */
    public ModelData getModelData(boolean includeCullInfo)
    {
        boolean[] cullData = includeCullInfo ? culledFaces : FramedBlockData.NO_CULLED_FACES;
        FramedBlockData modelData = new FramedBlockData(camoContainer.getContent(), cullData, false, isReinforced());
        ModelData.Builder builder = ModelData.builder().with(FramedBlockData.PROPERTY, modelData);
        attachAdditionalModelData(builder);
        return builder.build();
    }

    protected void attachAdditionalModelData(ModelData.Builder builder) { }

    /*
     * NBT stuff
     */

    public CompoundTag writeToBlueprint()
    {
        return saveWithoutMetadata();
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.put("camo", CamoContainerHelper.writeToDisk(camoContainer));
        nbt.putBoolean("glowing", glowing);
        nbt.putBoolean("intangible", intangible);
        nbt.putBoolean("reinforced", reinforced);
        nbt.putByte("updated", (byte) DATA_VERSION);

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        camoContainer = loadAndValidateCamo(nbt, "camo");
        glowing = nbt.getBoolean("glowing");
        intangible = nbt.getBoolean("intangible");
        reinforced = nbt.getBoolean("reinforced");
    }

    protected CamoContainer<?, ?> loadAndValidateCamo(CompoundTag tag, String key)
    {
        CamoContainer<?, ?> camo = CamoContainerHelper.readFromDisk(tag.getCompound(key));
        if (!CamoContainerHelper.validateCamo(camo))
        {
            recheckStates = true;
            LOGGER.warn(
                    "Framed Block of type \"{}\" at position {} contains an invalid camo of type \"\" containing \"{}\", removing camo! This might be caused by a config or tag change!",
                    BuiltInRegistries.BLOCK.getKey(getBlockState().getBlock()),
                    worldPosition,
                    camo.getContent().getCamoId()
            );
            return EmptyCamoContainer.EMPTY;
        }
        recheckStates = tag.getByte("updated") < DATA_VERSION;
        return camo;
    }
}

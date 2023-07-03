package xfacthd.framedblocks.api.block;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.camo.EmptyCamoContainer;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.util.ClientUtils;

import java.util.List;

@SuppressWarnings("deprecation")
public class FramedBlockEntity extends BlockEntity
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Component MSG_BLACKLISTED = Utils.translate("msg", "blacklisted");
    public static final Component MSG_BLOCK_ENTITY = Utils.translate("msg", "block_entity");
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final int DATA_VERSION = 2;
    protected static final int FLAG_GLOWING = 1;
    protected static final int FLAG_INTANGIBLE = 1 << 1;
    protected static final int FLAG_REINFORCED = 1 << 2;

    private final FramedBlockData modelData = new FramedBlockData();
    private CamoContainer camoContainer = EmptyCamoContainer.EMPTY;
    private boolean glowing = false;
    private boolean intangible = false;
    private boolean reinforced = false;
    private boolean recheckStates = false;

    public FramedBlockEntity(BlockPos pos, BlockState state)
    {
        this(FramedBlocksAPI.getInstance().defaultBlockEntity(), pos, state);
    }

    protected FramedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public final InteractionResult handleInteraction(Player player, InteractionHand hand, BlockHitResult hit)
    {
        ItemStack stack = player.getItemInHand(hand);
        boolean secondary = hitSecondary(hit);
        CamoContainer camo = getCamo(secondary);

        if (camo.getType().isBlock() && stack.is(Utils.FRAMED_HAMMER.get()))
        {
            return clearBlockCamo(player, camo, stack, secondary);
        }
        else if (camo.getType().isFluid() && stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent())
        {
            return clearFluidCamo(player, camo, stack, secondary);
        }
        else if (camo.isEmpty() && stack.getItem() instanceof BlockItem)
        {
            return setBlockCamo(player, stack, secondary);
        }
        else if (camo.isEmpty() && stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent())
        {
            return setFluidCamo(player, stack, secondary);
        }
        else if (stack.is(Tags.Items.DUSTS_GLOWSTONE) && !glowing)
        {
            return applyGlowstone(player, stack);
        }
        else if (!camo.isEmpty() && !player.isShiftKeyDown() && stack.is(Utils.FRAMED_SCREWDRIVER.get()))
        {
            return rotateCamo(camo);
        }
        else if (!intangible && canMakeIntangible(stack))
        {
            return applyIntangibility(player, stack);
        }
        else if (intangible && player.isShiftKeyDown() && stack.is(Utils.FRAMED_SCREWDRIVER.get()))
        {
            return removeIntangibility(player);
        }
        else if (!reinforced && stack.is(Utils.FRAMED_REINFORCEMENT.get()))
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
        if (!FramedBlocksAPI.getInstance().enableIntangibility())
        {
            return false;
        }
        return stack.is(FramedBlocksAPI.getInstance().getIntangibilityMarkerItem()) && getBlockType().allowMakingIntangible();
    }

    private static boolean canRemoveReinforcement(ItemStack stack)
    {
        if (stack.getItem().canPerformAction(stack, ToolActions.PICKAXE_DIG))
        {
            return stack.isCorrectToolForDrops(Blocks.OBSIDIAN.defaultBlockState());
        }
        return false;
    }

    private InteractionResult clearBlockCamo(Player player, CamoContainer camo, ItemStack stack, boolean secondary)
    {
        //noinspection ConstantConditions
        if (!level.isClientSide())
        {
            ItemStack camoStack = camo.toItemStack(stack);
            if ((!player.isCreative() || !player.getInventory().contains(camoStack)) && !player.getInventory().add(camoStack))
            {
                player.drop(camoStack, false);
            }

            setCamo(EmptyCamoContainer.EMPTY, secondary);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private InteractionResult clearFluidCamo(Player player, CamoContainer camo, ItemStack stack, boolean secondary)
    {
        ItemStack input = stack.copy();
        input.setCount(1);

        ItemStack result = camo.toItemStack(input);
        if (!result.isEmpty())
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                if (!player.isCreative())
                {
                    // Container holds fluid in NBT -> stack doesn't change
                    if (result == input)
                    {
                        player.setItemInHand(InteractionHand.MAIN_HAND, result);
                    }
                    else // Container holds fluid by type (i.e. bucket) -> got a new stack
                    {
                        stack.shrink(1);
                        if (!player.getInventory().add(result))
                        {
                            player.drop(result, false);
                        }
                    }
                }

                setCamo(EmptyCamoContainer.EMPTY, secondary);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.FAIL;
    }

    private InteractionResult setBlockCamo(Player player, ItemStack stack, boolean secondary)
    {
        BlockState state = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
        if (state.getBlock() instanceof IFramedBlock)
        {
            return InteractionResult.FAIL;
        }

        if (isValidBlock(state, player))
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                CamoContainer.Factory factory = FramedBlocksAPI.getInstance().getCamoContainerFactory(stack);
                setCamo(factory.fromItem(stack), secondary);

                if (!player.isCreative())
                {
                    stack.shrink(1);
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.CONSUME;
    }

    private InteractionResult setFluidCamo(Player player, ItemStack stack, boolean secondary)
    {
        CamoContainer.Factory factory = FramedBlocksAPI.getInstance().getCamoContainerFactory(stack);
        CamoContainer camo = factory.fromItem(stack);
        if (!camo.isEmpty())
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                if (!player.isCreative())
                {
                    ItemStack result = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(handler ->
                    {
                        handler.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                        return handler.getContainer();
                    }).orElse(ItemStack.EMPTY);

                    // Container holds fluid by type (i.e. bucket) -> got a new stack
                    if (result != stack)
                    {
                        stack.shrink(1);
                        if (!result.isEmpty() && !player.getInventory().add(result))
                        {
                            player.drop(result, false);
                        }
                    }
                }

                setCamo(camo, secondary);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.CONSUME;
    }

    private InteractionResult applyGlowstone(Player player, ItemStack stack)
    {
        //noinspection ConstantConditions
        if (!level.isClientSide())
        {
            if (!player.isCreative())
            {
                stack.shrink(1);
            }

            int light = getLightValue();
            glowing = true;
            if (!updateDynamicStates(false, true, false))
            {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }

            boolean lightUpdate = getLightValue() != light;

            setChanged();
            if (lightUpdate)
            {
                doLightUpdate();
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private InteractionResult rotateCamo(CamoContainer camo)
    {
        if (camo.rotateCamo())
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.FAIL;
    }

    private InteractionResult applyIntangibility(Player player, ItemStack stack)
    {
        //noinspection ConstantConditions
        if (!level.isClientSide())
        {
            if (!player.isCreative())
            {
                stack.shrink(1);
            }

            setIntangible(true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private InteractionResult removeIntangibility(Player player)
    {
        //noinspection ConstantConditions
        if (!level.isClientSide())
        {
            setIntangible(false);

            ItemStack result = new ItemStack(FramedBlocksAPI.getInstance().getIntangibilityMarkerItem());
            if (!player.getInventory().add(result))
            {
                player.drop(result, false);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private InteractionResult applyReinforcement(Player player, ItemStack stack)
    {
        //noinspection ConstantConditions
        if (!level.isClientSide())
        {
            if (!player.isCreative())
            {
                stack.shrink(1);
            }

            setReinforced(true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private InteractionResult removeReinforcement(Player player, ItemStack stack, InteractionHand hand)
    {
        //noinspection ConstantConditions
        if (!level.isClientSide())
        {
            setReinforced(false);

            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

            ItemStack result = new ItemStack(Utils.FRAMED_REINFORCEMENT.get());
            if (!player.getInventory().add(result))
            {
                player.drop(result, false);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    protected final boolean isValidBlock(BlockState state, Player player)
    {
        Block block = state.getBlock();
        if (block instanceof IFramedBlock)
        {
            return false;
        }

        if (state.is(Utils.BLACKLIST))
        {
            if (player != null)
            {
                player.displayClientMessage(MSG_BLACKLISTED, true);
            }
            return false;
        }
        if (state.hasBlockEntity() && !FramedBlocksAPI.getInstance().allowBlockEntities() && !state.is(Utils.BE_WHITELIST))
        {
            if (player != null)
            {
                player.displayClientMessage(MSG_BLOCK_ENTITY, true);
            }
            return false;
        }

        //noinspection ConstantConditions
        return state.isSolidRender(level, worldPosition) || state.is(Utils.FRAMEABLE) || state.getBlock() instanceof LiquidBlock;
    }

    protected boolean hitSecondary(BlockHitResult hit)
    {
        return false;
    }

    public void setCamo(CamoContainer camo, boolean secondary)
    {
        int light = getLightValue();

        this.camoContainer = camo;

        setChanged();
        if (getLightValue() != light)
        {
            doLightUpdate();
        }

        if (!updateDynamicStates(true, true, true))
        {
            //noinspection ConstantConditions
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public boolean isSolidSide(Direction side)
    {
        if (camoContainer.isEmpty())
        {
            return false;
        }
        return getBlock().getFullFacePredicate().test(getBlockState(), side) && camoContainer.isSolid(level, worldPosition);
    }

    /**
     * Returns the camo for the given {@link BlockState}. Used for cases where different double blocks
     * with the same underlying shape(s) don't use the same side to return the camo for a given "sub-state".
     */
    public CamoContainer getCamo(BlockState state)
    {
        return camoContainer;
    }

    /**
     * Used to return a different camo depending on the given side
     * @param side The blocks face, can return EMPTY if the face does not pass the CTM_PREDICATE
     */
    public CamoContainer getCamo(Direction side)
    {
        return camoContainer;
    }

    /**
     * Returns the camo for the given edge of the given side
     */
    public CamoContainer getCamo(Direction side, @Nullable Direction edge)
    {
        return getCamo(side);
    }

    /**
     * Used to return a different camo depending on the exact interaction location
     */
    public CamoContainer getCamo(BlockHitResult hit)
    {
        return getCamo(hitSecondary(hit));
    }

    protected CamoContainer getCamo(boolean secondary)
    {
        return camoContainer;
    }

    public final CamoContainer getCamo()
    {
        return camoContainer;
    }

    protected boolean isCamoSolid()
    {
        return !camoContainer.isEmpty() && camoContainer.isSolid(level, worldPosition);
    }

    protected boolean doesCamoPropagateSkylightDown()
    {
        //noinspection ConstantConditions
        return camoContainer.getState().propagatesSkylightDown(level, worldPosition);
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
            //noinspection ConstantConditions
            level.setBlock(worldPosition, state, Block.UPDATE_ALL);
        }
        return changed;
    }

    @SuppressWarnings("ConstantConditions")
    public final void updateCulling(boolean neighbors, boolean rerender)
    {
        boolean changed = false;
        for (Direction dir : DIRECTIONS)
        {
            changed |= updateCulling(dir, false);
            if (neighbors && level.getBlockEntity(worldPosition.relative(dir)) instanceof FramedBlockEntity be)
            {
                be.updateCulling(dir.getOpposite(), true);
            }
        }

        if (rerender && changed)
        {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public boolean updateCulling(Direction side, boolean rerender)
    {
        return updateCulling(modelData, getBlockState(), side, rerender);
    }

    protected final boolean updateCulling(
            FramedBlockData modelData, BlockState testState, Direction side, boolean rerender
    )
    {
        boolean wasHidden = modelData.isSideHidden(side);
        //noinspection ConstantConditions
        boolean hidden = ((IFramedBlock) testState.getBlock()).isSideHidden(level, worldPosition, testState, side);
        if (wasHidden != hidden)
        {
            modelData.setSideHidden(side, hidden);
            if (rerender)
            {
                //noinspection ConstantConditions
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
            return true;
        }
        return false;
    }

    public float getCamoExplosionResistance(Explosion explosion)
    {
        float camoRes = camoContainer.getState().getExplosionResistance(level, worldPosition, explosion);
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
        return camoContainer.isEmpty() || camoContainer.getState().isFlammable(level, worldPosition, face);
    }

    public int getCamoFlammability(Direction face)
    {
        if (reinforced)
        {
            return 0;
        }
        return camoContainer.isEmpty() ? -1 : camoContainer.getState().getFlammability(level, worldPosition, face);
    }

    public int getCamoFireSpreadSpeed(Direction face)
    {
        if (reinforced)
        {
            return 0;
        }
        return camoContainer.isEmpty() ? -1 : camoContainer.getState().getFireSpreadSpeed(level, worldPosition, face);
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

            setChanged();
            //noinspection ConstantConditions
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public final boolean isGlowing()
    {
        return glowing;
    }

    public int getLightValue()
    {
        return glowing ? 15 : camoContainer.getState().getLightEmission();
    }

    public void setIntangible(boolean intangible)
    {
        if (this.intangible != intangible)
        {
            this.intangible = intangible;

            setChanged();

            if (!updateDynamicStates(true, false, false))
            {
                //noinspection ConstantConditions
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    public boolean isIntangible(CollisionContext ctx)
    {
        if (!FramedBlocksAPI.getInstance().enableIntangibility() || !intangible)
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

            //noinspection ConstantConditions
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            setChanged();
        }
    }

    public boolean isReinforced()
    {
        return reinforced;
    }

    protected final void doLightUpdate()
    {
        //noinspection ConstantConditions
        level.getChunkSource().getLightEngine().checkBlock(worldPosition);
    }

    public final IFramedBlock getBlock()
    {
        return (IFramedBlock) getBlockState().getBlock();
    }

    public final IBlockType getBlockType()
    {
        return getBlock().getBlockType();
    }

    public boolean canAutoApplyCamoOnPlacement()
    {
        return true;
    }

    public void addCamoDrops(List<ItemStack> drops)
    {
        if (!camoContainer.isEmpty())
        {
            drops.add(camoContainer.toItemStack(ItemStack.EMPTY));
        }
        if (reinforced)
        {
            drops.add(new ItemStack(Utils.FRAMED_REINFORCEMENT.get()));
        }
    }

    public MapColor getMapColor()
    {
        return camoContainer.getMapColor(level, worldPosition);
    }

    public float[] getCamoBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        if (!camoContainer.isEmpty())
        {
            return camoContainer.getBeaconColorMultiplier(level, pos, beaconPos);
        }
        return null;
    }

    public boolean shouldCamoDisplayFluidOverlay(BlockAndTintGetter level, BlockPos pos, FluidState fluid)
    {
        return camoContainer.isEmpty() || camoContainer.getState().shouldDisplayFluidOverlay(level, pos, fluid);
    }

    @Override
    public void onLoad()
    {
        //noinspection ConstantConditions
        if (!level.isClientSide() && recheckStates)
        {
            checkCamoSolid();
        }
        super.onLoad();
    }

    /*
     * Special handling for connected textures
     */

    @Nullable
    public BlockState getComponentBySkipPredicate(BlockGetter ctLevel, BlockState neighborState, Direction side)
    {
        return getBlockState();
    }

    /**
     * Extract the nested {@link ModelData}, if any, from the given data based on the given state,
     * only relevant for double blocks
     */
    public ModelData getModelData(ModelData data, BlockState state)
    {
        return data;
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
            //noinspection ConstantConditions
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            requestModelDataUpdate();
        }
    }

    protected void writeToDataPacket(CompoundTag nbt)
    {
        nbt.put("camo", CamoContainer.writeToNetwork(camoContainer));
        nbt.putByte("flags", writeFlags());
    }

    protected boolean readFromDataPacket(CompoundTag nbt)
    {
        boolean needUpdate = false;
        boolean needCullingUpdate = false;

        CamoContainer newCamo = CamoContainer.readFromNetwork(nbt.getCompound("camo"));
        if (!newCamo.equals(camoContainer))
        {
            int oldLight = getLightValue();
            camoContainer = newCamo;
            if (oldLight != getLightValue()) { doLightUpdate(); }

            modelData.setCamoState(camoContainer.getState());

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

        boolean newReinforced = nbt.getBoolean("reinforced");
        if (newReinforced != reinforced)
        {
            reinforced = newReinforced;
            modelData.setReinforced(reinforced);
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

        nbt.put("camo", CamoContainer.writeToNetwork(camoContainer));
        nbt.putByte("flags", writeFlags());

        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt)
    {
        CamoContainer newCamo = CamoContainer.readFromNetwork(nbt.getCompound("camo"));
        if (!newCamo.equals(camoContainer))
        {
            camoContainer = newCamo;

            modelData.setCamoState(camoContainer.getState());

            ClientUtils.enqueueClientTask(() -> updateCulling(true, true));
        }

        byte flags = nbt.getByte("flags");
        glowing = readFlag(flags, FLAG_GLOWING);
        intangible = readFlag(flags, FLAG_INTANGIBLE);

        boolean newReinforced = readFlag(flags, FLAG_REINFORCED);
        if (newReinforced != reinforced)
        {
            reinforced = newReinforced;
            modelData.setReinforced(reinforced);
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
    public ModelData getModelData()
    {
        return ModelData.builder()
                .with(FramedBlockData.PROPERTY, modelData)
                .build();
    }

    protected final FramedBlockData getModelDataInternal()
    {
        return modelData;
    }

    protected void initModelData()
    {
        modelData.setCamoState(camoContainer.getState());
    }

    @Override
    public void setLevel(Level level)
    {
        super.setLevel(level);
        if (level.isClientSide())
        {
            //Try initializing model data early to make it work on Create contraptions
            initModelData();
        }
    }

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
        nbt.put("camo", CamoContainer.save(camoContainer));
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

        CamoContainer camo = CamoContainer.load(nbt.getCompound("camo"));
        if (camo.isEmpty() || isValidBlock(camo.getState(), null))
        {
            recheckStates = nbt.getByte("updated") < DATA_VERSION;
            camoContainer = camo;
        }
        else
        {
            recheckStates = true;
            LOGGER.warn(
                    "Framed Block of type \"{}\" at position {} contains an invalid camo of type \"{}\", removing camo! This might be caused by a config or tag change!",
                    ForgeRegistries.BLOCKS.getKey(getBlockState().getBlock()),
                    worldPosition,
                    ForgeRegistries.BLOCKS.getKey(camo.getState().getBlock())
            );
        }
        glowing = nbt.getBoolean("glowing");
        intangible = nbt.getBoolean("intangible");
        reinforced = nbt.getBoolean("reinforced");
    }
}
package xfacthd.framedblocks.api.block;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.*;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.slf4j.Logger;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.util.client.ClientUtils;

import java.util.List;

@SuppressWarnings("deprecation")
public class FramedBlockEntity extends BlockEntity
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final TranslatableComponent MSG_BLACKLISTED = Utils.translate("msg", "blacklisted");
    public static final TranslatableComponent MSG_BLOCK_ENTITY = Utils.translate("msg", "block_entity");
    private static final Direction[] DIRECTIONS = Direction.values();

    private final FramedBlockData modelData = new FramedBlockData(false);
    private ItemStack camoStack = ItemStack.EMPTY;
    private BlockState camoState = Blocks.AIR.defaultBlockState();
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
        BlockState camo = getCamoState(hit);
        if (!camo.isAir() && !(camo.getBlock() instanceof LiquidBlock) && stack.is(Utils.FRAMED_HAMMER.get()))
        {
            return clearBlockCamo(player, hit);
        }
        else if (!camo.isAir() && camo.getBlock() instanceof LiquidBlock && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent())
        {
            if (true) { return InteractionResult.PASS; }
            return clearFluidCamo(player, camo, stack, hit);
        }
        else if (camo.isAir() && stack.getItem() instanceof BlockItem)
        {
            return setBlockCamo(player, stack, hit);
        }
        else if (camo.isAir() && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent())
        {
            if (true) { return InteractionResult.PASS; }
            return setFluidCamo(player, stack, hit);
        }
        else if (stack.is(Tags.Items.DUSTS_GLOWSTONE) && !glowing)
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                if (!player.isCreative()) { stack.shrink(1); }

                int light = getLightValue();
                glowing = true;
                if (!updateDynamicStates(false, true))
                {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                }

                boolean lightUpdate = getLightValue() != light;

                setChanged();
                if (lightUpdate) { doLightUpdate(); }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        else if (!camo.isAir() && !player.isShiftKeyDown() && stack.is(Utils.FRAMED_SCREWDRIVER.get()))
        {
            return rotateCamo(camo, hit);
        }
        else if (!intangible && canMakeIntangible(stack))
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                if (!player.isCreative()) { stack.shrink(1); }

                setIntangible(true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        else if (intangible && player.isShiftKeyDown() && stack.is(Utils.FRAMED_SCREWDRIVER.get()))
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
        else if (!reinforced && stack.is(Utils.FRAMED_REINFORCEMENT.get()))
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                if (!player.isCreative()) { stack.shrink(1); }

                setReinforced(true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        else if (reinforced && canRemoveReinforcement(stack))
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

    private InteractionResult clearBlockCamo(Player player, BlockHitResult hit)
    {
        //noinspection ConstantConditions
        if (!level.isClientSide())
        {
            int light = getLightValue();

            ItemStack camoStack = getCamoStack(hit);
            if ((!player.isCreative() || !player.getInventory().contains(camoStack)) && !player.getInventory().add(camoStack))
            {
                player.drop(camoStack, false);
            }

            applyCamo(ItemStack.EMPTY, Blocks.AIR.defaultBlockState(), hit);
            if (!updateDynamicStates(true, true))
            {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }

            boolean lightUpdate = getLightValue() != light;

            setChanged();
            if (lightUpdate) { doLightUpdate(); }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private InteractionResult clearFluidCamo(Player player, BlockState camo, ItemStack stack, BlockHitResult hit)
    {
        LazyOptional<IFluidHandlerItem> cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
        return cap.map(handler ->
        {
            FluidStack fluid = new FluidStack(((LiquidBlock) camo.getBlock()).getFluid(), 1000);
            if (handler.fill(fluid, IFluidHandler.FluidAction.SIMULATE) == 1000)
            {
                //noinspection ConstantConditions
                if (!level.isClientSide())
                {
                    if (!player.isCreative())
                    {
                        if (stack.getItem() == Items.BUCKET)
                        {
                            stack.shrink(1);

                            ItemStack result = new ItemStack(fluid.getFluid().getBucket());
                            if (!player.getInventory().add(result))
                            {
                                player.drop(result, false);
                            }
                        }
                        else
                        {
                            handler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }

                    int light = getLightValue();
                    applyCamo(ItemStack.EMPTY, Blocks.AIR.defaultBlockState(), hit);
                    if (!updateDynamicStates(false, true))
                    {
                        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                    }
                    boolean lightUpdate = getLightValue() != light;

                    setChanged();
                    if (lightUpdate) { doLightUpdate(); }
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
            return InteractionResult.FAIL;
        }).orElse(InteractionResult.FAIL);
    }

    private InteractionResult setBlockCamo(Player player, ItemStack stack, BlockHitResult hit)
    {
        BlockState state = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
        if (isValidBlock(state, player))
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                int light = getLightValue();

                applyCamo(stack.split(1), state, hit);
                if (!updateDynamicStates(true, true))
                {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                }
                if (player.isCreative()) { stack.grow(1); }

                setChanged();
                if (getLightValue() != light) { doLightUpdate(); }
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.FAIL;
    }

    private InteractionResult setFluidCamo(Player player, ItemStack stack, BlockHitResult hit)
    {
        LazyOptional<IFluidHandlerItem> cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
        return cap.map(handler ->
        {
            FluidStack fluid = handler.getFluidInTank(0);

            //TODO: sides are not hidden when next to a "falling" fluid block
            BlockState state = fluid.getFluid().defaultFluidState().createLegacyBlock().setValue(BlockStateProperties.LEVEL, 8);
            if (!state.isAir())
            {
                ItemStack bucket = new ItemStack(fluid.getFluid().getBucket());
                if (fluid.getAmount() >= 1000 && !bucket.isEmpty() && handler.drain(1000, IFluidHandler.FluidAction.SIMULATE).getAmount() == 1000)
                {
                    //noinspection ConstantConditions
                    if (!level.isClientSide())
                    {
                        if (!player.isCreative())
                        {
                            if (stack.getItem() instanceof BucketItem)
                            {
                                stack.shrink(1);

                                ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                                if (!player.getInventory().add(emptyBucket))
                                {
                                    player.drop(emptyBucket, false);
                                }
                            }
                            else
                            {
                                handler.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                            }
                        }

                        int light = getLightValue();

                        //Setting the bucket as the camo stack would allow duping buckets
                        applyCamo(ItemStack.EMPTY, state, hit);
                        if (!updateDynamicStates(false, true))
                        {
                            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                        }

                        setChanged();
                        if (getLightValue() != light) { doLightUpdate(); }
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }
            return InteractionResult.FAIL;
        }).orElse(InteractionResult.FAIL);
    }

    private InteractionResult rotateCamo(BlockState camo, BlockHitResult hit)
    {
        Property<?> prop = getRotatableProperty(camo);
        if (prop != null)
        {
            //noinspection ConstantConditions
            if (!level.isClientSide())
            {
                applyCamo(getCamoStack(hit), camo.cycle(prop), hit);

                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.FAIL;
    }

    private static Property<?> getRotatableProperty(BlockState state)
    {
        for (Property<?> prop : state.getProperties())
        {
            if (prop.getValueClass() == Direction.Axis.class)
            {
                return prop;
            }
            else if (prop instanceof DirectionProperty)
            {
                return prop;
            }
        }
        return null;
    }

    protected final boolean isValidBlock(BlockState state, Player player)
    {
        Block block = state.getBlock();
        if (block instanceof IFramedBlock) { return false; }

        if (state.is(Utils.BLACKLIST))
        {
            if (player != null)
            {
                player.displayClientMessage(MSG_BLACKLISTED, true);
            }
            return false;
        }
        if (state.hasBlockEntity() && !FramedBlocksAPI.getInstance().allowBlockEntities())
        {
            if (player != null)
            {
                player.displayClientMessage(MSG_BLOCK_ENTITY, true);
            }
            return false;
        }

        //noinspection ConstantConditions
        return state.isSolidRender(level, worldPosition) || state.is(Utils.FRAMEABLE);
    }

    protected void applyCamo(ItemStack camoStack, BlockState camoState, BlockHitResult hit)
    {
        this.camoStack = camoStack;
        this.camoState = camoState;
    }

    public void setCamo(ItemStack camoStack, BlockState camoState, boolean secondary)
    {
        int light = getLightValue();

        this.camoStack = camoStack;
        this.camoState = camoState;

        setChanged();
        if (getLightValue() != light)
        {
            doLightUpdate();
        }
        //noinspection ConstantConditions
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public boolean isSolidSide(Direction side)
    {
        //noinspection ConstantConditions
        return getBlock().getCtmPredicate().test(getBlockState(), side) && camoState.isSolidRender(level, worldPosition);
    }

    /**
     * Returns the camo for the given {@link BlockState}. Used for cases where different double blocks
     * with the same underlying shape(s) don't use the same side to return the camo for a given "sub-state".
     */
    public BlockState getCamoState(BlockState state) { return camoState; }

    /**
     * Used to return a different camo state depending on the given side
     * @param side The blocks face, can return AIR if the face does not pass the CTM_PREDICATE
     */
    public BlockState getCamoState(Direction side) { return camoState; }

    protected BlockState getCamoState(BlockHitResult hit) { return camoState; }

    public final BlockState getCamoState() { return camoState; }

    protected ItemStack getCamoStack(BlockHitResult hit) { return camoStack; }

    public final ItemStack getCamoStack() { return camoStack; }

    protected boolean isCamoSolid()
    {
        //noinspection ConstantConditions
        return !camoState.isAir() && camoState.isSolidRender(level, worldPosition);
    }

    public final void checkCamoSolid()
    {
        if (getBlock().getBlockType().canOccludeWithSolidCamo() && !camoState.isAir())
        {
            updateDynamicStates(true, false);
        }
    }

    protected final boolean updateDynamicStates(boolean updateSolid, boolean updateLight)
    {
        if (!getBlock().getBlockType().canOccludeWithSolidCamo()) { return false; }

        BlockState state = getBlockState();
        boolean changed = false;

        if (updateSolid)
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

    protected final boolean updateCulling(FramedBlockData modelData, BlockState testState, Direction side, boolean rerender)
    {
        boolean wasHidden = modelData.isSideHidden(side);
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
        float camoRes = camoState.getExplosionResistance(level, worldPosition, explosion);
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
        return camoState.isAir() || camoState.isFlammable(level, worldPosition, face);
    }

    public int getCamoFlammability(Direction face)
    {
        if (reinforced)
        {
            return 0;
        }
        return getCamoState().isAir() ? -1 : getCamoState().getFlammability(level, worldPosition, face);
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

    public final boolean isGlowing() { return glowing; }

    public int getLightValue()
    {
        if (glowing) { return 15; }
        return camoState.getLightEmission();
    }

    public void setIntangible(boolean intangible)
    {
        if (this.intangible != intangible)
        {
            this.intangible = intangible;

            setChanged();

            if (!updateDynamicStates(true, false))
            {
                //noinspection ConstantConditions
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    public boolean isIntangible(CollisionContext ctx)
    {
        if (!FramedBlocksAPI.getInstance().enableIntangibility() || !intangible) { return false; }

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

    public final IFramedBlock getBlock() { return (IFramedBlock) getBlockState().getBlock(); }

    public final IBlockType getBlockType() { return getBlock().getBlockType(); }

    public void addCamoDrops(List<ItemStack> drops)
    {
        if (!camoStack.isEmpty())
        {
            drops.add(camoStack);
        }
        if (reinforced)
        {
            drops.add(new ItemStack(Utils.FRAMED_REINFORCEMENT.get()));
        }
    }

    public MaterialColor getMapColor()
    {
        if (!camoState.isAir())
        {
            //noinspection ConstantConditions
            return camoState.getMapColor(level, worldPosition);
        }
        return null;
    }

    public float[] getCamoBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        if (!camoState.isAir())
        {
            return camoState.getBeaconColorMultiplier(level, pos, beaconPos);
        }
        return null;
    }

    public boolean shouldCamoDisplayFluidOverlay(BlockAndTintGetter level, BlockPos pos, FluidState fluid)
    {
        return camoState.isAir() || camoState.shouldDisplayFluidOverlay(level, pos, fluid);
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
     * Sync
     */

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public final void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
    {
        CompoundTag nbt = pkt.getTag();
        if (nbt != null && readFromDataPacket(nbt))
        {
            //noinspection ConstantConditions
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    protected void writeToDataPacket(CompoundTag nbt)
    {
        nbt.put("camo_stack", camoStack.save(new CompoundTag()));
        nbt.put("camo_state", NbtUtils.writeBlockState(camoState));
        nbt.putBoolean("glowing", glowing);
        nbt.putBoolean("intangible", intangible);
        nbt.putBoolean("reinforced", reinforced);
    }

    protected boolean readFromDataPacket(CompoundTag nbt)
    {
        camoStack = ItemStack.of(nbt.getCompound("camo_stack"));

        boolean needUpdate = false;
        boolean needCullingUpdate = false;
        BlockState newState = NbtUtils.readBlockState(nbt.getCompound("camo_state"));
        if (newState != camoState)
        {
            int oldLight = getLightValue();
            camoState = newState;
            if (oldLight != getLightValue()) { doLightUpdate(); }

            modelData.setCamoState(camoState);

            needUpdate = true;
            needCullingUpdate = true;
        }

        boolean newGlow = nbt.getBoolean("glowing");
        if (newGlow != glowing)
        {
            glowing = newGlow;
            needUpdate = true;

            doLightUpdate();
        }

        boolean newIntangible = nbt.getBoolean("intangible");
        if (newIntangible != intangible)
        {
            intangible = newIntangible;
            needUpdate = true;
            needCullingUpdate = true;
        }

        reinforced = nbt.getBoolean("reinforced");

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

        nbt.put("camo_stack", camoStack.save(new CompoundTag()));
        nbt.put("camo_state", NbtUtils.writeBlockState(camoState));
        nbt.putBoolean("glowing", glowing);
        nbt.putBoolean("intangible", intangible);
        nbt.putBoolean("reinforced", reinforced);

        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt)
    {
        camoStack = ItemStack.of(nbt.getCompound("camo_stack"));

        BlockState newState = NbtUtils.readBlockState(nbt.getCompound("camo_state"));
        if (newState != camoState)
        {
            camoState = newState;

            modelData.setCamoState(camoState);

            ClientUtils.enqueueClientTask(() -> updateCulling(true, true));
        }

        glowing = nbt.getBoolean("glowing");
        intangible = nbt.getBoolean("intangible");
        reinforced = nbt.getBoolean("reinforced");
    }

    /*
     * Model data
     */

    @Override
    public IModelData getModelData() { return modelData; }

    protected final FramedBlockData getModelDataInternal() { return modelData; }

    protected void initModelData() { modelData.setCamoState(camoState); }

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

    public CompoundTag writeToBlueprint() { return saveWithoutMetadata(); }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.put("camo_stack", camoStack.save(new CompoundTag()));
        nbt.put("camo_state", NbtUtils.writeBlockState(camoState));
        nbt.putBoolean("glowing", glowing);
        nbt.putBoolean("intangible", intangible);
        nbt.putBoolean("reinforced", reinforced);
        nbt.putByte("updated", (byte) 2);

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        BlockState state = NbtUtils.readBlockState(nbt.getCompound("camo_state"));
        if (state.isAir() || isValidBlock(state, null))
        {
            recheckStates = nbt.getByte("updated") < 2;
            camoStack = ItemStack.of(nbt.getCompound("camo_stack"));
            camoState = state;
        }
        else
        {
            recheckStates = true;
            LOGGER.warn(
                    "Framed Block of type \"{}\" at position {} contains an invalid camo of type \"{}\", removing camo! This might be caused by a config or tag change!",
                    getBlockState().getBlock().getRegistryName(),
                    worldPosition,
                    state.getBlock().getRegistryName()
            );
        }
        glowing = nbt.getBoolean("glowing");
        intangible = nbt.getBoolean("intangible");
        reinforced = nbt.getBoolean("reinforced");
    }
}
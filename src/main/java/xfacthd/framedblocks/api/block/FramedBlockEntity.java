package xfacthd.framedblocks.api.block;

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
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.*;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.util.FramedBlockData;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;

@SuppressWarnings("deprecation")
public class FramedBlockEntity extends BlockEntity
{
    public static final TranslatableComponent MSG_BLACKLISTED = Utils.translate("msg", "blacklisted");
    public static final TranslatableComponent MSG_BLOCK_ENTITY = Utils.translate("msg", "block_entity");

    private final FramedBlockData modelData = new FramedBlockData(false);
    private ItemStack camoStack = ItemStack.EMPTY;
    private BlockState camoState = Blocks.AIR.defaultBlockState();
    private boolean glowing = false;

    public FramedBlockEntity(BlockPos pos, BlockState state) { this(FramedBlocksAPI.getInstance().defaultBlockEntity(), pos, state); }

    protected FramedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    public final InteractionResult handleInteraction(Player player, InteractionHand hand, BlockHitResult hit)
    {
        ItemStack stack = player.getItemInHand(hand);
        BlockState camo = getCamoState(hit);
        if (!camo.isAir() && !(camo.getBlock() instanceof LiquidBlock) && FramedBlocksAPI.getInstance().isFramedHammer(stack))
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

                glowing = true;

                setChanged();
                doLightUpdate();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        else if (!camo.isAir() && stack.is(Utils.WRENCH))
        {
            return rotateCamo(camo, hit);
        }

        return InteractionResult.PASS;
    }

    private InteractionResult clearBlockCamo(Player player, BlockHitResult hit)
    {
        //noinspection ConstantConditions
        if (!level.isClientSide())
        {
            int light = getLightValue();

            ItemStack camoStack = getCamoStack(hit);
            if (!player.getInventory().add(camoStack))
            {
                player.drop(camoStack, false);
            }

            applyCamo(ItemStack.EMPTY, Blocks.AIR.defaultBlockState(), hit);

            boolean lightUpdate = getLightValue() != light;

            setChanged();
            if (lightUpdate) { doLightUpdate(); }
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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
                    boolean lightUpdate = getLightValue() != light;

                    setChanged();
                    if (lightUpdate) { doLightUpdate(); }
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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
                if (player.isCreative()) { stack.grow(1); }

                setChanged();
                if (getLightValue() != light) { doLightUpdate(); }
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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

                        setChanged();
                        if (getLightValue() != light) { doLightUpdate(); }
                        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public boolean isSolidSide(Direction side)
    {
        return getBlock().getCtmPredicate().test(getBlockState(), side) && camoState.canOcclude();
    }

    /**
     * Used to return a different camo state depending on the given side
     * @param side The blocks face, can return AIR if the face does not pass the CTM_PREDICATE
     */
    public BlockState getCamoState(Direction side) { return camoState; }

    protected BlockState getCamoState(BlockHitResult hit) { return camoState; }

    public final BlockState getCamoState() { return camoState; }

    protected ItemStack getCamoStack(BlockHitResult hit) { return camoStack; }

    public final ItemStack getCamoStack() { return camoStack; }

    public float getCamoExplosionResistance(Explosion explosion)
    {
        return camoState.getExplosionResistance(level, worldPosition, explosion);
    }

    public boolean isCamoFlammable(Direction face)
    {
        return camoState.isAir() || camoState.isFlammable(level, worldPosition, face);
    }

    public int getCamoFlammability(Direction face)
    {
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
            //noinspection ConstantConditions
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public final boolean isGlowing() { return glowing; }

    public int getLightValue()
    {
        if (glowing) { return 15; }
        return camoState.getLightEmission();
    }

    protected final void doLightUpdate()
    {
        //noinspection ConstantConditions
        level.getChunkSource().getLightEngine().checkBlock(worldPosition);
    }

    public final IFramedBlock getBlock() { return (IFramedBlock) getBlockState().getBlock(); }

    public void addCamoDrops(List<ItemStack> drops)
    {
        if (!camoStack.isEmpty())
        {
            drops.add(camoStack);
        }
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
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    protected void writeToDataPacket(CompoundTag nbt)
    {
        nbt.put("camo_stack", camoStack.save(new CompoundTag()));
        nbt.put("camo_state", NbtUtils.writeBlockState(camoState));
        nbt.putBoolean("glowing", glowing);
    }

    protected boolean readFromDataPacket(CompoundTag nbt)
    {
        camoStack = ItemStack.of(nbt.getCompound("camo_stack"));

        boolean needUpdate = false;
        BlockState newState = NbtUtils.readBlockState(nbt.getCompound("camo_state"));
        if (newState != camoState)
        {
            int oldLight = getLightValue();
            camoState = newState;
            if (oldLight != getLightValue()) { doLightUpdate(); }

            modelData.setLevel(level);
            modelData.setPos(worldPosition);
            modelData.setCamoState(camoState);

            needUpdate = true;
        }

        boolean newGlow = nbt.getBoolean("glowing");
        if (newGlow != glowing)
        {
            glowing = newGlow;
            needUpdate = true;

            doLightUpdate();
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

            modelData.setLevel(level);
            modelData.setPos(worldPosition);
            modelData.setCamoState(camoState);
        }

        glowing = nbt.getBoolean("glowing");
    }

    /*
     * Model data
     */

    @Override
    public IModelData getModelData() { return modelData; }

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

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        BlockState state = NbtUtils.readBlockState(nbt.getCompound("camo_state"));
        if (state.isAir() || isValidBlock(state, null))
        {
            camoStack = ItemStack.of(nbt.getCompound("camo_stack"));
            camoState = state;
        }
        else
        {
            FramedBlocks.LOGGER.warn(
                    "Framed Block of type \"{}\" at position {} contains an invalid camo of type \"{}\", removing camo! This might be caused by a config or tag change!",
                    getBlockState().getBlock().getRegistryName(),
                    worldPosition,
                    state.getBlock().getRegistryName()
            );
        }
        glowing = nbt.getBoolean("glowing");
    }
}
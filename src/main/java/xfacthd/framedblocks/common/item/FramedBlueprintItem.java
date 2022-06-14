package xfacthd.framedblocks.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.*;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.data.FramedToolType;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;

import javax.annotation.Nullable;
import java.util.*;

public class FramedBlueprintItem extends FramedToolItem
{
    public static final String CONTAINED_BLOCK = "desc.framed_blocks:blueprint_block";
    public static final String CAMO_BLOCK = "desc.framed_blocks:blueprint_camo";
    public static final String IS_ILLUMINATED = "desc.framed_blocks:blueprint_illuminated";
    public static final IFormattableTextComponent BLOCK_NONE = new TranslationTextComponent("desc.framed_blocks:blueprint_none").withStyle(TextFormatting.RED);
    public static final IFormattableTextComponent BLOCK_INVALID = new TranslationTextComponent("desc.framed_blocks:blueprint_invalid").withStyle(TextFormatting.RED);
    public static final IFormattableTextComponent ILLUMINATED_FALSE = new TranslationTextComponent("desc.framed_blocks:blueprint_illuminated_false").withStyle(TextFormatting.RED);
    public static final IFormattableTextComponent ILLUMINATED_TRUE = new TranslationTextComponent("desc.framed_blocks:blueprint_illuminated_true").withStyle(TextFormatting.GREEN);
    public static final IFormattableTextComponent CANT_COPY = new TranslationTextComponent("desc.framed_blocks:blueprint_cant_copy").withStyle(TextFormatting.RED);

    public FramedBlueprintItem(FramedToolType type) { super(type); }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) { return false; }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown())
        {
            if (!world.isClientSide())
            {
                CompoundNBT tag = stack.getOrCreateTagElement("blueprint_data");
                tag.remove("framed_block");
                tag.remove("camo_data");
                tag.remove("camo_data_two");
            }
            return ActionResult.sidedSuccess(stack, world.isClientSide());
        }
        return super.use(world, player, hand);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        PlayerEntity player = context.getPlayer();
        if (player == null) { return ActionResultType.FAIL; }

        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        CompoundNBT tag = context.getItemInHand().getOrCreateTagElement("blueprint_data");

        if (player.isShiftKeyDown())
        {
            return writeBlueprint(world, pos, tag);
        }
        else if (!tag.isEmpty())
        {
            return readBlueprint(context, player, tag);
        }
        return super.useOn(context);
    }

    private ActionResultType writeBlueprint(World world, BlockPos pos, CompoundNBT tag)
    {
        TileEntity te = world.getBlockEntity(pos);
        if (!(te instanceof FramedTileEntity))
        {
            return ActionResultType.FAIL;
        }

        if (!world.isClientSide())
        {
            BlockState state = world.getBlockState(pos);
            //noinspection ConstantConditions
            String block = state.getBlock().getRegistryName().toString();
            tag.putString("framed_block", block);

            CompoundNBT nbt = ((FramedTileEntity)te).writeToBlueprint();
            if (state.getBlock() == FBContent.blockFramedDoor.get())
            {
                boolean top = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER;
                BlockPos posTwo = top ? pos.below() : pos.above();
                TileEntity teTwo = world.getBlockEntity(posTwo);
                CompoundNBT nbtTwo = teTwo instanceof FramedTileEntity ? ((FramedTileEntity)teTwo).writeToBlueprint() : new CompoundNBT();

                tag.put("camo_data", top ? nbtTwo : nbt);
                tag.put("camo_data_two", top ? nbt : nbtTwo);
            }
            else
            {
                tag.put("camo_data", nbt);
            }
        }
        return ActionResultType.sidedSuccess(world.isClientSide());
    }

    private ActionResultType readBlueprint(ItemUseContext context, PlayerEntity player, CompoundNBT tag)
    {
        Block block = getTargetBlock(context.getItemInHand());

        //noinspection deprecation
        if (block.defaultBlockState().isAir())
        {
            return ActionResultType.FAIL;
        }

        Item item = block.asItem();
        if (!(item instanceof BlockItem))
        {
            return ActionResultType.FAIL;
        }

        if (checkMissingMaterials(player, item, tag))
        {
            return ActionResultType.FAIL;
        }

        return tryPlace(context, player, item, tag);
    }

    private boolean checkMissingMaterials(PlayerEntity player, Item item, CompoundNBT tag)
    {
        if (player.abilities.instabuild) { return false; } //Creative mode can always build

        CompoundNBT camoData = tag.getCompound("camo_data");

        ItemStack camo = ItemStack.of(camoData.getCompound("camo_stack"));
        ItemStack camoTwo = camoData.contains("camo_stack_two") ? ItemStack.of(camoData.getCompound("camo_stack_two")) : ItemStack.EMPTY;
        boolean glowstone = tag.getCompound("camo_data").getBoolean("glowing");

        boolean doubleBlock = false;
        if (item == FBContent.blockFramedDoor.get().asItem() && tag.contains("camo_data_two"))
        {
            camoTwo = ItemStack.of(tag.getCompound("camo_data_two").getCompound("camo_stack"));
        }
        else if (item == FBContent.blockFramedDoublePanel.get().asItem())
        {
            item = FBContent.blockFramedPanel.get().asItem();
            doubleBlock = true;
        }
        else if (item == FBContent.blockFramedDoubleSlab.get().asItem())
        {
            item = FBContent.blockFramedSlab.get().asItem();
            doubleBlock = true;
        }

        if (doubleBlock)
        {
            int count = player.inventory.countItem(item);
            if (count < 2) { return true; }
        }
        else
        {
            if (!player.inventory.contains(new ItemStack(item))) { return true; }
        }

        if (!camo.isEmpty() && camo.getItem() == camoTwo.getItem())
        {
            int count = player.inventory.countItem(camo.getItem());
            if (count < 2) { return true; }
        }
        else
        {
            if (!camo.isEmpty() && !player.inventory.contains(camo)) { return true; }
            if (!camoTwo.isEmpty() && !player.inventory.contains(camoTwo)) { return true; }
        }

        return glowstone && !player.inventory.contains(Tags.Items.DUSTS_GLOWSTONE);
    }

    private ActionResultType tryPlace(ItemUseContext context, PlayerEntity player, Item item, CompoundNBT tag)
    {
        ItemStack dummyStack = new ItemStack(item, 1);
        dummyStack.getOrCreateTag().put("BlockEntityTag", tag.getCompound("camo_data").copy());

        ItemUseContext placeContext = new ItemUseContext(
                context.getLevel(),
                context.getPlayer(),
                context.getHand(),
                dummyStack,
                new BlockRayTraceResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside())
        );
        //Needs to happen before placing to make sure we really get the target pos, especially in case of replacing stuff like grass
        BlockPos topPos = new BlockItemUseContext(placeContext).getClickedPos().above();
        ActionResultType result = item.useOn(placeContext);

        if (!context.getLevel().isClientSide() && result.consumesAction())
        {
            if (item == FBContent.blockFramedDoor.get().asItem())
            {
                if (context.getLevel().getBlockEntity(topPos) instanceof FramedTileEntity && tag.contains("camo_data_two", Constants.NBT.TAG_COMPOUND))
                {
                    //noinspection ConstantConditions
                    dummyStack.getOrCreateTag().put("BlockEntityTag", tag.get("camo_data_two"));
                    BlockItem.updateCustomBlockEntityTag(context.getLevel(), context.getPlayer(), topPos, dummyStack);
                }
            }

            if (!player.abilities.instabuild)
            {
                consumeItems(player, item, tag);
            }
        }

        return result;
    }

    private void consumeItems(PlayerEntity player, Item item, CompoundNBT tag)
    {
        CompoundNBT camoData = tag.getCompound("camo_data");

        ItemStack camo = ItemStack.of(camoData.getCompound("camo_stack"));
        ItemStack camoTwo = camoData.contains("camo_stack_two") ? ItemStack.of(camoData.getCompound("camo_stack_two")) : ItemStack.EMPTY;
        boolean glowstone = camoData.getBoolean("glowing");

        boolean doubleBlock = false;
        if (item == FBContent.blockFramedDoor.get().asItem() && tag.contains("camo_data_two"))
        {
            camoTwo = ItemStack.of(tag.getCompound("camo_data_two").getCompound("camo_stack"));
        }
        else if (item == FBContent.blockFramedDoublePanel.get().asItem())
        {
            item = FBContent.blockFramedPanel.get().asItem();
            doubleBlock = true;
        }
        else if (item == FBContent.blockFramedDoubleSlab.get().asItem())
        {
            item = FBContent.blockFramedSlab.get().asItem();
            doubleBlock = true;
        }

        int remainingBlock = doubleBlock ? 2 : 1;
        boolean foundCamo = false;
        boolean foundCamoTwo = false;
        boolean foundGlowstone = false;

        PlayerInventory inv = player.inventory;
        for (int i = 0; i < inv.getContainerSize(); i++)
        {
            ItemStack stack = inv.getItem(i);
            if (remainingBlock > 0 && stack.getItem() == item)
            {
                int size = stack.getCount();
                stack.shrink(Math.min(remainingBlock, size));
                remainingBlock -= size - stack.getCount();

                inv.setChanged();
            }

            if (!foundCamo && !camo.isEmpty() && stack.getItem() == camo.getItem())
            {
                foundCamo = true;

                stack.shrink(1);
                inv.setChanged();
            }
            //Make sure stack in inventory is not empty when using the same camos in both slots
            if (!foundCamoTwo && !camoTwo.isEmpty() && stack.getItem() == camoTwo.getItem() && !stack.isEmpty())
            {
                foundCamoTwo = true;

                stack.shrink(1);
                inv.setChanged();
            }

            if (!foundGlowstone && glowstone && stack.getItem().is(Tags.Items.DUSTS_GLOWSTONE))
            {
                foundGlowstone = true;

                stack.shrink(1);
                inv.setChanged();
            }

            if (remainingBlock <= 0 && (camo.isEmpty() || foundCamo) && (camoTwo.isEmpty() || foundCamoTwo) && (!glowstone || foundGlowstone))
            {
                break;
            }
        }
    }

    public Block getTargetBlock(ItemStack stack)
    {
        CompoundNBT tag = stack.getOrCreateTagElement("blueprint_data");
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("framed_block")));
        Objects.requireNonNull(block);
        return block;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> components, ITooltipFlag flag)
    {
        CompoundNBT tag = stack.getOrCreateTagElement("blueprint_data");
        if (tag.isEmpty())
        {
            components.add(new TranslationTextComponent("desc.framed_blocks:blueprint_block", BLOCK_NONE).withStyle(TextFormatting.GOLD));
        }
        else
        {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("framed_block")));
            ITextComponent blockName = block == null ? BLOCK_INVALID : block.getName().withStyle(TextFormatting.WHITE);

            CompoundNBT beTag = tag.getCompound("camo_data");
            ITextComponent camoName = !(block instanceof IFramedBlock) ? BLOCK_NONE : ((IFramedBlock) block).printCamoBlock(beTag);
            ITextComponent illuminated = beTag.getBoolean("glowing") ? ILLUMINATED_TRUE : ILLUMINATED_FALSE;

            ITextComponent lineOne = new TranslationTextComponent(CONTAINED_BLOCK, blockName).withStyle(TextFormatting.GOLD);
            ITextComponent lineTwo = new TranslationTextComponent(CAMO_BLOCK, camoName).withStyle(TextFormatting.GOLD);
            ITextComponent lineThree = new TranslationTextComponent(IS_ILLUMINATED, illuminated).withStyle(TextFormatting.GOLD);

            components.addAll(Arrays.asList(lineOne, lineTwo, lineThree));
        }
    }
}
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
    public static final IFormattableTextComponent BLOCK_NONE = new TranslationTextComponent("desc.framed_blocks:blueprint_none").mergeStyle(TextFormatting.RED);
    public static final IFormattableTextComponent BLOCK_INVALID = new TranslationTextComponent("desc.framed_blocks:blueprint_invalid").mergeStyle(TextFormatting.RED);
    public static final IFormattableTextComponent ILLUMINATED_FALSE = new TranslationTextComponent("desc.framed_blocks:blueprint_illuminated_false").mergeStyle(TextFormatting.RED);
    public static final IFormattableTextComponent ILLUMINATED_TRUE = new TranslationTextComponent("desc.framed_blocks:blueprint_illuminated_true").mergeStyle(TextFormatting.GREEN);
    public static final IFormattableTextComponent CANT_COPY = new TranslationTextComponent("desc.framed_blocks:blueprint_cant_copy").mergeStyle(TextFormatting.RED);

    public FramedBlueprintItem(FramedToolType type) { super(type); }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) { return false; }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking())
        {
            if (!world.isRemote())
            {
                CompoundNBT tag = stack.getOrCreateChildTag("blueprint_data");
                tag.remove("framed_block");
                tag.remove("camo_data");
                tag.remove("camo_data_two");
            }
            return ActionResult.func_233538_a_(stack, world.isRemote());
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        PlayerEntity player = context.getPlayer();
        if (player == null) { return ActionResultType.FAIL; }

        World world = context.getWorld();
        BlockPos pos = context.getPos();

        CompoundNBT tag = context.getItem().getOrCreateChildTag("blueprint_data");

        if (player.isSneaking())
        {
            return writeBlueprint(world, pos, tag);
        }
        else if (!tag.isEmpty())
        {
            return readBlueprint(context, player, tag);
        }
        return super.onItemUse(context);
    }

    private ActionResultType writeBlueprint(World world, BlockPos pos, CompoundNBT tag)
    {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof FramedTileEntity))
        {
            return ActionResultType.FAIL;
        }

        if (!world.isRemote())
        {
            BlockState state = world.getBlockState(pos);
            //noinspection ConstantConditions
            String block = state.getBlock().getRegistryName().toString();
            tag.putString("framed_block", block);

            CompoundNBT nbt = ((FramedTileEntity)te).writeToBlueprint();
            if (state.getBlock() == FBContent.blockFramedDoor.get())
            {
                boolean top = state.get(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER;
                BlockPos posTwo = top ? pos.down() : pos.up();
                TileEntity teTwo = world.getTileEntity(posTwo);
                CompoundNBT nbtTwo = teTwo instanceof FramedTileEntity ? ((FramedTileEntity)teTwo).writeToBlueprint() : new CompoundNBT();

                tag.put("camo_data", top ? nbtTwo : nbt);
                tag.put("camo_data_two", top ? nbt : nbtTwo);
            }
            else
            {
                tag.put("camo_data", nbt);
            }
        }
        return ActionResultType.func_233537_a_(world.isRemote());
    }

    private ActionResultType readBlueprint(ItemUseContext context, PlayerEntity player, CompoundNBT tag)
    {
        Block block = getTargetBlock(context.getItem());

        //noinspection deprecation
        if (block.getDefaultState().isAir())
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
        if (player.abilities.isCreativeMode) { return false; } //Creative mode can always build

        CompoundNBT camoData = tag.getCompound("camo_data");

        ItemStack camo = ItemStack.read(camoData.getCompound("camo_stack"));
        ItemStack camoTwo = camoData.contains("camo_stack_two") ? ItemStack.read(camoData.getCompound("camo_stack_two")) : ItemStack.EMPTY;
        boolean glowstone = tag.getCompound("camo_data").getBoolean("glowing");

        boolean doubleBlock = false;
        if (item == FBContent.blockFramedDoor.get().asItem() && tag.contains("camo_data_two"))
        {
            camoTwo = ItemStack.read(tag.getCompound("camo_data_two").getCompound("camo_stack"));
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
            int count = player.inventory.count(item);
            if (count < 2) { return true; }
        }
        else
        {
            if (!player.inventory.hasItemStack(new ItemStack(item))) { return true; }
        }

        if (!camo.isEmpty() && camo.getItem() == camoTwo.getItem())
        {
            int count = player.inventory.count(camo.getItem());
            if (count < 2) { return true; }
        }
        else
        {
            if (!camo.isEmpty() && !player.inventory.hasItemStack(camo)) { return true; }
            if (!camoTwo.isEmpty() && !player.inventory.hasItemStack(camoTwo)) { return true; }
        }

        return glowstone && !player.inventory.hasTag(Tags.Items.DUSTS_GLOWSTONE);
    }

    private ActionResultType tryPlace(ItemUseContext context, PlayerEntity player, Item item, CompoundNBT tag)
    {
        ItemStack dummyStack = new ItemStack(item, 1);
        dummyStack.getOrCreateTag().put("BlockEntityTag", tag.getCompound("camo_data").copy());

        ItemUseContext placeContext = new ItemUseContext(
                context.getWorld(),
                context.getPlayer(),
                context.getHand(),
                dummyStack,
                new BlockRayTraceResult(context.getHitVec(), context.getFace(), context.getPos(), context.isInside())
        );
        //Needs to happen before placing to make sure we really get the target pos, especially in case of replacing stuff like grass
        BlockPos topPos = new BlockItemUseContext(placeContext).getPos().up();
        ActionResultType result = item.onItemUse(placeContext);

        if (!context.getWorld().isRemote() && result.isSuccessOrConsume())
        {
            if (item == FBContent.blockFramedDoor.get().asItem())
            {
                if (context.getWorld().getTileEntity(topPos) instanceof FramedTileEntity && tag.contains("camo_data_two", Constants.NBT.TAG_COMPOUND))
                {
                    //noinspection ConstantConditions
                    dummyStack.getOrCreateTag().put("BlockEntityTag", tag.get("camo_data_two"));
                    BlockItem.setTileEntityNBT(context.getWorld(), context.getPlayer(), topPos, dummyStack);
                }
            }

            if (!player.abilities.isCreativeMode)
            {
                consumeItems(player, item, tag);
            }
        }

        return result;
    }

    private void consumeItems(PlayerEntity player, Item item, CompoundNBT tag)
    {
        CompoundNBT camoData = tag.getCompound("camo_data");

        ItemStack camo = ItemStack.read(camoData.getCompound("camo_stack"));
        ItemStack camoTwo = camoData.contains("camo_stack_two") ? ItemStack.read(camoData.getCompound("camo_stack_two")) : ItemStack.EMPTY;
        boolean glowstone = camoData.getBoolean("glowing");

        boolean doubleBlock = false;
        if (item == FBContent.blockFramedDoor.get().asItem() && tag.contains("camo_data_two"))
        {
            camoTwo = ItemStack.read(tag.getCompound("camo_data_two").getCompound("camo_stack"));
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

        int foundBlock = doubleBlock ? 2 : 1;
        boolean foundCamo = false;
        boolean foundCamoTwo = false;
        boolean foundGlowstone = false;

        PlayerInventory inv = player.inventory;
        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack stack = inv.getStackInSlot(i);
            if (foundBlock > 0 && stack.getItem() == item)
            {
                int size = stack.getCount();
                stack.shrink(foundBlock);
                foundBlock -= size - stack.getCount();

                inv.markDirty();
            }

            if (!foundCamo && !camo.isEmpty() && stack.getItem() == camo.getItem())
            {
                foundCamo = true;

                stack.shrink(1);
                inv.markDirty();
            }
            //Make sure stack in inventory is not empty when using the same camos in both slots
            if (!foundCamoTwo && !camoTwo.isEmpty() && stack.getItem() == camoTwo.getItem() && !stack.isEmpty())
            {
                foundCamoTwo = true;

                stack.shrink(1);
                inv.markDirty();
            }

            if (!foundGlowstone && glowstone && stack.getItem().isIn(Tags.Items.DUSTS_GLOWSTONE))
            {
                foundGlowstone = true;

                stack.shrink(1);
                inv.markDirty();
            }

            if (foundBlock <= 0 && (camo.isEmpty() || foundCamo) && (camoTwo.isEmpty() || foundCamoTwo) && (!glowstone || foundGlowstone))
            {
                break;
            }
        }
    }

    public Block getTargetBlock(ItemStack stack)
    {
        CompoundNBT tag = stack.getOrCreateChildTag("blueprint_data");
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("framed_block")));
        Objects.requireNonNull(block);
        return block;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World level, List<ITextComponent> components, ITooltipFlag flag)
    {
        CompoundNBT tag = stack.getOrCreateChildTag("blueprint_data");
        if (tag.isEmpty())
        {
            components.add(new TranslationTextComponent("desc.framed_blocks:blueprint_block", BLOCK_NONE).mergeStyle(TextFormatting.GOLD));
        }
        else
        {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("framed_block")));
            ITextComponent blockName = block == null ? BLOCK_INVALID : block.getTranslatedName().mergeStyle(TextFormatting.WHITE);

            CompoundNBT beTag = tag.getCompound("camo_data");
            ITextComponent camoName = !(block instanceof IFramedBlock) ? BLOCK_NONE : ((IFramedBlock) block).printCamoBlock(beTag);
            ITextComponent illuminated = beTag.getBoolean("glowing") ? ILLUMINATED_TRUE : ILLUMINATED_FALSE;

            ITextComponent lineOne = new TranslationTextComponent(CONTAINED_BLOCK, blockName).mergeStyle(TextFormatting.GOLD);
            ITextComponent lineTwo = new TranslationTextComponent(CAMO_BLOCK, camoName).mergeStyle(TextFormatting.GOLD);
            ITextComponent lineThree = new TranslationTextComponent(IS_ILLUMINATED, illuminated).mergeStyle(TextFormatting.GOLD);

            components.addAll(Arrays.asList(lineOne, lineTwo, lineThree));
        }
    }
}
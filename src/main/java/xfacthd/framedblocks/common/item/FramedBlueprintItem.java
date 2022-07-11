package xfacthd.framedblocks.common.item;

import com.google.common.base.Preconditions;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.common.util.ServerConfig;

import javax.annotation.Nullable;
import java.util.*;

public class FramedBlueprintItem extends FramedToolItem
{
    public static final String CONTAINED_BLOCK = "desc.framed_blocks.blueprint_block";
    public static final String CAMO_BLOCK = "desc.framed_blocks.blueprint_camo";
    public static final String IS_ILLUMINATED = "desc.framed_blocks.blueprint_illuminated";
    public static final MutableComponent BLOCK_NONE = Utils.translate("desc", "blueprint_none").withStyle(ChatFormatting.RED);
    public static final MutableComponent BLOCK_INVALID = Utils.translate("desc", "blueprint_invalid").withStyle(ChatFormatting.RED);
    public static final MutableComponent ILLUMINATED_FALSE = Utils.translate("desc", "blueprint_illuminated_false").withStyle(ChatFormatting.RED);
    public static final MutableComponent ILLUMINATED_TRUE = Utils.translate("desc", "blueprint_illuminated_true").withStyle(ChatFormatting.GREEN);
    public static final MutableComponent CANT_COPY = Utils.translate("desc", "blueprint_cant_copy").withStyle(ChatFormatting.RED);
    public static final Component CANT_PLACE_FLUID_CAMO = Utils.translate("desc", "blueprint_cant_place_fluid_camo").withStyle(ChatFormatting.RED);

    private static final Map<Block, BlueprintCopyBehaviour> COPY_BEHAVIOURS = new IdentityHashMap<>();
    private static final BlueprintCopyBehaviour NO_OP_BEHAVIOUR = new BlueprintCopyBehaviour(){};

    public FramedBlueprintItem(FramedToolType type) { super(type); }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) { return false; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown())
        {
            if (!level.isClientSide())
            {
                CompoundTag tag = stack.getOrCreateTagElement("blueprint_data");
                tag.remove("framed_block");
                tag.remove("camo_data");
                tag.remove("camo_data_two");
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Player player = context.getPlayer();
        if (player == null) { return InteractionResult.FAIL; }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        CompoundTag tag = context.getItemInHand().getOrCreateTagElement("blueprint_data");

        if (player.isShiftKeyDown())
        {
            return writeBlueprint(level, pos, tag);
        }
        else if (!tag.isEmpty())
        {
            return readBlueprint(context, player, tag);
        }
        return super.useOn(context);
    }

    private static InteractionResult writeBlueprint(Level level, BlockPos pos, CompoundTag tag)
    {
        if (!(level.getBlockEntity(pos) instanceof FramedBlockEntity be))
        {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide())
        {
            BlockState state = level.getBlockState(pos);
            //noinspection ConstantConditions
            String blockName = ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString();
            tag.putString("framed_block", blockName);

            Block block = state.getBlock();
            if (!getBehaviour(block).writeToBlueprint(level, pos, state, be, tag))
            {
                tag.put("camo_data", be.writeToBlueprint());
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static InteractionResult readBlueprint(UseOnContext context, Player player, CompoundTag tag)
    {
        Block block = getTargetBlock(context.getItemInHand());

        if (block.defaultBlockState().isAir())
        {
            return InteractionResult.FAIL;
        }

        Item item = block.asItem();
        if (!(item instanceof BlockItem blockItem))
        {
            return InteractionResult.FAIL;
        }

        if (checkMissingMaterials(player, blockItem, tag))
        {
            return InteractionResult.FAIL;
        }

        return tryPlace(context, player, blockItem, tag);
    }

    private static boolean checkMissingMaterials(Player player, BlockItem item, CompoundTag tag)
    {
        if (player.getAbilities().instabuild) { return false; } //Creative mode can always build

        Set<CamoContainer> camos = getCamoContainers(item, tag);

        //Copying fluid camos is currently not possible
        if (camos.stream().anyMatch(camo -> camo.getType().isFluid()))
        {
            player.sendSystemMessage(CANT_PLACE_FLUID_CAMO);
            return true;
        }

        List<ItemStack> materials = new ArrayList<>();
        materials.add(getBlockItem(item));
        materials.addAll(getCamoStacksMerged(camos));

        int glowstone = getBehaviour(item.getBlock()).getGlowstoneCount(tag);
        if (glowstone > 0)
        {
            materials.add(new ItemStack(Items.GLOWSTONE_DUST, glowstone));
        }
        int intangible = getBehaviour(item.getBlock()).getIntangibleCount(tag);
        if (intangible > 0)
        {
            materials.add(new ItemStack(ServerConfig.intangibleMarkerItem, glowstone));
        }

        for (ItemStack stack : materials)
        {
            if (stack.isEmpty()) { continue; }

            if (player.getInventory().countItem(stack.getItem()) < stack.getCount())
            {
                return true;
            }
        }

        return false;
    }

    private static InteractionResult tryPlace(UseOnContext context, Player player, BlockItem item, CompoundTag tag)
    {
        ItemStack dummyStack = new ItemStack(item, 1);
        dummyStack.getOrCreateTag().put("BlockEntityTag", tag.getCompound("camo_data").copy());

        UseOnContext placeContext = new UseOnContext(
                context.getLevel(),
                context.getPlayer(),
                context.getHand(),
                dummyStack,
                new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside())
        );
        //Needs to happen before placing to make sure we really get the target pos, especially in case of replacing stuff like grass
        BlockPos pos = new BlockPlaceContext(placeContext).getClickedPos();
        InteractionResult result = item.useOn(placeContext);

        if (!context.getLevel().isClientSide() && result.consumesAction())
        {
            getBehaviour(item.getBlock()).postProcessPaste(context.getLevel(), pos, context.getPlayer(), tag, dummyStack);

            if (!player.getAbilities().instabuild)
            {
                consumeItems(player, item, tag);
            }
        }

        return result;
    }

    private static void consumeItems(Player player, BlockItem item, CompoundTag tag)
    {
        Set<CamoContainer> camos = getCamoContainers(item, tag);

        //Copying fluid camos is currently not possible
        if (camos.stream().anyMatch(camo -> camo.getType().isFluid())) { return; }

        List<ItemStack> materials = new ArrayList<>();
        materials.add(getBlockItem(item));
        materials.addAll(getCamoStacksMerged(camos));

        int glowstone = getBehaviour(item.getBlock()).getGlowstoneCount(tag);
        if (glowstone > 0)
        {
            materials.add(new ItemStack(Items.GLOWSTONE_DUST, glowstone));
        }
        int intangible = getBehaviour(item.getBlock()).getIntangibleCount(tag);
        if (intangible > 0)
        {
            materials.add(new ItemStack(ServerConfig.intangibleMarkerItem, glowstone));
        }

        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++)
        {
            ItemStack stack = inv.getItem(i);
            for (ItemStack material : materials)
            {
                if (!material.isEmpty() && stack.is(material.getItem()))
                {
                    int size = stack.getCount();
                    stack.shrink(Math.min(material.getCount(), size));
                    material.shrink(size - stack.getCount());

                    inv.setChanged();
                }
            }
            materials.removeIf(ItemStack::isEmpty);

            if (materials.isEmpty())
            {
                break;
            }
        }
    }

    private static BlueprintCopyBehaviour getBehaviour(Block block)
    {
        return COPY_BEHAVIOURS.getOrDefault(block, NO_OP_BEHAVIOUR);
    }

    public static Set<CamoContainer> getCamoContainers(BlockItem item, CompoundTag tag)
    {
        return getBehaviour(item.getBlock())
                .getCamos(tag)
                .orElseGet(() ->
                        Set.of(CamoContainer.load(tag.getCompound("camo_data").getCompound("camo")))
                );
    }

    private static ItemStack getBlockItem(BlockItem item)
    {
        return getBehaviour(item.getBlock())
                .getBlockItem()
                .orElse(new ItemStack(item));
    }

    private static List<ItemStack> getCamoStacksMerged(Set<CamoContainer> camos)
    {
        List<ItemStack> camoStacks = new ArrayList<>();
        for (CamoContainer camo : camos)
        {
            ItemStack stack = camo.toItemStack(ItemStack.EMPTY);
            if (stack.isEmpty())
            {
                continue;
            }

            for (ItemStack existing : camoStacks)
            {
                if (ItemStack.isSame(existing, stack))
                {
                    int size = existing.getCount();
                    existing.grow(Math.max(existing.getMaxStackSize() - size, stack.getCount()));
                    stack.shrink(existing.getCount() - size);

                    if (stack.isEmpty())
                    {
                        break;
                    }
                }
            }

            if (!stack.isEmpty())
            {
                camoStacks.add(stack);
            }
        }
        return camoStacks;
    }

    public static Block getTargetBlock(ItemStack stack)
    {
        CompoundTag tag = stack.getOrCreateTagElement("blueprint_data");
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("framed_block")));
        Objects.requireNonNull(block);
        return block;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag)
    {
        CompoundTag tag = stack.getOrCreateTagElement("blueprint_data");
        if (tag.isEmpty())
        {
            components.add(Component.translatable(CONTAINED_BLOCK, BLOCK_NONE).withStyle(ChatFormatting.GOLD));
        }
        else
        {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("framed_block")));
            Component blockName = block == null ? BLOCK_INVALID : block.getName().withStyle(ChatFormatting.WHITE);

            CompoundTag beTag = tag.getCompound("camo_data");
            Component camoName = !(block instanceof IFramedBlock fb) ? BLOCK_NONE : fb.printCamoBlock(beTag).orElse(BLOCK_NONE);
            Component illuminated = beTag.getBoolean("glowing") ? ILLUMINATED_TRUE : ILLUMINATED_FALSE;

            Component lineOne = Component.translatable(CONTAINED_BLOCK, blockName).withStyle(ChatFormatting.GOLD);
            Component lineTwo = Component.translatable(CAMO_BLOCK, camoName).withStyle(ChatFormatting.GOLD);
            Component lineThree = Component.translatable(IS_ILLUMINATED, illuminated).withStyle(ChatFormatting.GOLD);

            components.addAll(Arrays.asList(lineOne, lineTwo, lineThree));
        }
    }



    public static synchronized void registerBehaviour(BlueprintCopyBehaviour behaviour, Block... blocks)
    {
        Preconditions.checkNotNull(behaviour, "BlueprintCopyBehaviour must be non-null");
        Preconditions.checkNotNull(blocks, "Blocks array must be non-null to register a BlueprintCopyBehaviour");
        Preconditions.checkState(blocks.length > 0, "At least one block must be provided to register a BlueprintCopyBehaviour");

        for (Block block : blocks)
        {
            COPY_BEHAVIOURS.put(block, behaviour);
        }
    }
}
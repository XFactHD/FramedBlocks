package xfacthd.framedblocks.common.item;

import com.google.common.base.Preconditions;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.fml.ModLoader;
import xfacthd.framedblocks.api.blueprint.*;
import xfacthd.framedblocks.api.camo.*;
import xfacthd.framedblocks.api.util.CamoList;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.common.config.ServerConfig;

import java.util.*;

public class FramedBlueprintItem extends FramedToolItem
{
    public static final String CONTAINED_BLOCK = "desc.framedblocks.blueprint_block";
    public static final String CAMO_BLOCK = "desc.framedblocks.blueprint_camo";
    public static final String IS_ILLUMINATED = "desc.framedblocks.blueprint_illuminated";
    public static final String IS_INTANGIBLE = "desc.framedblocks.blueprint_intangible";
    public static final String IS_REINFORCED = "desc.framedblocks.blueprint_reinforced";
    public static final String MISSING_MATERIALS = Utils.translationKey("desc", "blueprint_missing_materials");
    public static final MutableComponent BLOCK_NONE = Utils.translate("desc", "blueprint_none").withStyle(ChatFormatting.RED);
    public static final MutableComponent BLOCK_INVALID = Utils.translate("desc", "blueprint_invalid").withStyle(ChatFormatting.RED);
    public static final MutableComponent FALSE = Utils.translate("desc", "blueprint_false").withStyle(ChatFormatting.RED);
    public static final MutableComponent TRUE = Utils.translate("desc", "blueprint_true").withStyle(ChatFormatting.GREEN);
    public static final MutableComponent CANT_COPY = Utils.translate("desc", "blueprint_cant_copy").withStyle(ChatFormatting.RED);
    public static final Component CANT_PLACE_FLUID_CAMO = Utils.translate("desc", "blueprint_cant_place_fluid_camo").withStyle(ChatFormatting.RED);
    private static final String MATERIAL_LIST_PREFIX = "\n  - ";

    private static final Map<Block, BlueprintCopyBehaviour> COPY_BEHAVIOURS = new IdentityHashMap<>();
    private static final BlueprintCopyBehaviour NO_OP_BEHAVIOUR = new BlueprintCopyBehaviour(){};

    public FramedBlueprintItem(FramedToolType type)
    {
        super(type, new Properties().component(FBContent.DC_TYPE_BLUEPRINT_DATA, BlueprintData.EMPTY));
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player)
    {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown())
        {
            if (!level.isClientSide())
            {
                stack.set(FBContent.DC_TYPE_BLUEPRINT_DATA, BlueprintData.EMPTY);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Player player = context.getPlayer();
        if (player == null)
        {
            return InteractionResult.FAIL;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (player.isShiftKeyDown())
        {
            return writeBlueprint(level, pos, context.getItemInHand());
        }
        BlueprintData data = context.getItemInHand().getOrDefault(FBContent.DC_TYPE_BLUEPRINT_DATA, BlueprintData.EMPTY);
        if (!data.isEmpty())
        {
            return readBlueprint(context, player, data);
        }
        return super.useOn(context);
    }

    private static InteractionResult writeBlueprint(Level level, BlockPos pos, ItemStack stack)
    {
        if (!(level.getBlockEntity(pos) instanceof FramedBlockEntity be))
        {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide())
        {
            BlockState state = be.getBlockState();
            BlueprintData data = getBehaviour(state.getBlock()).writeToBlueprint(level, pos, state, be);
            stack.set(FBContent.DC_TYPE_BLUEPRINT_DATA, data);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static InteractionResult readBlueprint(UseOnContext context, Player player, BlueprintData data)
    {
        Block block = data.block();
        if (block.defaultBlockState().isAir())
        {
            return InteractionResult.FAIL;
        }

        Item item = block.asItem();
        if (!(item instanceof BlockItem blockItem))
        {
            return InteractionResult.FAIL;
        }

        if (checkMissingMaterials(player, data))
        {
            return InteractionResult.FAIL;
        }

        return tryPlace(context, player, blockItem, data);
    }

    private static boolean checkMissingMaterials(Player player, BlueprintData data)
    {
        if (player.getAbilities().instabuild)
        {
            //Creative mode can always build
            return false;
        }

        CamoList camos = getCamoContainers(data);

        if (!canCopyAllCamos(camos))
        {
            if (player.level().isClientSide())
            {
                player.sendSystemMessage(CANT_PLACE_FLUID_CAMO);
            }
            return true;
        }

        List<ItemStack> materials = new ArrayList<>();
        materials.add(getBlockItem(data));
        if (ServerConfig.VIEW.shouldConsumeCamoItem())
        {
            materials.addAll(getCamoStacksMerged(camos));
        }

        BlueprintCopyBehaviour behaviour = getBehaviour(data.block());

        int glowstone = behaviour.getGlowstoneCount(data);
        if (glowstone > 0)
        {
            materials.add(new ItemStack(Items.GLOWSTONE_DUST, glowstone));
        }
        int intangible = behaviour.getIntangibleCount(data);
        if (intangible > 0)
        {
            materials.add(new ItemStack(ServerConfig.VIEW.getIntangibilityMarkerItem(), glowstone));
        }
        int reinforcement = behaviour.getReinforcementCount(data);
        if (reinforcement > 0)
        {
            materials.add(new ItemStack(FBContent.ITEM_FRAMED_REINFORCEMENT.value(), reinforcement));
        }
        materials.addAll(behaviour.getAdditionalConsumedMaterials(data));

        List<ItemStack> missingMaterials = new ArrayList<>();
        for (ItemStack stack : materials)
        {
            if (stack.isEmpty())
            {
                continue;
            }

            if (player.getInventory().countItem(stack.getItem()) < stack.getCount())
            {
                missingMaterials.add(stack);
            }
        }

        if (!missingMaterials.isEmpty())
        {
            if (player.level().isClientSide())
            {
                List<String> names = missingMaterials.stream()
                        .map(s -> s.getHoverName().getString())
                        .toList();
                String list = MATERIAL_LIST_PREFIX + String.join(MATERIAL_LIST_PREFIX, names);
                player.sendSystemMessage(Component.translatable(MISSING_MATERIALS).append(list));
            }
            return true;
        }

        return false;
    }

    private static InteractionResult tryPlace(UseOnContext context, Player player, BlockItem item, BlueprintData data)
    {
        ItemStack dummyStack = new ItemStack(item, 1);

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
            if (context.getLevel().getBlockEntity(pos) instanceof FramedBlockEntity be)
            {
                be.applyBlueprintData(data);
            }
            getBehaviour(data.block()).postProcessPaste(context.getLevel(), pos, context.getPlayer(), data, dummyStack);

            if (!player.getAbilities().instabuild)
            {
                consumeItems(player, data);
            }
        }

        return result;
    }

    private static void consumeItems(Player player, BlueprintData data)
    {
        CamoList camos = getCamoContainers(data);

        if (!canCopyAllCamos(camos)) return;

        List<ItemStack> materials = new ArrayList<>();
        materials.add(getBlockItem(data));
        if (ServerConfig.VIEW.shouldConsumeCamoItem())
        {
            materials.addAll(getCamoStacksMerged(camos));
        }

        BlueprintCopyBehaviour behaviour = getBehaviour(data.block());

        int glowstone = behaviour.getGlowstoneCount(data);
        if (glowstone > 0)
        {
            materials.add(new ItemStack(Items.GLOWSTONE_DUST, glowstone));
        }
        int intangible = behaviour.getIntangibleCount(data);
        if (intangible > 0)
        {
            materials.add(new ItemStack(ServerConfig.VIEW.getIntangibilityMarkerItem(), glowstone));
        }
        int reinforcement = behaviour.getReinforcementCount(data);
        if (reinforcement > 0)
        {
            materials.add(new ItemStack(FBContent.ITEM_FRAMED_REINFORCEMENT.value(), reinforcement));
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean canCopyAllCamos(CamoList camos)
    {
        if (!ServerConfig.VIEW.shouldConsumeCamoItem()) return true;

        //Copying fluid camos is currently not possible
        return camos.stream()
                .map(CamoContainer::getFactory)
                .allMatch(CamoContainerFactory::canTriviallyConvertToItemStack);
    }

    public static BlueprintCopyBehaviour getBehaviour(Block block)
    {
        return COPY_BEHAVIOURS.getOrDefault(block, NO_OP_BEHAVIOUR);
    }

    public static CamoList getCamoContainers(BlueprintData data)
    {
        return getBehaviour(data.block()).getCamos(data);
    }

    private static ItemStack getBlockItem(BlueprintData data)
    {
        return getBehaviour(data.block()).getBlockItem(data);
    }

    private static List<ItemStack> getCamoStacksMerged(CamoList camos)
    {
        List<ItemStack> camoStacks = new ArrayList<>();
        for (CamoContainer<?, ?> camo : camos)
        {
            ItemStack stack = CamoContainerHelper.dropCamo(camo);
            if (stack.isEmpty())
            {
                continue;
            }

            for (ItemStack existing : camoStacks)
            {
                if (ItemStack.isSameItem(existing, stack))
                {
                    int size = existing.getCount();
                    existing.grow(Math.min(existing.getMaxStackSize() - size, stack.getCount()));
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> components, TooltipFlag flag)
    {
        BlueprintData blueprintData = stack.getOrDefault(FBContent.DC_TYPE_BLUEPRINT_DATA, BlueprintData.EMPTY);
        if (blueprintData.isEmpty())
        {
            components.add(Component.translatable(CONTAINED_BLOCK, BLOCK_NONE).withStyle(ChatFormatting.GOLD));
        }
        else
        {
            Block block = blueprintData.block();
            Component blockName = block == Blocks.AIR ? BLOCK_INVALID : block.getName().withStyle(ChatFormatting.WHITE);

            Component camoName = !(block instanceof IFramedBlock fb) ? BLOCK_NONE : fb.printCamoBlock(blueprintData).orElse(BLOCK_NONE);
            Component illuminated = blueprintData.glowing() ? TRUE : FALSE;
            Component intangible = blueprintData.intangible() ? TRUE : FALSE;
            Component reinforced = blueprintData.reinforced() ? TRUE : FALSE;

            Component lineOne = Component.translatable(CONTAINED_BLOCK, blockName).withStyle(ChatFormatting.GOLD);
            Component lineTwo = Component.translatable(CAMO_BLOCK, camoName).withStyle(ChatFormatting.GOLD);
            Component lineThree = Component.translatable(IS_ILLUMINATED, illuminated).withStyle(ChatFormatting.GOLD);
            Component lineFour = Component.translatable(IS_INTANGIBLE, intangible).withStyle(ChatFormatting.GOLD);
            Component lineFive = Component.translatable(IS_REINFORCED, reinforced).withStyle(ChatFormatting.GOLD);

            components.addAll(Arrays.asList(lineOne, lineTwo, lineThree, lineFour, lineFive));
        }
    }



    public static void init()
    {
        ModLoader.postEvent(new RegisterBlueprintCopyBehavioursEvent((behaviour, blocks) ->
        {
            Preconditions.checkNotNull(behaviour, "BlueprintCopyBehaviour must be non-null");
            Preconditions.checkState(blocks.length > 0, "At least one block must be provided to register a BlueprintCopyBehaviour");

            for (Block block : blocks)
            {
                Preconditions.checkNotNull(block);
                COPY_BEHAVIOURS.put(block, behaviour);
            }
        }));
    }
}

package io.github.xfacthd.framedblocks.common.item;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.block.blockentity.FrameModifier;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.api.blueprint.RegisterBlueprintCopyBehavioursEvent;
import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.CamoContainerHelper;
import io.github.xfacthd.framedblocks.api.camo.CamoList;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.config.ServerConfig;
import io.github.xfacthd.framedblocks.common.data.FramedToolType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.fml.ModLoader;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class FramedBlueprintItem extends FramedToolItem
{
    public static final Component CANT_PLACE_FLUID_CAMO = Utils.translate("desc", "blueprint_cant_place_fluid_camo").withStyle(ChatFormatting.RED);
    private static final String MATERIAL_LIST_PREFIX = "\n  - ";
    private static final FrameModifier[] MODIFIERS = FrameModifier.values();

    private static final Map<Block, BlueprintCopyBehaviour> COPY_BEHAVIOURS = new IdentityHashMap<>();
    private static final BlueprintCopyBehaviour NO_OP_BEHAVIOUR = new BlueprintCopyBehaviour(){};

    public FramedBlueprintItem(FramedToolType type, Properties props)
    {
        super(type, props.component(FBContent.DC_TYPE_BLUEPRINT_DATA, BlueprintData.EMPTY));
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player)
    {
        return false;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown())
        {
            if (!level.isClientSide())
            {
                stack.set(FBContent.DC_TYPE_BLUEPRINT_DATA, BlueprintData.EMPTY);
            }
            return InteractionResult.SUCCESS;
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
        if (!(level.getBlockEntity(pos) instanceof IFramedBlockEntity be))
        {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide())
        {
            BlockState state = be.getBlockState();
            BlueprintCopyBehaviour behaviour = getBehaviour(state.getBlock());
            BlueprintData data = behaviour.writeToBlueprint(level, pos, state, be);
            data = storeBlockStateProperties(behaviour, data, state);
            stack.set(FBContent.DC_TYPE_BLUEPRINT_DATA, data);
        }
        return InteractionResult.SUCCESS;
    }

    private static BlueprintData storeBlockStateProperties(BlueprintCopyBehaviour behaviour, BlueprintData data, BlockState state)
    {
        Set<Property<?>> properties = behaviour.getPropertiesToCopy(state);
        if (!properties.isEmpty())
        {
            BlockItemStateProperties stateProps = BlockItemStateProperties.EMPTY;
            for (Property<?> property : properties)
            {
                stateProps = stateProps.with(property, state);
            }
            if (!stateProps.isEmpty())
            {
                return data.withBlockState(stateProps);
            }
        }
        return data;
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
            if (player instanceof ServerPlayer serverPlayer)
            {
                serverPlayer.sendSystemMessage(CANT_PLACE_FLUID_CAMO);
            }
            return true;
        }

        List<ItemStack> materials = collectMaterials(data, camos);

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
            if (player instanceof ServerPlayer serverPlayer)
            {
                List<String> names = missingMaterials.stream()
                        .map(s -> s.getHoverName().getString())
                        .toList();
                String list = MATERIAL_LIST_PREFIX + String.join(MATERIAL_LIST_PREFIX, names);
                serverPlayer.sendSystemMessage(Component.translatable(BlueprintData.MISSING_MATERIALS).append(list));
            }
            return true;
        }

        return false;
    }

    private static InteractionResult tryPlace(UseOnContext context, Player player, BlockItem item, BlueprintData data)
    {
        ItemStack dummyStack = new ItemStack(item, 1);
        dummyStack.set(DataComponents.BLOCK_STATE, data.blockState());

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
            if (context.getLevel().getBlockEntity(pos) instanceof IFramedBlockEntity be)
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

        List<ItemStack> materials = collectMaterials(data, camos);

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
        return camos.stream().allMatch(CamoContainer::canTriviallyConvertToItemStack);
    }

    private static List<ItemStack> collectMaterials(BlueprintData data, CamoList camos)
    {
        List<ItemStack> materials = new ArrayList<>();
        materials.add(getBlockItem(data));
        if (ServerConfig.VIEW.shouldConsumeCamoItem())
        {
            materials.addAll(getCamoStacksMerged(camos));
        }

        BlueprintCopyBehaviour behaviour = getBehaviour(data.block());
        for (FrameModifier modifier : MODIFIERS)
        {
            modifier.collectForBlueprint(behaviour, data, materials);
        }
        materials.addAll(behaviour.getAdditionalConsumedMaterials(data));
        return materials;
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
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> appender, TooltipFlag flag)
    {
        stack.getOrDefault(FBContent.DC_TYPE_BLUEPRINT_DATA, BlueprintData.EMPTY).addToTooltip(ctx, appender, flag, stack);
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

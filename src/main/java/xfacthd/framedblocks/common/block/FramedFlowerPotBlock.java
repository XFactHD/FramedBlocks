package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import xfacthd.framedblocks.common.blockentity.FramedFlowerPotBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class FramedFlowerPotBlock extends FramedBlock
{
    public FramedFlowerPotBlock() { super(BlockType.FRAMED_FLOWER_POT); }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        InteractionResult result = super.use(state, level, pos, player, hand, hit);
        if (result != InteractionResult.PASS) { return result; }

        if (level.getBlockEntity(pos) instanceof FramedFlowerPotBlockEntity be)
        {
            ItemStack stack = player.getItemInHand(hand);
            boolean isFlower = stack.getItem() instanceof BlockItem item && !getFlowerPotState(item.getBlock()).isAir();

            if (isFlower != be.hasFlowerBlock())
            {
                if (!level.isClientSide())
                {
                    if (isFlower && !be.hasFlowerBlock())
                    {
                        be.setFlowerBlock(((BlockItem) stack.getItem()).getBlock());

                        player.awardStat(Stats.POT_FLOWER);
                        if (!player.getAbilities().instabuild)
                        {
                            stack.shrink(1);
                        }
                    }
                    else
                    {
                        ItemStack flowerStack = new ItemStack(be.getFlowerBlock());
                        if (stack.isEmpty())
                        {
                            player.setItemInHand(hand, flowerStack);
                        }
                        else if (!player.addItem(flowerStack))
                        {
                            player.drop(flowerStack, false);
                        }

                        be.setFlowerBlock(Blocks.AIR);
                    }
                }

                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
            else
            {
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> drops = super.getDrops(state, builder);

        if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof FramedFlowerPotBlockEntity be)
        {
            Block flower = be.getFlowerBlock();
            if (!flower.defaultBlockState().isAir())
            {
                drops.add(new ItemStack(flower));
            }
        }

        return drops;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type)
    {
        return false;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new FramedFlowerPotBlockEntity(pos, state); }



    private static final Field FULL_POTS_FIELD = ObfuscationReflectionHelper.findField(FlowerPotBlock.class, "fullPots");
    public static BlockState getFlowerPotState(Block flower) //TODO: PR an accessor to an unmodifiable view of the fullPots map to Forge
    {
        try
        {
            //noinspection unchecked
            Map<ResourceLocation, Supplier<? extends Block>> fullPots = (Map<ResourceLocation, Supplier<? extends Block>>) FULL_POTS_FIELD.get(Blocks.FLOWER_POT);
            return fullPots.getOrDefault(flower.getRegistryName(), Blocks.AIR.delegate).get().defaultBlockState();
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Failed to access FlowerPotBlock#fullPots", e);
        }
    }
}
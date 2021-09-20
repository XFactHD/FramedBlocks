package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.pathfinding.PathType;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.tileentity.FramedFlowerPotTileEntity;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class FramedFlowerPotBlock extends FramedBlock
{
    public FramedFlowerPotBlock() { super(BlockType.FRAMED_FLOWER_POT); }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        ActionResultType result = super.onBlockActivated(state, world, pos, player, hand, hit);
        if (result != ActionResultType.PASS) { return result; }

        TileEntity te = world.getTileEntity(pos);
        if (te instanceof FramedFlowerPotTileEntity)
        {
            FramedFlowerPotTileEntity pot = (FramedFlowerPotTileEntity) te;

            ItemStack stack = player.getHeldItem(hand);
            Item item = stack.getItem();
            boolean isFlower = item instanceof BlockItem && !getFlowerPotState(((BlockItem) item).getBlock()).isAir();

            if (isFlower != pot.hasFlowerBlock())
            {
                if (!world.isRemote())
                {
                    if (isFlower && !pot.hasFlowerBlock())
                    {
                        pot.setFlowerBlock(((BlockItem) stack.getItem()).getBlock());

                        player.addStat(Stats.POT_FLOWER);
                        if (!player.abilities.isCreativeMode)
                        {
                            stack.shrink(1);
                        }
                    }
                    else
                    {
                        ItemStack flowerStack = new ItemStack(pot.getFlowerBlock());
                        if (stack.isEmpty())
                        {
                            player.setHeldItem(hand, flowerStack);
                        }
                        else if (!player.addItemStackToInventory(flowerStack))
                        {
                            player.dropItem(flowerStack, false);
                        }

                        pot.setFlowerBlock(Blocks.AIR);
                    }
                }

                return ActionResultType.func_233537_a_(world.isRemote());
            }
            else
            {
                return ActionResultType.CONSUME;
            }
        }

        return ActionResultType.PASS;
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader level, BlockPos pos, PathType type)
    {
        return false;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedFlowerPotTileEntity(); }



    private static final Field FULL_POTS_FIELD = ObfuscationReflectionHelper.findField(FlowerPotBlock.class, "fullPots");
    public static BlockState getFlowerPotState(Block flower) //TODO: PR an accessor to an unmodifiable view of the fullPots map to Forge
    {
        try
        {
            //noinspection unchecked
            Map<ResourceLocation, Supplier<? extends Block>> fullPots = (Map<ResourceLocation, Supplier<? extends Block>>) FULL_POTS_FIELD.get(Blocks.FLOWER_POT);
            return fullPots.getOrDefault(flower.getRegistryName(), Blocks.AIR.delegate).get().getDefaultState();
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Failed to access FlowerPotBlock#fullPots", e);
        }
    }
}
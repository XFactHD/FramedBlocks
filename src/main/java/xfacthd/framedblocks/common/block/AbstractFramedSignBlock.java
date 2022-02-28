package xfacthd.framedblocks.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.tileentity.FramedSignTileEntity;

import javax.annotation.Nullable;

public abstract class AbstractFramedSignBlock extends FramedBlock
{
    protected AbstractFramedSignBlock(BlockType type, Properties props) { super(type, props); }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        //Makes sure the block can have a camo applied, even when the sign can execute a command
        ActionResultType result = super.use(state, world, pos, player, hand, hit);
        if (result != ActionResultType.PASS) { return result; }

        ItemStack stack = player.getItemInHand(hand);
        boolean dye = stack.getItem() instanceof DyeItem && player.abilities.mayBuild;
        if (world.isClientSide)
        {
            return dye ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
        }
        else
        {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof FramedSignTileEntity)
            {
                FramedSignTileEntity sign = (FramedSignTileEntity) te;
                if (dye)
                {
                    boolean success = sign.setTextColor(((DyeItem) stack.getItem()).getDyeColor());
                    if (success && !player.isCreative())
                    {
                        stack.shrink(1);
                        player.inventory.setChanged();
                    }

                    if (success) { return ActionResultType.SUCCESS; }
                }
                else if (sign.executeCommand((ServerPlayerEntity) player))
                {
                    return ActionResultType.SUCCESS;
                }
            }
        }

        return ActionResultType.PASS;
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(world, pos, placer, stack);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState state, IBlockReader world, BlockPos pos, PathType type)
    {
        return type != PathType.WATER || world.getFluidState(pos).is(FluidTags.WATER);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedSignTileEntity(); }

    @Override
    public boolean isPossibleToRespawnInThis() { return true; }
}
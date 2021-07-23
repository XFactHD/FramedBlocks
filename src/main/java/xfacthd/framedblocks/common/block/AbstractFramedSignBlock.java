package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.tileentity.FramedSignTileEntity;

import javax.annotation.Nullable;

public abstract class AbstractFramedSignBlock extends FramedBlock
{
    protected AbstractFramedSignBlock(BlockType type, Properties props) { super(type, props); }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        //Makes sure the block can have a camo applied, even when the sign can execute a command
        InteractionResult result = super.use(state, world, pos, player, hand, hit);
        if (result != InteractionResult.FAIL) { return result; }

        ItemStack stack = player.getItemInHand(hand);
        boolean dye = stack.getItem() instanceof DyeItem && player.getAbilities().mayBuild;
        if (world.isClientSide())
        {
            return dye ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        else
        {
            if (world.getBlockEntity(pos) instanceof FramedSignTileEntity sign)
            {
                if (dye)
                {
                    boolean success = sign.setTextColor(((DyeItem) stack.getItem()).getDyeColor());
                    if (success && !player.isCreative())
                    {
                        stack.shrink(1);
                        player.getInventory().setChanged();
                    }

                    if (success) { return InteractionResult.SUCCESS; }
                }
                else if (sign.executeCommand((ServerPlayer) player))
                {
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(world, pos, placer, stack);
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedSignTileEntity(pos, state);
    }

    @Override
    public boolean isPossibleToRespawnInThis() { return true; }
}
package xfacthd.framedblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedChestTileEntity;
import xfacthd.framedblocks.common.tileentity.FramedStorageTileEntity;

@SuppressWarnings("deprecation")
public class FramedStorageBlock extends FramedBlock
{
    public FramedStorageBlock(BlockType type) { super(type); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.SOLID);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        ActionResultType result = super.onBlockActivated(state, world, pos, player, hand, hit);
        if (result != ActionResultType.PASS) { return result; }

        if (!world.isRemote())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof FramedStorageTileEntity)
            {
                ((FramedStorageTileEntity) te).open((ServerPlayerEntity) player);
            }
        }
        return ActionResultType.func_233537_a_(world.isRemote());
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (newState.getBlock() != state.getBlock())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof FramedChestTileEntity)
            {
                FramedChestTileEntity chest = (FramedChestTileEntity) te;
                chest.getDrops().forEach(stack -> spawnAsEntity(world, pos, stack));
                chest.clearContents();
                world.updateComparatorOutputLevel(pos, this);
            }
        }

        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) { return true; }

    @Override
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof FramedChestTileEntity)
        {
            return ((FramedChestTileEntity) te).getAnalogOutputSignal();
        }
        return 0;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedStorageTileEntity(); }
}

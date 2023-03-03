package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.menu.FramingSawMenu;

public class FramingSawBlock extends Block
{
    public static final Component MENU_TITLE = Utils.translate("title", "frame_crafter");
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 9, 16);

    public FramingSawBlock()
    {
        super(Properties.of(Material.STONE)
                .requiresCorrectToolForDrops()
                .strength(3.5F)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FramedProperties.FACING_HOR);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, context.getHorizontalDirection());
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (!level.isClientSide())
        {
            NetworkHooks.openGui((ServerPlayer) player, new MenuProvider()
            {
                @Override
                public Component getDisplayName()
                {
                    return MENU_TITLE;
                }

                @Override
                public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
                {
                    return new FramingSawMenu(containerId, inventory, ContainerLevelAccess.create(level, pos));
                }
            });
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        Direction dir = rotation.rotate(state.getValue(FramedProperties.FACING_HOR));
        return state.setValue(FramedProperties.FACING_HOR, dir);
    }
}

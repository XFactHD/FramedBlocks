package xfacthd.framedblocks.common.block.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.menu.FramingSawMenu;

public class FramingSawBlock extends Block
{
    public static final Component SAW_MENU_TITLE = Utils.translate("title", "framing_saw");
    protected static final VoxelShape SHAPE = box(0, 0, 0, 16, 9, 16);

    public FramingSawBlock()
    {
        super(Properties.of()
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .strength(3.5F)
        );
        if (defaultBlockState().hasProperty(PropertyHolder.SAW_ENCODER))
        {
            // Powered saw does not have this property
            registerDefaultState(defaultBlockState().setValue(PropertyHolder.SAW_ENCODER, false));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.SAW_ENCODER);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(FramedProperties.FACING_HOR, context.getHorizontalDirection());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit)
    {
        if (!level.isClientSide())
        {
            player.openMenu(new MenuProvider()
            {
                @Override
                public Component getDisplayName()
                {
                    return getSawMenuTitle();
                }

                @Override
                public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
                {
                    return createSawMenu(containerId, inventory, level, pos);
                }
            }, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        Direction dir = rotation.rotate(state.getValue(FramedProperties.FACING_HOR));
        return state.setValue(FramedProperties.FACING_HOR, dir);
    }

    protected Component getSawMenuTitle()
    {
        return SAW_MENU_TITLE;
    }

    protected AbstractContainerMenu createSawMenu(int containerId, Inventory inventory, Level level, BlockPos pos)
    {
        return FramingSawMenu.create(containerId, inventory, ContainerLevelAccess.create(level, pos));
    }
}

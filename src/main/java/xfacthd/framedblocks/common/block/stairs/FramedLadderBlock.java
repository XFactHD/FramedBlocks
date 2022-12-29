package xfacthd.framedblocks.common.block.stairs;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedLadderBlock extends FramedBlock
{
    private static final VoxelShape SHAPE_NORTH = box( 0, 0,  0, 16, 16,  2);
    private static final VoxelShape COLLISION_SHAPE_NORTH = box( 0, 0,  0, 16, 16,  3);

    private static final VoxelShape[] SHAPES = Util.make(new VoxelShape[4], arr ->
        Direction.Plane.HORIZONTAL.stream().forEach(dir ->
                arr[dir.get2DDataValue()] = Utils.rotateShape(Direction.NORTH, dir, SHAPE_NORTH)
        )
    );
    private static final VoxelShape[] COLLISION_SHAPES = Util.make(new VoxelShape[4], arr ->
        Direction.Plane.HORIZONTAL.stream().forEach(dir ->
                arr[dir.get2DDataValue()] = Utils.rotateShape(Direction.NORTH, dir, COLLISION_SHAPE_NORTH)
        )
    );

    public FramedLadderBlock() { super(BlockType.FRAMED_LADDER); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FramedProperties.FACING_HOR, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = defaultBlockState().setValue(FramedProperties.FACING_HOR, context.getHorizontalDirection());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES[state.getValue(FramedProperties.FACING_HOR).get2DDataValue()];
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return COLLISION_SHAPES[state.getValue(FramedProperties.FACING_HOR).get2DDataValue()];
    }

    @Override
    protected boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos) { return false; }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) { return true; }

    @Override
    public boolean makesOpenTrapdoorAboveClimbable(BlockState state, LevelReader level, BlockPos pos, BlockState trapdoorState)
    {
        return state.getValue(FramedProperties.FACING_HOR) == trapdoorState.getValue(TrapDoorBlock.FACING).getOpposite();
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorFaceBlock(state, mirror);
    }
}
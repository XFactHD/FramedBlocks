package xfacthd.framedblocks.tests;

import com.google.common.base.Preconditions;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.test.TestUtils;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.*;

@GameTestHolder(FramedConstants.MOD_ID)
public final class BeaconTintTests
{
    private static final String BATCH_NAME = "beacon_tint";
    private static final String STRUCTURE_NAME = FramedConstants.MOD_ID + ":empty";

    // All blocks that are completely unable to apply a tint to the beacon beam
    private static final Set<BlockType> NON_TINTING = Set.of(
            BlockType.FRAMED_SLAB_EDGE,
            BlockType.FRAMED_SLAB_CORNER,
            BlockType.FRAMED_PANEL,
            BlockType.FRAMED_CORNER_PILLAR,
            BlockType.FRAMED_FENCE,
            BlockType.FRAMED_GATE,
            BlockType.FRAMED_DOOR,
            BlockType.FRAMED_IRON_DOOR,
            BlockType.FRAMED_LADDER,
            BlockType.FRAMED_BUTTON,
            BlockType.FRAMED_STONE_BUTTON,
            BlockType.FRAMED_LEVER,
            BlockType.FRAMED_SIGN,
            BlockType.FRAMED_WALL_SIGN,
            BlockType.FRAMED_TORCH,
            BlockType.FRAMED_WALL_TORCH,
            BlockType.FRAMED_SOUL_TORCH,
            BlockType.FRAMED_SOUL_WALL_TORCH,
            BlockType.FRAMED_LATTICE_BLOCK,
            BlockType.FRAMED_VERTICAL_STAIRS,
            BlockType.FRAMED_BARS,
            BlockType.FRAMED_PANE,
            BlockType.FRAMED_FLOWER_POT,
            BlockType.FRAMED_POST,
            BlockType.FRAMED_HALF_STAIRS,
            BlockType.FRAMED_SLOPE_PANEL,
            BlockType.FRAMED_DOUBLE_SLOPE_PANEL,
            BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER,
            BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER,
            BlockType.FRAMED_WALL_BOARD,
            BlockType.FRAMED_GATE_DOOR,
            BlockType.FRAMED_IRON_GATE_DOOR
    );

    @GameTestGenerator
    public static Collection<TestFunction> generateBeaconTintTests()
    {
        return Arrays.stream(BlockType.values())
                .filter(type -> !NON_TINTING.contains(type))
                .map(type -> new ResourceLocation(FramedConstants.MOD_ID, type.getName()))
                .map(ForgeRegistries.BLOCKS::getValue)
                .filter(Objects::nonNull)
                .map(BeaconTintTests::getTestState)
                .map(state -> new TestFunction(
                        BATCH_NAME,
                        getTestName(state),
                        STRUCTURE_NAME,
                        100,
                        0,
                        true,
                        helper -> TestUtils.testBeaconBeamTinting(helper, state, getCamoSides(state.getBlock()))
                ))
                .toList();
    }

    private static BlockState getTestState(Block block)
    {
        Preconditions.checkArgument(block instanceof IFramedBlock);

        IBlockType type = ((IFramedBlock) block).getBlockType();
        if (type instanceof BlockType blockType)
        {
            BlockState state = block.defaultBlockState();
            return switch (blockType)
            {
                case FRAMED_PILLAR -> state.setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
                case FRAMED_HALF_PILLAR -> state.setValue(BlockStateProperties.FACING, Direction.DOWN);
                case FRAMED_PRISM -> state.setValue(BlockStateProperties.FACING, Direction.UP);
                case FRAMED_SLOPED_PRISM -> state.setValue(BlockStateProperties.FACING, Direction.UP).setValue(PropertyHolder.ORIENTATION, Direction.NORTH);
                case FRAMED_DOUBLE_STAIRS, FRAMED_VERTICAL_DOUBLE_STAIRS -> state.setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
                case FRAMED_LARGE_BUTTON, FRAMED_LARGE_STONE_BUTTON -> state.setValue(BlockStateProperties.ATTACH_FACE, AttachFace.FLOOR);
                default -> state;
            };
        }
        return block.defaultBlockState();
    }

    private static String getTestName(BlockState state)
    {
        ResourceLocation regName = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        Preconditions.checkState(regName != null);
        return String.format("intangibilitytests.test_%s", regName.getPath());
    }

    private static List<Direction> getCamoSides(Block block)
    {
        Preconditions.checkArgument(block instanceof IFramedBlock);

        IBlockType type = ((IFramedBlock) block).getBlockType();
        if (!type.isDoubleBlock() || !(type instanceof BlockType))
        {
            return List.of(Direction.UP);
        }

        return switch ((BlockType) type)
        {
            case FRAMED_DOUBLE_PANEL,
                 FRAMED_DOUBLE_SLOPE_PANEL,
                 FRAMED_INV_DOUBLE_SLOPE_PANEL,
                 FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> List.of(Direction.NORTH, Direction.SOUTH);

            case FRAMED_VERTICAL_DOUBLE_STAIRS -> List.of(Direction.EAST, Direction.WEST);

            default -> List.of(Direction.UP, Direction.DOWN);
        };
    }
}

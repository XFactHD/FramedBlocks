package xfacthd.framedblocks.tests;

import com.google.common.base.Preconditions;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.test.TestUtils;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.util.ServerConfig;

import java.util.*;

@GameTestHolder(FramedConstants.MOD_ID)
public class IntangibilityTests
{
    private static final String BATCH_NAME = "intangibility";
    private static final String STRUCTURE_NAME = FramedConstants.MOD_ID + ":empty";

    private static boolean intangibility = false;

    @GameTestGenerator
    public static Collection<TestFunction> generateIntangibilityTests()
    {
        return Arrays.stream(BlockType.values())
                .filter(BlockType::allowMakingIntangible)
                .map(type -> new ResourceLocation(FramedConstants.MOD_ID, type.getName()))
                .map(ForgeRegistries.BLOCKS::getValue)
                .filter(Objects::nonNull)
                .map(IntangibilityTests::getTestState)
                .map(state -> new TestFunction(
                        BATCH_NAME,
                        getTestName(state),
                        STRUCTURE_NAME,
                        100,
                        0,
                        true,
                        helper -> TestUtils.testBlockIntangibility(helper, state)
                ))
                .toList();
    }

    @BeforeBatch(batch = BATCH_NAME)
    public static void beforeBatch(ServerLevel level)
    {
        intangibility = ServerConfig.enableIntangibleFeature;
        ServerConfig.enableIntangibleFeature = true;
    }

    @AfterBatch(batch = BATCH_NAME)
    public static void afterBatch(ServerLevel level)
    {
        ServerConfig.enableIntangibleFeature = intangibility;
    }

    private static BlockState getTestState(Block block)
    {
        Preconditions.checkArgument(block instanceof IFramedBlock);

        IBlockType type = ((IFramedBlock) block).getBlockType();
        return block.defaultBlockState();
    }

    private static String getTestName(BlockState state)
    {
        ResourceLocation regName = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        Preconditions.checkState(regName != null);
        return String.format("intangibilitytests.test_%s", regName.getPath());
    }
}

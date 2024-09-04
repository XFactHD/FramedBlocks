package xfacthd.framedblocks.selftest.tests;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.selftest.SelfTestReporter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class BlockEntityPresence
{
    public static void checkBlockEntityTypePresent(SelfTestReporter reporter, List<Block> blocks)
    {
        reporter.startTest("valid BlockEntityType presence");

        blocks.forEach(block ->
        {
            Set<Holder<BlockEntityType<?>>> types = FBContent.getBlockEntities()
                    .stream()
                    .filter(type -> type.value().getValidBlocks().contains(block))
                    .collect(Collectors.toSet());

            IBlockType type = ((IFramedBlock) block).getBlockType();
            if (types.isEmpty())
            {
                reporter.warn(
                        "Block '{}' is not valid for any BE types (double: {}, special: {})",
                        block, type.isDoubleBlock(), type.hasSpecialTile()
                );
            }
            else if (types.size() > 1)
            {
                String typesString = types.stream()
                        .map(Utils::getKeyOrThrow)
                        .map(ResourceKey::location)
                        .map(ResourceLocation::toString)
                        .collect(Collectors.joining(", "));
                reporter.warn(
                        "Block '{}' is valid for multiple BE types: [{}] (double: {}, special: {})",
                        block, typesString, type.isDoubleBlock(), type.hasSpecialTile()
                );
            }
        });

        reporter.endTest();
    }



    private BlockEntityPresence() { }
}

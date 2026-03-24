package io.github.xfacthd.framedblocks.selftest.tests;

import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.selftest.SelfTestReporter;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

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
                        .map(ResourceKey::identifier)
                        .map(Identifier::toString)
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

package xfacthd.framedblocks.common.util;

import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.compat.flywheel.FlywheelCompat;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;

@SuppressWarnings("unused")
public class ApiImpl implements FramedBlocksAPI
{
    @Override
    @SuppressWarnings("removal")
    public String modid() { return FramedConstants.MOD_ID; }

    @Override
    public BlockEntityType<FramedBlockEntity> defaultBlockEntity() { return FBContent.blockEntityTypeFramedBlock.get(); }

    @Override
    public BlockState defaultModelState() { return FBContent.blockFramedCube.get().defaultBlockState(); }

    @Override
    public CreativeModeTab defaultCreativeTab() { return FramedBlocks.FRAMED_TAB; }

    @Override
    @SuppressWarnings("removal")
    public boolean isFramedHammer(ItemStack stack) { return stack.getItem() == FBContent.itemFramedHammer.get(); }

    @Override
    public boolean isFramedDoubleBlockEntity(FramedBlockEntity be) { return be instanceof FramedDoubleBlockEntity; }

    @Override
    public boolean areBlocksFireproof() { return CommonConfig.fireproofBlocks; }

    @Override
    public boolean detailedCullingEnabled() { return ClientConfig.detailedCulling; }

    @Override
    public boolean allowBlockEntities() { return ServerConfig.allowBlockEntities; }

    @Override
    public boolean enableIntangibility() { return ServerConfig.enableIntangibleFeature; }

    @Override
    public Item getIntangibilityMarkerItem() { return ServerConfig.intangibleMarkerItem; }

    @Override
    public void registerBlueprintCopyBehaviour(BlueprintCopyBehaviour behaviour, Block... blocks)
    {
        FramedBlueprintItem.registerBehaviour(behaviour, blocks);
    }

    @Override
    public boolean canHideNeighborFaceInLevel(BlockGetter level) { return !FlywheelCompat.isVirtualLevel(level); }
}
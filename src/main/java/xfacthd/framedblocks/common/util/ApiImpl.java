package xfacthd.framedblocks.common.util;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;

public class ApiImpl implements FramedBlocksAPI
{
    @Override
    public String modid() { return FramedBlocks.MODID; }

    @Override
    public BlockEntityType<FramedBlockEntity> defaultBlockEntity() { return FBContent.blockEntityTypeFramedBlock.get(); }

    @Override
    public BlockState defaultModelState() { return FBContent.blockFramedCube.get().defaultBlockState(); }

    @Override
    public CreativeModeTab defaultCreativeTab() { return FramedBlocks.FRAMED_TAB; }

    @Override
    public boolean isFramedHammer(ItemStack stack) { return stack.getItem() == FBContent.itemFramedHammer.get(); }

    @Override
    public boolean isFramedDoubleBlockEntity(FramedBlockEntity be) { return be instanceof FramedDoubleBlockEntity; }

    @Override
    public boolean areBlocksFireproof() { return CommonConfig.fireproofBlocks; }

    @Override
    public boolean detailedCullingEnabled() { return ClientConfig.detailedCulling; }
}
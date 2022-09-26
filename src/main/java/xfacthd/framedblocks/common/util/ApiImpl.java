package xfacthd.framedblocks.common.util;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.compat.flywheel.FlywheelCompat;
import xfacthd.framedblocks.common.compat.nocubes.NoCubesCompat;
import xfacthd.framedblocks.common.data.camo.CamoFactories;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;

@SuppressWarnings("unused")
public final class ApiImpl implements FramedBlocksAPI
{
    @Override
    public BlockEntityType<FramedBlockEntity> defaultBlockEntity() { return FBContent.blockEntityTypeFramedBlock.get(); }

    @Override
    public BlockState defaultModelState() { return FBContent.blockFramedCube.get().defaultBlockState(); }

    @Override
    public CreativeModeTab defaultCreativeTab() { return FramedBlocks.FRAMED_TAB; }

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
    public IForgeRegistry<CamoContainer.Factory> getCamoContainerFactoryRegistry()
    {
        return FBContent.CAMO_CONTAINER_FACTORY_REGISTRY.get();
    }

    @Override
    public void registerCamoContainerFactory(Item item, CamoContainer.Factory factory)
    {
        CamoFactories.registerCamoFactory(item, factory);
    }

    @Override
    public CamoContainer.Factory emptyCamoContainerFactory() { return FBContent.factoryEmpty.get(); }

    @Override
    public CamoContainer.Factory getCamoContainerFactory(ItemStack stack) { return CamoFactories.getFactory(stack); }

    @Override
    public void registerBlueprintCopyBehaviour(BlueprintCopyBehaviour behaviour, Block... blocks)
    {
        FramedBlueprintItem.registerBehaviour(behaviour, blocks);
    }

    @Override
    public boolean canHideNeighborFaceInLevel(BlockGetter level)
    {
        return !FlywheelCompat.isVirtualLevel(level);
    }

    @Override
    public boolean canCullBlockNextTo(BlockState state, BlockState adjState)
    {
        return !state.is(BlockTags.LEAVES) && NoCubesCompat.mayCullNextTo(adjState);
    }
}
package xfacthd.framedblocks.common.compat.buildinggadgets;

import com.direwolf20.buildinggadgets.common.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.tainted.building.tilesupport.ITileDataSerializer;
import com.direwolf20.buildinggadgets.common.tainted.building.tilesupport.NBTTileEntityData;
import com.direwolf20.buildinggadgets.common.tainted.building.view.BuildContext;
import com.direwolf20.buildinggadgets.common.tainted.inventory.materials.MaterialList;
import com.direwolf20.buildinggadgets.common.tainted.inventory.materials.objects.UniqueItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.util.FramedUtils;
import xfacthd.framedblocks.common.util.ServerConfig;

final class FramedBlockEntityData extends NBTTileEntityData
{
    public FramedBlockEntityData(FramedBlockEntity be) { super(writeBlockEntityTag(be), buildMaterialList(be)); }

    public FramedBlockEntityData(CompoundTag data, MaterialList materials) { super(data, materials); }

    @Override
    public boolean placeIn(BuildContext context, BlockState state, BlockPos pos)
    {
        BuildingGadgets.LOG.trace("Placing {} with Tile NBT at {}.", state, pos);
        context.getWorld().setBlock(pos, state, 0);

        FramedUtils.enqueueImmediateTask(context.getWorld(), () ->
        {
            BlockEntity te = context.getWorld().getBlockEntity(pos);
            if (te != null)
            {
                CompoundTag nbt = getNBT();
                nbt.putInt("x", pos.getX());
                nbt.putInt("y", pos.getY());
                nbt.putInt("z", pos.getZ());

                try
                {
                    te.load(nbt);
                }
                catch (Exception e)
                {
                    BuildingGadgets.LOG.debug("Failed to apply Tile NBT Data to {} at {} in Context {}", state, pos, context, e);
                }
            }
        }, true);

        return true;
    }

    @Override
    public ITileDataSerializer getSerializer() { return BuildingGadgetsCompat.GuardedAccess.FRAMED_SERIALIZER.get(); }

    private static CompoundTag writeBlockEntityTag(FramedBlockEntity be)
    {
        CompoundTag tag = be.writeToBlueprint();

        if (be.getCamo().getType().isFluid())
        {
            tag.remove("camo");
        }
        if (be instanceof FramedDoubleBlockEntity dbe && dbe.getCamoTwo().getType().isFluid())
        {
            tag.remove("camo_two");
        }

        return tag;
    }

    private static MaterialList buildMaterialList(FramedBlockEntity be)
    {
        MaterialList.SimpleBuilder builder = MaterialList.simpleBuilder();

        //Add base materials
        if (be.getBlock() == FBContent.blockFramedDoubleSlab.get())
        {
            builder.add(
                    UniqueItem.ofStack(new ItemStack(FBContent.blockFramedSlab.get())),
                    UniqueItem.ofStack(new ItemStack(FBContent.blockFramedSlab.get()))
            );
        }
        else if (be.getBlock() == FBContent.blockFramedDoublePanel.get())
        {
            builder.add(
                    UniqueItem.ofStack(new ItemStack(FBContent.blockFramedPanel.get())),
                    UniqueItem.ofStack(new ItemStack(FBContent.blockFramedPanel.get()))
            );
        }
        else
        {
            builder.add(UniqueItem.ofStack(new ItemStack(be.getBlockState().getBlock())));
        }

        //Add main camo stack
        if (!be.getCamo().isEmpty() && !be.getCamo().getType().isFluid())
        {
            builder.add(UniqueItem.ofStack(be.getCamo().toItemStack(ItemStack.EMPTY)));
        }

        //Add secondary camo stack
        if (be instanceof FramedDoubleBlockEntity dbe && !dbe.getCamoTwo().isEmpty() && !dbe.getCamoTwo().getType().isFluid())
        {
            builder.add(UniqueItem.ofStack(dbe.getCamoTwo().toItemStack(ItemStack.EMPTY)));
        }

        //Add glowstone
        if (be.isGlowing())
        {
            builder.add(UniqueItem.ofStack(new ItemStack(Items.GLOWSTONE_DUST)));
        }

        //Add intangible marker item
        if (be.isIntangible(CollisionContext.empty()))
        {
            builder.add(UniqueItem.ofStack(new ItemStack(ServerConfig.intangibleMarkerItem)));
        }

        return builder.build();
    }
}
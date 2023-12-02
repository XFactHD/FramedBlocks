package xfacthd.framedblocks.mixin.client;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(LevelRenderer.class)
public interface AccessorLevelRenderer
{
    @Accessor("visibleSections")
    ObjectArrayList<SectionRenderDispatcher.RenderSection> framedblocks$getVisibleSections();

    @Accessor("globalBlockEntities")
    Set<BlockEntity> framedblocks$getGlobalBlockEntities();
}

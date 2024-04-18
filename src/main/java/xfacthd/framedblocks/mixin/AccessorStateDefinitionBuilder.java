package xfacthd.framedblocks.mixin;

import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(StateDefinition.Builder.class)
public interface AccessorStateDefinitionBuilder
{
    @Accessor("properties")
    Map<String, Property<?>> framedblocks$getProperties();
}

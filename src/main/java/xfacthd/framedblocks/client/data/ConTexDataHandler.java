package xfacthd.framedblocks.client.data;

import com.google.common.base.Preconditions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

import java.util.HashSet;
import java.util.Set;

public final class ConTexDataHandler
{
    private static Set<ModelProperty<?>> prelimProperties = new HashSet<>();
    private static boolean locked = false;
    private static ModelProperty<?>[] properties = null;

    public static void lockRegistration()
    {
        Preconditions.checkState(!locked, "ConTexDataHandler is already locked");

        locked = true;
        properties = prelimProperties.toArray(ModelProperty[]::new);
        prelimProperties = null;
    }

    public static void addConTexProperty(ModelProperty<?> property)
    {
        Preconditions.checkState(!locked, "ModelProperty registration is locked");
        Preconditions.checkNotNull(property, "ModelProperty must be non-null");
        prelimProperties.add(property);
    }

    public static Object extractConTexData(ModelData modelData)
    {
        for (ModelProperty<?> prop : properties)
        {
            Object data = modelData.get(prop);
            if (data != null)
            {
                return data;
            }
        }
        return null;
    }



    private ConTexDataHandler() { }
}

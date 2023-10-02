package xfacthd.framedblocks.client.model.door;

import xfacthd.framedblocks.api.model.wrapping.GeometryFactory;

public class FramedIronDoorGeometry extends FramedDoorGeometry
{
    public FramedIronDoorGeometry(GeometryFactory.Context ctx)
    {
        super(ctx);
    }

    @Override
    public boolean useBaseModel()
    {
        return true;
    }
}
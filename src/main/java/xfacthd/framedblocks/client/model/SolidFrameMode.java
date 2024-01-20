package xfacthd.framedblocks.client.model;

public enum SolidFrameMode
{
    NEVER
    {
        @Override
        public boolean useSolidFrame(boolean solidDesired)
        {
            return false;
        }
    },
    DEFAULT
    {
        @Override
        public boolean useSolidFrame(boolean solidDesired)
        {
            return solidDesired;
        }
    },
    ALWAYS
    {
        @Override
        public boolean useSolidFrame(boolean solidDesired)
        {
            return true;
        }
    };

    public abstract boolean useSolidFrame(boolean solidDesired);
}

package xfacthd.framedblocks.client.modelwrapping;

public final class ModelCounter
{
    private int totalCount = 0;
    private int distinctCount = 0;

    public void increment(boolean distinct)
    {
        totalCount++;
        if (distinct)
        {
            distinctCount++;
        }
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public int getDistinctCount()
    {
        return distinctCount;
    }
}

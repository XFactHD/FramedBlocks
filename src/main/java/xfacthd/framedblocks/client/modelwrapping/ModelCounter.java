package xfacthd.framedblocks.client.modelwrapping;

public final class ModelCounter
{
    private int totalCount = 0;
    private int distinctCount = 0;
    private int itemCount = 0;

    public void increment(boolean distinct)
    {
        totalCount++;
        if (distinct)
        {
            distinctCount++;
        }
    }

    public void incrementItem()
    {
        itemCount++;
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public int getDistinctCount()
    {
        return distinctCount;
    }

    public int getItemCount()
    {
        return itemCount;
    }
}

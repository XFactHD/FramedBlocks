package xfacthd.framedblocks.util;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public final class MarkdownTable
{
    private final List<String> header = new ArrayList<>();
    private final BooleanList alignment = new BooleanArrayList();
    private final List<List<String>> rows = new ArrayList<>();
    private List<String> currRow = new ArrayList<>();

    public MarkdownTable header(String cell)
    {
        return header(cell, false);
    }

    public MarkdownTable header(String cell, boolean alignRight)
    {
        header.add(cell.trim());
        alignment.add(alignRight);
        return this;
    }

    public MarkdownTable cell(String cell)
    {
        currRow.add(cell.trim());
        return this;
    }

    public MarkdownTable newRow()
    {
        Preconditions.checkState(
                currRow.size() == header.size(),
                "Row length inconsistent. Expected: %s, got: %s",
                header.size(), currRow.size()
        );

        rows.add(currRow);
        currRow = new ArrayList<>();
        return this;
    }

    public String print()
    {
        if (!currRow.isEmpty())
        {
            newRow();
        }

        int[] lengths = new int[header.size()];
        for (int i = 0; i < header.size(); i++)
        {
            lengths[i] = header.get(i).length();
            for (List<String> row : rows)
            {
                lengths[i] = Math.max(lengths[i], row.get(i).length());
            }
        }

        StringBuilder out = new StringBuilder();

        // Header texts
        out.append("|");
        for (int i = 0; i < header.size(); i++)
        {
            String cell = header.get(i);
            int targetLen = lengths[i];
            printCell(out, cell, targetLen, alignment.getBoolean(i));
        }
        out.append("\n");

        // Header separator
        out.append("|");
        for (int i = 0; i < header.size(); i++)
        {
            boolean alignRight = alignment.getBoolean(i);
            int targetLen = lengths[i];
            if (!alignRight)
            {
                out.append(":");
            }
            out.append("-".repeat(targetLen + 1));
            if (alignRight)
            {
                out.append(":");
            }
            out.append("|");
        }
        out.append("\n");

        // Cell texts
        for (List<String> row : rows)
        {
            out.append("|");
            for (int i = 0; i < row.size(); i++)
            {
                String cell = row.get(i);
                int targetLen = lengths[i];
                printCell(out, cell, targetLen, alignment.getBoolean(i));
            }
            out.append("\n");
        }

        return out.toString();
    }

    private static void printCell(StringBuilder out, String cell, int targetLen, boolean alignRight)
    {
        out.append(" ");

        int diff = targetLen - cell.length();
        if (diff == 0)
        {
            out.append(cell);
        }
        else
        {
            if (alignRight)
            {
                out.append(" ".repeat(diff));
            }
            out.append(cell);
            if (!alignRight)
            {
                out.append(" ".repeat(diff));
            }
        }

        out.append(" |");
    }
}

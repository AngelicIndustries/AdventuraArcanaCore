package industries.angelic.adventuraarcanacore.compat.reccomplex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * The column queues of Recurrent Complex's natural fill (RECURRENT_COMPLEX_NATURAL_FILL). RC asks them whether a column
 * is queued with contains() on an int[], which compares arrays by identity and never finds one, so every column went in
 * again and again and each copy spread further. These queues answer contains() by the column's coordinates.
 */
public final class NaturalFillQueue extends ArrayList<int[]> {

    private final Set<Long> queued = new HashSet<>();

    private static long key(int[] column) {
        return (long) column[0] << 32 | column[1] & 0xFFFFFFFFL;
    }

    @Override
    public boolean contains(Object o) {
        return o instanceof int[] && ((int[]) o).length == 2 && queued.contains(key((int[]) o));
    }

    @Override
    public boolean add(int[] column) {
        queued.add(key(column));
        return super.add(column);
    }

    @Override
    public int[] remove(int index) {
        int[] column = super.remove(index);
        queued.remove(key(column));
        return column;
    }

    @Override
    public void clear() {
        queued.clear();
        super.clear();
    }
}

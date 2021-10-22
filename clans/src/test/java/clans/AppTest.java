package clans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import clans.clan.utils.Loc2di;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        Loc2di loc1 = new Loc2di(1, 2, "world");
        Loc2di loc2 = new Loc2di(1, 2, "world");

        assertTrue("2 identical locations", loc1.equals(loc2));
        assertEquals("2 identical locations", loc1, loc2);
        assertEquals(loc1.hashCode(), loc2.hashCode());

    }

    @Test
    public void testArray() {
        Loc2di loc1 = new Loc2di(1, 2, "world");
        Loc2di loc2 = new Loc2di(1, 3, "world");
        Loc2di loc3 = new Loc2di(1, 4, "world");
        Loc2di loc4 = new Loc2di(1, 5, "world");
        Loc2di loc5 = new Loc2di(0, 6, "world");

        ArrayList<Loc2di> tilesCord = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            tilesCord.add(new Loc2di(1, i, "world"));
        }

        assertTrue(tilesCord.remove(loc1));
        assertTrue(tilesCord.remove(loc2));
        assertTrue(tilesCord.remove(loc3));
        assertTrue(tilesCord.remove(loc4));
        assertFalse(tilesCord.remove(loc5));
    }
}

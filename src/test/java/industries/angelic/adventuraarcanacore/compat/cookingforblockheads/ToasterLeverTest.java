package industries.angelic.adventuraarcanacore.compat.cookingforblockheads;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ToasterLeverTest {

    @Test
    public void leverGoesDownSlowlyAndPopsUpQuickly() {
        float lever = 0;
        for (int tick = 1; tick <= ToasterLever.DOWN_TICKS; tick++) {
            lever = ToasterLever.step(lever, true);
            assertEquals(tick / (float) ToasterLever.DOWN_TICKS, lever, 1e-6F);
        }
        assertEquals(1, ToasterLever.step(lever, true), 0);
        lever = ToasterLever.step(lever, false);
        assertEquals(0.5F, lever, 1e-6F);
        lever = ToasterLever.step(lever, false);
        assertEquals(0, lever, 0);
        assertEquals(0, ToasterLever.step(lever, false), 0);
    }

    @Test
    public void stoppingHalfwayPopsUpFromWhereTheLeverIs() {
        float lever = ToasterLever.step(ToasterLever.step(0, true), true);
        assertEquals(0, ToasterLever.step(ToasterLever.step(lever, false), false), 0);
    }

    @Test
    public void breadMovesLikeCookingForBlockheadsAtEitherEnd() {
        // Cooking for Blockheads drew the bread at 0.025 up and 0.025 - 0.08 down.
        assertEquals(0.025F, ToasterLever.breadY(0), 1e-6F);
        assertEquals(0.025F - 0.08F, ToasterLever.breadY(1), 1e-6F);
    }
}

package testcases;

import control.EventControl;
import entity.Shipment;
import pattern.strategy.FragileGoodsInspectionStrategy;
import pattern.strategy.HazardousInspectionStrategy;
import pattern.strategy.StandardInspectionStrategy;
import java.util.Collections;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.rules.TestName;

public class InspectionStrategyTest {
    private Shipment shipment;
    @Rule
    public TestName testName = new TestName();

    @BeforeClass
    public static void setUpClass() {
        System.out.println("Running InspectionStrategyTest class tests...");
    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println("Finished InspectionStrategyTest class tests.");
    }

    @Before
    public void setUp() {
        shipment = new Shipment("S1", "A", "B", Collections.emptyList());
    }

    @After
    public void tearDown() {
        System.out.println("Done: " + testName.getMethodName());
    }

    @Test
    public void testInspectionStrategies() {
        String standard = new StandardInspectionStrategy().inspect(shipment);
        String fragile = new FragileGoodsInspectionStrategy().inspect(shipment);
        String hazardous = new HazardousInspectionStrategy().inspect(shipment);

        assertEquals("INSPECTED_APPROVED", standard);
        assertEquals("INSPECTED_APPROVED_FRAGILE_HANDLING", fragile);
        assertEquals("INSPECTED_APPROVED_HAZMAT_PROTOCOL", hazardous);
    }

    @Test
    public void testUnsupportedStrategyIsRejected() {
        EventControl control = new EventControl(null);
        try {
            control.setStrategy(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("Inspection strategy is required.", ex.getMessage());
        }
    }
}

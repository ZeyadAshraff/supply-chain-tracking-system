package testcases;

import entity.DeliveryEvidence;
import entity.Good;
import entity.Item;
import entity.Manufacturer;
import entity.Role;
import entity.Shipment;
import entity.events.DeliveryEvent;
import entity.events.DispatchEvent;
import entity.events.Event;
import entity.events.ReceiptEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.rules.TestName;

public class ShipmentStatusTest {
    private Shipment shipment;
    @Rule
    public TestName testName = new TestName();

    @BeforeClass
    public static void setUpClass() {
        System.out.println("Running ShipmentStatusTest class tests...");
    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println("Finished ShipmentStatusTest class tests.");
    }

    @Before
    public void setUp() {
        Manufacturer manufacturer = new Manufacturer("M1", "M", "m@m.com", "x", true, "Co");
        Good good = new Good("G1", "Label", "Desc", manufacturer, 10, 2);
        Item item = new Item("I1", 10, LocalDate.now(), good);
        shipment = new Shipment("S1", "Factory", "Retailer", List.of(item));
    }

    @After
    public void tearDown() {
        System.out.println("Done: " + testName.getMethodName());
    }

    @Test
    public void testStatusDerivedFromLastEvent() {
        shipment.addEvent(new DispatchEvent("E1", LocalDateTime.now(), "Dispatch", "V1"));
        assertEquals("IN_TRANSIT", shipment.getStatus());

        shipment.addEvent(new ReceiptEvent("E2", LocalDateTime.now(), "Receipt", "GOOD"));
        assertEquals("RECEIVED_AT_WAREHOUSE", shipment.getStatus());

        DeliveryEvidence ev = new DeliveryEvidence("D1", "path", LocalDateTime.now(), "UNVERIFIED");
        shipment.addEvent(new DeliveryEvent("E3", LocalDateTime.now(), "Delivery", ev));
        assertEquals("DELIVERED", shipment.getStatus());
    }

    @Test
    public void testInvalidEventOrderIsRejected() {
        try {
            Event.assertCanRecordEvent(Role.SHIPPER, "DELIVERY", shipment.getStatus());
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals("CREATED", shipment.getStatus());
        }
    }
}

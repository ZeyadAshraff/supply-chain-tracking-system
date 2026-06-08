package testcases;

import entity.Good;
import entity.Item;
import entity.Manufacturer;
import java.time.LocalDate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.rules.TestName;

public class ItemQuantityTest {
    private Manufacturer manufacturer;
    private Good good;
    @Rule
    public TestName testName = new TestName();

    @BeforeClass
    public static void setUpClass() {
        System.out.println("Running ItemQuantityTest class tests...");
    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println("Finished ItemQuantityTest class tests.");
    }

    @Before
    public void setUp() {
        manufacturer = new Manufacturer("M1", "M", "m@m.com", "x", true, "Co");
        good = new Good("G1", "Label", "Desc", manufacturer, 10, 2);
    }

    @After
    public void tearDown() {
        System.out.println("Done: " + testName.getMethodName());
    }

    @Test
    public void testIncreaseAndDecreaseItemQuantity() {
        Item item = new Item("I1", 10, LocalDate.now(), good);

        item.increaseQuantity(5);
        assertEquals(15, item.getQuantity());

        item.decreaseQuantity(3);
        assertEquals(12, item.getQuantity());
    }

    @Test
    public void testIncreaseQuantityZeroThrowsAndKeepsState() {
        Item item = createItemWithQuantity(10);
        int before = item.getQuantity();

        try {
            item.increaseQuantity(0);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(before, item.getQuantity());
        }
    }

    @Test
    public void testIncreaseQuantityNegativeThrowsAndKeepsState() {
        Item item = createItemWithQuantity(10);
        int before = item.getQuantity();

        try {
            item.increaseQuantity(-1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(before, item.getQuantity());
        }
    }

    @Test
    public void testDecreaseQuantityZeroThrowsAndKeepsState() {
        Item item = createItemWithQuantity(10);
        int before = item.getQuantity();

        try {
            item.decreaseQuantity(0);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(before, item.getQuantity());
        }
    }

    @Test
    public void testDecreaseQuantityNegativeThrowsAndKeepsState() {
        Item item = createItemWithQuantity(10);
        int before = item.getQuantity();

        try {
            item.decreaseQuantity(-1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(before, item.getQuantity());
        }
    }

    @Test
    public void testDecreaseQuantityGreaterThanCurrentThrowsAndKeepsState() {
        Item item = createItemWithQuantity(10);
        int before = item.getQuantity();

        try {
            item.decreaseQuantity(11);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(before, item.getQuantity());
        }
    }

    private Item createItemWithQuantity(int quantity) {
        return new Item("I1", quantity, LocalDate.now(), good);
    }
}

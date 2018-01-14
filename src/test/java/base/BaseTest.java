package base;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

public class BaseTest {
    protected static ContextForTests context;
    protected static Dummy dummy;

    @Before
    @After
    public void rollback() {
        context.rollback();
    }

    @BeforeClass
    public static void initializeDummy() {
        context = ContextForTests.getInstance();
        dummy = new Dummy(context.getConfiguration());
    }
}

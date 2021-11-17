package htwb.ai;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class ClassProcessorTest {


    @Test
    void populateListWithNormalClass() {

        ClassProcessor c = new ClassProcessor("java.lang.String");
        c.start();

        int totalMethods = c.getNotInvocable().size() + c.getWithRunMe().size() + c.getWithoutRunMe().size();

        assertNotEquals(0, totalMethods);

    }

    @Test
    void populateListWithClassWithoutRunMe() {

        ClassProcessor c = new ClassProcessor("java.lang.String");
        c.start();

        int totalMethods = c.getWithRunMe().size();

        assertEquals(0, totalMethods);

    }

    @Test
    void populateListWithClassWithRunMe() {

        ClassProcessor c = new ClassProcessor("htwb.ai.MeanTestMethods");
        c.start();

        int totalMethods = c.getWithRunMe().size();

        assertNotEquals(0, totalMethods);

    }

    @Test
    void populateListWithClassWithNotInvocables() {

        ClassProcessor c = new ClassProcessor("htwb.ai.MeanTestMethods");
        c.start();

        int totalMethods = c.getNotInvocable().size();

        assertNotEquals(0, totalMethods);

    }

    @Test
    public void checkClassNotFoundClassProcessor() {

        Assertions.assertThrows(ClassNotFoundException.class, () -> {
            ClassProcessor.create("blub");
        });

    }

    @Test
    public void checkNoSuchMethodClassProcessor() {

        Assertions.assertThrows(NoSuchMethodException.class, () -> {
            ClassProcessor.create("java.io.Closeable");
        });

    }

    @Test
    public void checkInstantiateExceptionClassProcessor() {

        Assertions.assertThrows(InstantiationException.class, () -> {
            ClassProcessor.create("java.lang.Number");
        });

    }

    @Test
    public void checkIllegalAccessClassProcessor() {

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            ClassProcessor.create("htwb.ai.PrivateConstructor");
        });

    }

    @Test
    public void checkInvocationTargetExceptionClassProcessor() {

        Assertions.assertThrows(InvocationTargetException.class, () -> {
            ClassProcessor.create("htwb.ai.ContstructorException");
        });

    }


}
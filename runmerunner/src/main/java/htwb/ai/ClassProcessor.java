package htwb.ai;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ClassProcessor {

    private Object processedObject;
    private Class processedClass;
    private List<Method> withoutRunMe = new ArrayList<Method>();
    private List<Method> withRunMe = new ArrayList<Method>();
    private List<String> notInvocable = new ArrayList<String>();

    public List<Method> getWithoutRunMe() {
        return withoutRunMe;
    }

    public List<Method> getWithRunMe() {
        return withRunMe;
    }

    public List<String> getNotInvocable() {
        return notInvocable;
    }

    public ClassProcessor(String processedClass) {
        if (processedClass == null) {
            throw new IllegalArgumentException("Null is not an allowed Class");
        }

        try {
            this.processedObject = create(processedClass);
            this.processedClass = Class.forName(processedClass);

        } catch (ClassNotFoundException e) {
            System.out.println("Class not found!");
            System.exit(0);
        } catch (InstantiationException e) {
            System.out.println("Cannot instantiate class!");
            System.exit(0);
        } catch (IllegalAccessException e) {
            System.out.println("Cannot access class!");
            System.exit(0);
        } catch (IllegalArgumentException e) {
            System.out.println("Illegal Argument!");
            System.exit(0);
        } catch (InvocationTargetException e) {
            System.out.println("Sorry there was an InvocationTargetException!");
            System.exit(0);
        } catch (NoSuchMethodException e) {
            System.out.println("Sorry there is no such Method!");
            System.exit(0);
        }
    }

    /**
     * Creates an instance of a class
     *
     * @param className - the name of class to be instantiated, class must extend Foo
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public static Object create (String className)
            throws InstantiationException, ClassNotFoundException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        Class<?> c = Class.forName(className);
        return  c.getDeclaredConstructor().newInstance();
    }

    public void start() {

        populateList();
        printWithoutRunMe();
        printAndCallWithRunMe();

    }

    public void populateList() {
        Method[] declMethods = this.processedClass.getDeclaredMethods();

        for (Method m : declMethods) {

            if (m.isAnnotationPresent(RunMe.class) == true) {
                this.withRunMe.add(m);
            } else {
                this.withoutRunMe.add(m);
            }

        }
    }

    public void printWithoutRunMe() {

        System.out.println("Methods without @RunMe:");
        withoutRunMe.forEach(x -> System.out.println(x.getName()));

    }

    public void printAndCallWithRunMe() {

        System.out.println("Methods with @RunMe:");
        withRunMe.forEach(x -> {
            System.out.println(x.getName());
            try {
                x.invoke(this.processedClass.newInstance());
            } catch (Exception e) {
                this.notInvocable.add(x.getName() + ": " + e.getClass().getSimpleName());
            }
        });

        if(!notInvocable.isEmpty()) {
            System.out.println("not invocable:");
            notInvocable.forEach(x -> System.out.println(x));
        }

    }

}

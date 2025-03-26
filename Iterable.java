import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.*;

public class IterableMockingExample {
    @Test
    public void testIterableMocking() {
        // Method 1: Using generics to ensure type safety
        Iterable<String> mockIterable = mock(Iterable.class);
        List<String> expectedList = Arrays.asList("apple", "banana", "cherry");
        
        // Stubbing the iterator with type-safe approach
        when(mockIterable.iterator()).thenReturn(expectedList.iterator());

        // Method 2: Using ArgumentMatchers.anyIterable() for type safety
        Iterable<String> anotherMockIterable = mock(Iterable.class);
        List<String> anotherExpectedList = Arrays.asList("dog", "cat", "bird");
        
        // Stubbing with type-safe argument matcher
        when(anotherMockIterable.iterator()).thenReturn(anotherExpectedList.iterator());

        // Method 3: Using Mockito.mock() with explicit type parameter
        Iterable<Integer> typeSafeMockIterable = mock(Iterable.class, withSettings()
            .useConstructor()
            .defaultAnswer(RETURNS_DEEP_STUBS));
        List<Integer> numberList = Arrays.asList(1, 2, 3);
        
        // Stubbing the iterator with type parameter
        when(typeSafeMockIterable.iterator()).thenReturn(numberList.iterator());
    }

    // Example method to show how you might use a mocked Iterable
    private <T> void processIterable(Iterable<T> items) {
        for (T item : items) {
            // Process each item
            System.out.println(item);
        }
    }
}

package task2;

import lombok.Value;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import task2.cp.PropsBinder;

import static org.junit.jupiter.api.Assertions.*;

class PropsBinderTest {

    @Test
    @DisplayName("From method works correctly")
    void testFrom() {
        @Value
        class Props {
            int prop1;
            String prop2;
        }
        val props = PropsBinder.from(Props.class);
        assertEquals(props.getProp1(), 50);
        assertEquals(props.getProp2(), "qwerty!");
    }
}
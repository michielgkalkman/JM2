package jmemorize.util;

import java.util.function.Consumer;

public class Tap {

    public static <T> T tap(T object, Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }
}

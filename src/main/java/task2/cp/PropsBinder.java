package task2.cp;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

public interface PropsBinder {

    static <T> T from(Class<T> tClass) {
        return from(tClass.getSimpleName(), tClass);
    }

    @SneakyThrows
    static <T> T from(String fileName, Class<T> tClass) {
        val properties = new Properties(); //считать property
        @Cleanup val inputstream = PropsBinder.class.getResourceAsStream(String.format("/%s.properties", fileName));
        properties.load(inputstream);

        Constructor<T> constructor = (Constructor<T>) Arrays.stream(tClass.getConstructors()) //из переданного класса вытаскиваем наибольший по количеству параметров конструктор
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow(() -> new RuntimeException("Нет ни одного конструктора!"));

        Object[] paramValues = Arrays.stream(constructor.getParameters()) //соответсвие между параметрами конструктора и значениями из пропертей
                .map(parameter -> resolveParameter(parameter,
                        properties.getProperty(parameter.getName())))
                .toArray();
        return constructor.newInstance(paramValues);
    }

    private static Object resolveParameter(Parameter parameter, String value) {
        Class<?> parameterType = parameter.getType();
        if (parameterType == String.class) {
            return value;
        }
        if (parameterType == int.class || parameterType == Integer.class) {
            return Integer.parseInt(value);
        }
        if (parameterType == double.class || parameterType == Double.class) {
            return Double.parseDouble(value);
        }
        if (parameterType == long.class || parameterType == Long.class) {
            return Long.parseLong(value);
        }
        return value;
        //TODO остальные примитивы и ссылочные типы
    }
}

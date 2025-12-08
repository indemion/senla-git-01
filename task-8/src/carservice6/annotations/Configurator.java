package carservice6.annotations;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

public class Configurator {
    private final Map<String, Properties> loadedProperties = new HashMap<>();

    public void configure(Object instance) {
        Class<?> clazz = instance.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                ConfigProperty configProperty = field.getAnnotation(ConfigProperty.class);
                String configFileName = configProperty.configFileName();
                String propertyName = configProperty.propertyName();
                Class<?> propertyType = configProperty.type();
                Type fieldGenericType = field.getGenericType();
                if (propertyType.equals(Void.class)) {
                    propertyType = field.getType();
                }

                if (propertyName.isEmpty()) {
                    propertyName = clazz.getSimpleName() + "." + field.getName();
                }

                Properties props = loadProperties(configFileName);
                String value = props.getProperty(propertyName);

                if (value == null) {
                    throw new PropertyNotFoundException("Значение свойства: " + propertyName + ", не найдено в файле: " +
                            configFileName);
                }

                Object convertedValue = convertValue(value, propertyType, fieldGenericType);
                field.setAccessible(true);
                try {
                    field.set(instance, convertedValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Object convertValue(String value, Class<?> targetType, Type fieldGenericType) {
        if (targetType.isArray()) {
            Class<?> componentType = targetType.getComponentType();
            String[] parts = value.split(",");
            Object array = Array.newInstance(componentType, parts.length);
            for (int i = 0; i < parts.length; i++) {
                Array.set(array, i, convertSimpleValue(parts[i], componentType));
            }
            return array;
        }

        if (Collection.class.isAssignableFrom(targetType)) {
            Class<?> collectionClass = targetType;
            Class<?> elementType = getCollectionElementType(fieldGenericType);
            if (collectionClass.isInterface()) {
                if (collectionClass == List.class) {
                    collectionClass = ArrayList.class;
                } else if (collectionClass == Set.class) {
                    collectionClass = HashSet.class;
                } else {
                    throw new IllegalArgumentException("Неподдерживаемый тип коллекции: " + targetType.getName());
                }
            }
            String[] parts = value.split(",");

            try {
                Collection<Object> collection = (Collection<Object>) collectionClass.getDeclaredConstructor()
                        .newInstance();
                for (String part : parts) {
                    collection.add(convertSimpleValue(part, elementType));
                }
                return collection;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        return convertSimpleValue(value, targetType);
    }

    private Class<?> getCollectionElementType(Type fieldGenericType) {
        Class<?> elementType = String.class;
        if (fieldGenericType instanceof ParameterizedType parameterizedType) {
            Type typeArgument = parameterizedType.getActualTypeArguments()[0];
            if (typeArgument instanceof Class<?> clazz) {
                elementType = clazz;
            }
        }
        return elementType;
    }

    private Object convertSimpleValue(String value, Class<?> targetType) {
        if (targetType.equals(String.class)) {
            return value;
        } else if (targetType.equals(byte.class) || targetType.equals(Byte.class)) {
            return Byte.parseByte(value);
        } else if (targetType.equals(short.class) || targetType.equals(Short.class)) {
            return Short.parseShort(value);
        } else if (targetType.equals(int.class) || targetType.equals(Integer.class)) {
            return Integer.parseInt(value);
        } else if (targetType.equals(long.class) || targetType.equals(Long.class)) {
            return Long.parseLong(value);
        } else if (targetType.equals(float.class) || targetType.equals(Float.class)) {
            return Float.parseFloat(value);
        } else if (targetType.equals(double.class) || targetType.equals(Double.class)) {
            return Double.parseDouble(value);
        } else if (targetType.equals(char.class) || targetType.equals(Character.class)) {
            if (!value.isEmpty()) {
                return value.charAt(0);
            } else {
                throw new IllegalArgumentException("Невозможно конвертировать пустую строку в char");
            }
        } else if (targetType.equals(boolean.class) || targetType.equals(Boolean.class)) {
            return Boolean.parseBoolean(value);
        }

        throw new IllegalArgumentException("Неподдерживаемый тип для конвертации: " + targetType.getName());
    }

    private Properties loadProperties(String configFileName) {
        Properties props = loadedProperties.get(configFileName);
        if (props != null) {
            return props;
        }

        props = new Properties();
        try(FileReader fr = new FileReader(configFileName)) {
            props.load(fr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return props;
    }
}

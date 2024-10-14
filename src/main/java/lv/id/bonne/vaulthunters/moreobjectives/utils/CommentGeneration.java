package lv.id.bonne.vaulthunters.moreobjectives.utils;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import lv.id.bonne.vaulthunters.moreobjectives.configs.annotations.JsonComment;


/**
 * This util class handles object serialization into string and adding comments from annotation.
 */
public class CommentGeneration
{

    /**
     * This method generates JSON text from given object but adds comments from @JsonComment annotation.
     */
    public static String writeWithComments(ObjectMapper mapper, Object obj)
        throws JsonProcessingException, IllegalAccessException
    {
        StringBuilder jsonWithComments = new StringBuilder();
        jsonWithComments.append("{\n");

        // Recursive method to process objects and their nested fields
        serializeObjectWithComments(obj, jsonWithComments, mapper, 1);

        jsonWithComments.append("\n}");

        return jsonWithComments.toString();
    }


    /**
     * Main method that serializes object and adds comments.
     */
    private static void serializeObjectWithComments(Object obj,
        StringBuilder jsonWithComments,
        ObjectMapper mapper,
        int indentLevel) throws IllegalAccessException, JsonProcessingException
    {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        String indent = "  ".repeat(indentLevel);
        boolean first = true;

        for (int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];
            field.setAccessible(true);

            if (field.isAnnotationPresent(JsonIgnore.class))
            {
                // Skip this field if it's marked to be ignored
                continue;
            }

            if (!first)
            {
                jsonWithComments.append(",");
                jsonWithComments.append("\n");
            }

            first = false;

            String jsonPropertyName = field.getName();

            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            if (jsonProperty != null)
            {
                // Use the value specified in @JsonProperty
                jsonPropertyName = jsonProperty.value();
            }

            JsonComment[] comment = field.getDeclaredAnnotationsByType(JsonComment.class);

            if (comment != null)
            {
                for (JsonComment jsonComment : comment)
                {
                    jsonWithComments.append(indent).append("// ").append(jsonComment.value()).append("\n");
                }
            }

            jsonWithComments.append(indent).append("\"").append(jsonPropertyName).append("\": ");

            Object fieldValue = field.get(obj);

            if (fieldValue != null)
            {
                if (field.isAnnotationPresent(JsonSerialize.class))
                {
                    // If the field is a primitive or String, serialize directly
                    jsonWithComments.append(mapper.writeValueAsString(field.get(obj)));
                }
                else if (fieldValue instanceof Collection<?> collection)
                {
                    // Handle Collection (List, Set)
                    jsonWithComments.append("[\n");
                    serializeCollection(collection, jsonWithComments, mapper, indentLevel + 1);
                    jsonWithComments.append(indent).append("]");
                }
                else if (fieldValue instanceof Map<?, ?> map)
                {
                    // Handle Map
                    jsonWithComments.append("{\n");
                    serializeMap(map, jsonWithComments, mapper, indentLevel + 1);
                    jsonWithComments.append(indent).append("}");
                }
                else if (isNotPrimitive(fieldValue))
                {
                    // Serialize object as recursive object.
                    jsonWithComments.append("{\n");
                    serializeObjectWithComments(fieldValue, jsonWithComments, mapper, indentLevel + 1);
                    jsonWithComments.append("\n");
                    jsonWithComments.append(indent).append("}");
                }
                else
                {
                    // If the field is a primitive or String, serialize directly
                    jsonWithComments.append(mapper.writeValueAsString(field.get(obj)));
                }
            }
        }
    }


    /**
     * This method serializes collections into json text with comments.
     */
    private static void serializeCollection(Collection<?> collection,
        StringBuilder jsonWithComments,
        ObjectMapper mapper,
        int indentLevel)
        throws JsonProcessingException, IllegalAccessException
    {
        String indent = "  ".repeat(indentLevel);
        int count = 0;
        for (Object item : collection)
        {
            jsonWithComments.append(indent);

            if (item instanceof Collection<?> collection2)
            {
                // Handle Collection (List, Set)
                jsonWithComments.append("[\n");
                serializeCollection(collection2, jsonWithComments, mapper, indentLevel + 1);
                jsonWithComments.append(indent).append("]");
            }
            else if (item instanceof Map<?, ?> map)
            {
                // Handle Map
                jsonWithComments.append("{\n");
                serializeMap(map, jsonWithComments, mapper, indentLevel + 1);
                jsonWithComments.append(indent).append("}");
            }
            else if (isNotPrimitive(item))
            {
                // Serialize object as recursive object.
                jsonWithComments.append("{\n");
                serializeObjectWithComments(item, jsonWithComments, mapper, indentLevel + 1);
                jsonWithComments.append("\n");
                jsonWithComments.append(indent).append("}");
            }
            else
            {
                // If the field is a primitive or String, serialize directly
                jsonWithComments.append(mapper.writeValueAsString(item));
            }

            if (++count < collection.size())
            {
                jsonWithComments.append(",");
            }

            jsonWithComments.append("\n");
        }
    }


    /**
     * This method serializes map entries into json text with comments.
     */
    private static void serializeMap(Map<?, ?> map,
        StringBuilder jsonWithComments,
        ObjectMapper mapper,
        int indentLevel)
        throws JsonProcessingException, IllegalAccessException
    {
        String indent = "  ".repeat(indentLevel);
        int count = 0;

        for (Map.Entry<?, ?> entry : map.entrySet())
        {
            jsonWithComments.append(indent).append("\"").append(entry.getKey()).append("\": ");
            Object value = entry.getValue();

            if (value instanceof Collection<?> collection)
            {
                // Handle Collection (List, Set)
                jsonWithComments.append("[\n");
                serializeCollection(collection, jsonWithComments, mapper, indentLevel + 1);
                jsonWithComments.append(indent).append("]");
            }
            else if (value instanceof Map<?, ?> map2)
            {
                // Handle Map
                jsonWithComments.append("{\n");
                serializeMap(map2, jsonWithComments, mapper, indentLevel + 1);
                jsonWithComments.append(indent).append("}");
            }
            else if (isNotPrimitive(value))
            {
                // Serialize object as recursive object.
                jsonWithComments.append("{\n");
                serializeObjectWithComments(value, jsonWithComments, mapper, indentLevel + 1);
                jsonWithComments.append("\n");
                jsonWithComments.append(indent).append("}");
            }
            else
            {
                // If the field is a primitive or String, serialize directly
                jsonWithComments.append(mapper.writeValueAsString(value));
            }

            if (++count < map.size())
            {
                jsonWithComments.append(",");
            }

            jsonWithComments.append("\n");
        }
    }


    /**
     * This method returns if an object is not primitive.
     */
    private static boolean isNotPrimitive(Object obj)
    {
        return !(obj instanceof String) &&
            !obj.getClass().isPrimitive() &&
            !(obj instanceof Number) &&
            !(obj instanceof Boolean);
    }
}

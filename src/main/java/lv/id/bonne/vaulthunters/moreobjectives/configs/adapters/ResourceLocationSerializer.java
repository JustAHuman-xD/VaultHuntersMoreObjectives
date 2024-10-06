package lv.id.bonne.vaulthunters.moreobjectives.configs.adapters;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;


/**
 * The resource location serializer
 */
public class ResourceLocationSerializer extends JsonSerializer<ResourceLocation>
{
    @Override
    public void serialize(ResourceLocation value, JsonGenerator gen, SerializerProvider serializers) throws IOException
    {
        if (value == null)
        {
            gen.writeNull();
        }
        else
        {
            gen.writeString(value.toString());
        }
    }
}

package lv.id.bonne.vaulthunters.moreobjectives.configs.adapters;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;


/**
 * The ResourceLocation deserializer.
 */
public class ResourceLocationDeserializer extends JsonDeserializer<ResourceLocation>
{
    @Override
    public ResourceLocation deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        return new ResourceLocation(parser.getText());
    }
}

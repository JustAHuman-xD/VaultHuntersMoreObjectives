package lv.id.bonne.vaulthunters.moreobjectives.configs.adapters;


import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import net.minecraft.resources.ResourceLocation;


public class ResourceLocationAdapter extends TypeAdapter<ResourceLocation>
{
    @Override
    public void write(JsonWriter out, ResourceLocation value) throws IOException
    {
        if (value == null)
        {
            out.nullValue();
        }
        else
        {
            out.value(value.toString());
        }
    }


    @Override
    public ResourceLocation read(JsonReader in) throws IOException
    {
        String locationString = in.nextString();
        return new ResourceLocation(locationString);
    }
}
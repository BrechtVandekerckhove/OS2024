package utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import domain.Sport;

public class SportSerializer extends JsonSerializer<Sport> {

	@Override
	public void serialize(Sport value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartObject();
		gen.writeStringField("sport_id", Integer.toString(value.getSportID()));
		gen.writeStringField("sport_name", value.getSportsName());
		gen.writeEndObject();
	}

}

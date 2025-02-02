package utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import domain.Sport;

public class SportDeserializer extends JsonDeserializer<Sport> {

	@Override
	public Sport deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		JsonNode node = p.getCodec().readTree(p);
		String sportsName = node.get("sport_name").asText();
		return new Sport(sportsName);
	}

}

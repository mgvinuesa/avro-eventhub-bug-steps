package com.example.demo;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.example.avro.Example;
import com.microsoft.azure.schemaregistry.kafka.avro.KafkaAvroDeserializer;
import com.microsoft.azure.schemaregistry.kafka.avro.KafkaAvroSerializer;

public class AvroSerDesOldVersionTest {

	private static final boolean AUTO_REGISTER_SCHEMA = true;
	private static final String SCHEMA_GROUP = "ImageSchemaRegistry";
	private static final String URL_SCHEMA_REGISTRY = "https://*********.servicebus.windows.net";
	private static final String CLIENT_SECRET_SCHEMA_REGISTRY = "1gV6-***********************";
	private static final String TENANT_ID_SCHEMA_REGISTRY = "a9a8e375************************";
	private static final String CLIENT_ID_SCHEMA_REGISTRY = "020a3ea0************************";

	private KafkaAvroDeserializer avroDeserializer = new KafkaAvroDeserializer();
	private KafkaAvroSerializer avroSerializer = new KafkaAvroSerializer();

	@BeforeEach
	public void setUp() {

		ClientSecretCredential azureCredential = new ClientSecretCredentialBuilder().clientId(CLIENT_ID_SCHEMA_REGISTRY)
				.tenantId(TENANT_ID_SCHEMA_REGISTRY).clientSecret(CLIENT_SECRET_SCHEMA_REGISTRY).build();

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("schema.registry.url", URL_SCHEMA_REGISTRY);
		properties.put("schema.group", SCHEMA_GROUP);
		properties.put("auto.register.schemas", AUTO_REGISTER_SCHEMA);

		properties.put("schema.registry.credential", azureCredential);

		avroDeserializer.configure(properties, false);
		avroSerializer.configure(properties, false);
	}

	@Test
	public void testSerDes() {
		Example example = new Example();
		example.setLocation("Badajoz");
		example.setName("Manuel");
		example.setSurname("Garcia");
		Headers headers = new RecordHeaders();
		byte[] body = avroSerializer.serialize("TOPIC", headers, example);

		avroDeserializer.deserialize("TOPIC", headers, body);

		String base64bytes = new String(Base64.getEncoder().encode(body));

	}
}

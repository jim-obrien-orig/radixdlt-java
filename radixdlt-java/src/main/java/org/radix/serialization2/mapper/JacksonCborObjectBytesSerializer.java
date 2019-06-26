package org.radix.serialization2.mapper;

import java.io.IOException;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

class JacksonCborObjectBytesSerializer<T> extends StdSerializer<T> {
	private static final long serialVersionUID = 290L;

	private final byte prefix;
	private final Function<T, byte[]> toByteArrayMapper;

	JacksonCborObjectBytesSerializer(Class<T> t, byte prefix, Function<T, byte[]> toByteArrayMapper) {
		super(t);
		this.prefix = prefix;
		this.toByteArrayMapper = toByteArrayMapper;
	}

	@Override
	public void serialize(T value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		byte[] objectBytes = toByteArrayMapper.apply(value);
		byte[] bytes = new byte[1 + objectBytes.length];

		bytes[0] = prefix;
		System.arraycopy(objectBytes, 0, bytes, 1, objectBytes.length);
		jgen.writeBinary(bytes);
	}
}

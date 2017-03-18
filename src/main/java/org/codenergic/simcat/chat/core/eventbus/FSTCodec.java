package org.codenergic.simcat.chat.core.eventbus;

import org.nustaq.serialization.FSTConfiguration;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class FSTCodec implements MessageCodec<Object, Object> {
	public static final String CODEC_NAME = FSTCodec.class.getName();
	private FSTConfiguration conf;

	public FSTCodec(FSTConfiguration conf) {
		this.conf = conf;
	}

	@Override
	public void encodeToWire(Buffer buffer, Object s) {
		byte[] obj = conf.asByteArray(s);
		buffer.appendInt(obj.length);
		buffer.appendBytes(obj);
	}

	@Override
	public Object decodeFromWire(final int pos, Buffer buffer) {
		int length = buffer.getInt(pos);
		int currentPos = pos + 4;
		byte[] obj = buffer.getBytes(currentPos, currentPos + length);

		return conf.asObject(obj);
	}

	@Override
	public Object transform(Object s) {
		return conf.deepCopy(s);
	}

	@Override
	public String name() {
		return FSTCodec.CODEC_NAME;
	}

	@Override
	public byte systemCodecID() {
		return -1;
	}
}

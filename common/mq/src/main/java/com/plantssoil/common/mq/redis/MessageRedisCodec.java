package com.plantssoil.common.mq.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.plantssoil.common.mq.IMessage;
import com.plantssoil.common.mq.SimpleMessage;
import com.plantssoil.common.mq.exception.MessageQueueException;

import io.lettuce.core.codec.RedisCodec;

/**
 * <Channel, IMessage> decode and encode, required by Redis to transfer
 * serialized Message Object
 * 
 * @author danialdy
 * @Date 6 Nov 2024 4:28:19 pm
 */
public class MessageRedisCodec implements RedisCodec<String, IMessage> {

    @Override
    public String decodeKey(ByteBuffer bytes) {
        try {
            return new String(bytes.array(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15001, e);
        }
    }

    @Override
    public IMessage decodeValue(ByteBuffer bytes) {
        try (ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes.array()); ObjectInputStream ois = new ObjectInputStream(bytesIn)) {
            SimpleMessage message = (SimpleMessage) ois.readObject();
            return message;
        } catch (IOException | ClassNotFoundException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15001, e);
        }
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        try {
            return ByteBuffer.wrap(key.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15001, e);
        }
    }

    @Override
    public ByteBuffer encodeValue(IMessage value) {
        try (ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bytesOut)) {
            oos.writeObject(value);
            oos.flush();
            byte[] bytes = bytesOut.toByteArray();
            return ByteBuffer.wrap(bytes);
        } catch (IOException e) {
            throw new MessageQueueException(MessageQueueException.BUSINESS_EXCEPTION_CODE_15001, e);
        }
    }

}

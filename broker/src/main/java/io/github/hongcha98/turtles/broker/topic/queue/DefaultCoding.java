package io.github.hongcha98.turtles.broker.topic.queue;

import io.github.hongcha98.remote.common.spi.SpiDescribe;
import io.github.hongcha98.remote.common.spi.SpiLoader;
import io.github.hongcha98.remote.protocol.Protocol;
import io.github.hongcha98.turtles.broker.constant.Constant;
import io.github.hongcha98.turtles.common.dto.message.Message;
import io.github.hongcha98.turtles.common.dto.message.MessageInfo;

import java.nio.ByteBuffer;

/**
 * 默认编解码
 */
@SpiDescribe(name = "turtles")
public class DefaultCoding implements Coding {
    @Override
    public byte[] encode(Message message) {
        byte[] bytes = SpiLoader.load(Protocol.class, Constant.PROTOCOL_CODE).encode(message);
        ByteBuffer byteBuffer = ByteBuffer.allocate(Constant.MESSAGE_LENGTH + bytes.length);
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes);
        return byteBuffer.array();
    }

    @Override
    public MessageInfo decode(ByteBuffer byteBuffer, int offset) {
        /**
         * 获取旧的position，后面设置回去
         */
        int oldPosition = byteBuffer.position();
        try {
            byteBuffer.position(offset);
            /**
             * 读取消息长度
             */
            int length = byteBuffer.getInt(offset);
            if (length == 0) return null;
            byteBuffer.position(offset + Constant.MESSAGE_LENGTH);
            byte[] bytes = new byte[length];
            byteBuffer.get(bytes);
            Message message = SpiLoader.load(Protocol.class, Constant.PROTOCOL_CODE).decode(bytes, Message.class);
            int nextOffset = offset + Constant.MESSAGE_LENGTH + length;
            return new MessageInfo(message, offset, nextOffset);
        } finally {
            byteBuffer.position(oldPosition);
        }
    }
}
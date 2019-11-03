package com.freeman.common.utils.serializer;

import org.nustaq.serialization.FSTConfiguration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *	Object序列化、反序列化、克隆
 */
public class ObjectSerializer<T> implements org.springframework.data.redis.serializer.RedisSerializer<T>,org.crazycake.shiro.serializer.RedisSerializer<T> {


	/** JDK 序列化对象 */
	public static byte[] serializeByJdk(Object object) {
		if (object == null){ return null; }

		byte[] bytes = null;
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos))
		{
			oos.writeObject(object);
			bytes = baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bytes;
	}

	/** JDK 反序列化对象 */
	public static Object deserializeByJdk(byte[] bytes) {
		if (bytes == null){ return null; }

		Object object = null;
		try(ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais))
		{
			if (bytes.length > 0) {
				object = ois.readObject();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}

	//==============================FST序列化对象======================================//
	private static FSTConfiguration fst = FSTConfiguration.createDefaultConfiguration();

	/** FST 序列化对象 **/
	public static byte[] serializeByFst(Object object) {
		return object == null ? new byte[0] : fst.asByteArray(object);
	}

	/** FST 反序列化对象 **/
	public static Object deserializeByFst(byte[] bytes) {
		return bytes == null ? null : fst.asObject(bytes);
	}

	/** 克隆一个对象（完全拷贝）**/
	public static Object cloneBeanByFst(Object source){
		return source == null ? null : deserializeByFst(serializeByFst(source));
	}


	@Override
	public byte[] serialize(T t) throws SerializationException {
		return t == null ? new byte[0] : fst.asByteArray(t);
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		return (bytes == null || bytes.length <= 0) ? null : (T)fst.asObject(bytes);
	}

	public T clone(T source){
		return source == null ? null : deserialize(serialize(source));
	}





	
}

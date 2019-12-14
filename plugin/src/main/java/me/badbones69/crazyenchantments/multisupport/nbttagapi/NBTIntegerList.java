package me.badbones69.crazyenchantments.multisupport.nbttagapi;

import me.badbones69.crazyenchantments.multisupport.nbttagapi.utils.nmsmappings.ClassWrapper;
import me.badbones69.crazyenchantments.multisupport.nbttagapi.utils.nmsmappings.ReflectionMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Integer implementation for NBTLists
 *
 * @author tr7zw
 *
 */
public class NBTIntegerList extends NBTList<Integer> {
	
	protected NBTIntegerList(NBTCompound owner, String name, NBTType type, Object list) {
		super(owner, name, type, list);
	}
	
	@Override
	protected Object asTag(Integer object) {
		try {
			Constructor<?> con = ClassWrapper.NMS_NBTTAGINT.getClazz().getDeclaredConstructor(int.class);
			con.setAccessible(true);
			return con.newInstance(object);
		}catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
		| NoSuchMethodException | SecurityException e) {
			throw new NbtApiException("Error while wrapping the Object " + object + " to it's NMS object!", e);
		}
	}
	
	@Override
	public Integer get(int index) {
		try {
			Object obj = ReflectionMethod.LIST_GET.run(listObject, index);
			return Integer.valueOf(obj.toString());
		}catch(NumberFormatException nf) {
			return 0;
		}catch(Exception ex) {
			throw new NbtApiException(ex);
		}
	}
	
}

package agency.highlysuspect.teavmfun;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSByRef;
import org.teavm.jso.JSExport;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

public class Built implements Opcodes {
	public static void main(String[] args) throws Exception {
		logString("Hello, world!");
	}

	@JSExport
	public static void dumpHier(byte[] bytes, JSStringConsumer appender) throws Exception {
		//TreeMap<String, Entry> entries = new TreeMap<>();

		try(ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(bytes))) {
			ZipEntry entry;
			while((entry = zip.getNextEntry()) != null) {
				if(entry.getName().endsWith(".class")) {
					ClassReader reader = new ClassReader(zip);
					reader.accept(new ClassVisitor(ASM9) {
						@Override
						public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
							//superName can be null for module-info classes and java/lang/Object
							if(superName != null) {
								StringBuilder bob = new StringBuilder()
									.append(name)
									.append(',')
									.append(superName);
								for(String itf : interfaces) {
									bob.append(',').append(itf);
								}

								appender.accept(bob.toString());
							}

							super.visit(version, access, name, signature, superName, interfaces);
						}
					}, ClassReader.SKIP_CODE);
				}
			}
		}
	}

	@JSFunctor
	public interface JSStringConsumer extends JSObject {
		void accept(String s);
	}

	record Entry(String superName, String... interfaces) {}

	@JSExport
	@JSByRef
	public static byte[] makeJar() throws Exception {
		ClassNode node = new ClassNode(Opcodes.ASM9);
		node.name = "MyClass";
		node.superName = "java/lang/Object";
		node.access = ACC_PUBLIC | ACC_SUPER;

		MethodNode clinit = new MethodNode(ASM9, ACC_PUBLIC | ACC_STATIC | ACC_SYNTHETIC, "<clinit>", "()V", null, null);
		clinit.instructions.add(new InsnNode(RETURN));
		clinit.maxLocals = clinit.maxStack = 0;
		node.methods.add(clinit);

		FieldNode fieldNode = new FieldNode(ASM9, ACC_PUBLIC | ACC_STATIC, "coolField", "I", null, 12345);
		node.fields.add(fieldNode);

		ClassWriter writer = new ClassWriter(0);
		node.accept(writer);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try(ZipOutputStream zout = new ZipOutputStream(out)) {
			zout.setLevel(9);
			zout.putNextEntry(new ZipEntry("MyClass.class"));
			write(zout, writer.toByteArray());
		}

		return out.toByteArray();
	}

	private static void write(OutputStream out, byte[] bytes) throws IOException {
		out.write(bytes, 0, bytes.length);
	}

	@JSBody(params = "message", script = "console.log(message);")
	public static native void logString(String s);

	@JSBody(params = "arr", script = "console.log(arr);")
	public static native void logArray(@JSByRef byte[] array);

	//Javascript's Uint8Array constructor will convert from signed to unsigned for me. That's nice
	@JSBody(params = "arr", script = "window.GLOBAL_ARRAY_THING = new Uint8Array(arr);")
	public static native void setGlobalVariableLol(@JSByRef byte[] array);
}

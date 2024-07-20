package agency.highlysuspect.teavmfun;

import org.teavm.classlib.impl.JCLPlugin;
import org.teavm.vm.spi.After;
import org.teavm.vm.spi.TeaVMHost;
import org.teavm.vm.spi.TeaVMPlugin;

@After(JCLPlugin.class)
public class MyPlugin implements TeaVMPlugin {
	@Override
	public void install(TeaVMHost host) {
		//huh i guess i didn't actually need a plugin
	}
}

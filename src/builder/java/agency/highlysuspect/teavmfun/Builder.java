package agency.highlysuspect.teavmfun;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.teavm.backend.javascript.JSModuleType;
import org.teavm.tooling.ConsoleTeaVMToolLog;
import org.teavm.tooling.TeaVMProblemRenderer;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.tooling.TeaVMTool;
import org.teavm.vm.TeaVMOptimizationLevel;
import org.teavm.vm.TeaVMPhase;
import org.teavm.vm.TeaVMProgressFeedback;
import org.teavm.vm.TeaVMProgressListener;

public class Builder {
	public static void main(String[] args) throws Exception {
		System.out.println("cwd: " + Paths.get(".").toAbsolutePath().normalize());

		Path in = Paths.get(System.getProperty("in")).toAbsolutePath().normalize();
		System.out.println("in: " + in);

		Path out = Paths.get(System.getProperty("out")).toAbsolutePath().normalize();
		System.out.println("out: " + out);

		TeaVMTool tool = new TeaVMTool();

		tool.setLog(new ConsoleTeaVMToolLog(true));

		tool.setTargetType(TeaVMTargetType.JAVASCRIPT);
		tool.setMainClass("agency.highlysuspect.teavmfun.Built");
		tool.setObfuscated(true);
		tool.setOptimizationLevel(TeaVMOptimizationLevel.FULL);

		tool.setJsModuleType(JSModuleType.ES2015);

		tool.setTargetDirectory(out.toFile());
		tool.setTargetFileName("classes.mjs");

		tool.setProgressListener(new TeaVMProgressListener() {
			@Override
			public TeaVMProgressFeedback phaseStarted(TeaVMPhase phase, int count) {
				System.out.println(phase.name() + "...");
				return TeaVMProgressFeedback.CONTINUE;
			}

			@Override
			public TeaVMProgressFeedback progressReached(int progress) {
				return TeaVMProgressFeedback.CONTINUE;
			}
		});
		tool.generate();

		//magic
		TeaVMProblemRenderer.describeProblems(tool.getDependencyInfo().getCallGraph(), tool.getProblemProvider(), tool.getLog());
	}
}

package com.widerwille.quicklook;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.xdebugger.frame.XFullValueEvaluator;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.jetbrains.cidr.execution.debugger.CidrDebugProcess;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriver;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class QuickLookFullValueEvaluator<T> extends XFullValueEvaluator
{
	private CidrDebugProcess process;
	private QuickLookValueRenderer.Evaluator<T> evaluator;

	public QuickLookFullValueEvaluator(CidrDebugProcess process, QuickLookValueRenderer.Evaluator<T> evaluator)
	{
		super("QuickLook");

		this.setShowValuePopup(false);
		this.process = process;
		this.evaluator = evaluator;
	}

	@Override
	public void startEvaluation(@NotNull XFullValueEvaluationCallback callback)
	{
		process.postCommand(new CidrDebugProcess.DebuggerImplicitCommand() {

			public void run(@NotNull DebuggerDriver driver) throws ExecutionException
			{
				T data = evaluator.evaluate();

				DebuggerUIUtil.invokeLater(() -> {

					if(callback.isObsolete())
						return;

					callback.evaluated("");


					JComponent component = evaluator.createComponent(data);
					if(component == null)
						component = new JLabel("No data", SwingConstants.CENTER);

					Project project = process.getProject();

					JFrame frame = WindowManager.getInstance().getFrame(project);
					Dimension frameSize = frame.getSize();
					Dimension size = new Dimension((int)(frameSize.width / 1.5), (int)(frameSize.height / 1.5));

					JBPopup popup = DebuggerUIUtil.createValuePopup(project, component, null);
					popup.setSize(size);

					if(component instanceof Disposable)
						Disposer.register(popup, (Disposable) component);

					RelativePoint point = new RelativePoint(frame, new Point((frameSize.width - size.width) / 2, (frameSize.height - size.height) / 2));
					popup.show(point);

				});
			}

		});
	}
}

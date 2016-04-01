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
import org.intellij.images.editor.impl.ImageEditorManagerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class QuickLookFullValueEvaluator extends XFullValueEvaluator
{
	private CidrDebugProcess process;
	private Evaluator evaluator;

	public QuickLookFullValueEvaluator(CidrDebugProcess process, Evaluator evaluator)
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
				BufferedImage data = evaluator.evaluate();

				DebuggerUIUtil.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						if(callback.isObsolete())
							return;

						callback.evaluated("");


						final JComponent component = createIconViewer(data);
						Project project = process.getProject();

						JFrame frame = WindowManager.getInstance().getFrame(project);
						Dimension frameSize = frame.getSize();
						Dimension size = new Dimension(frameSize.width / 2, frameSize.height / 2);

						JBPopup popup = DebuggerUIUtil.createValuePopup(project, component, null);
						popup.setSize(size);

						if(component instanceof Disposable)
							Disposer.register(popup, (Disposable)component);

						popup.show(new RelativePoint(frame, new Point(size.width / 2, size.height / 2)));
					}

				});
			}

		});
	}


	static private JComponent createIconViewer(@Nullable BufferedImage image) {
		if(image == null)
			return new JLabel("No data", SwingConstants.CENTER);

		return ImageEditorManagerImpl.createImageEditorUI(image);
	}

	public interface Evaluator
	{
		BufferedImage evaluate();
	}
}

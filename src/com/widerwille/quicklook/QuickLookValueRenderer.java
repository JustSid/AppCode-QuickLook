package com.widerwille.quicklook;

import com.intellij.execution.ExecutionException;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.jetbrains.cidr.execution.debugger.CidrDebugProcess;
import com.jetbrains.cidr.execution.debugger.backend.DBUserException;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriver;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import com.jetbrains.cidr.execution.debugger.evaluation.XValueNodeExpirable;
import com.jetbrains.cidr.execution.debugger.evaluation.renderers.ValueRenderer;
import org.intellij.images.editor.impl.ImageEditorManagerImpl;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;

public class QuickLookValueRenderer extends ValueRenderer
{
	private QuickLookValue value;
	private int dataFailCount = 0;
	private BufferedImage image;
	private Evaluator evaluator;

	public interface Evaluator<T>
	{
		T evaluate();
		JComponent createComponent(T data);
	}

	public class BufferedImageEvaluator implements Evaluator<BufferedImage>
	{
		@Override
		public BufferedImage evaluate()
		{
			if(image == null)
			{
				File file = null;

				try
				{
					file = getDataFile("png");
					image = ImageIO.read(file);
					file.delete();
				}
				catch(Exception e)
				{
					if(file != null)
						file.delete();

					image = null;
				}
			}

			return image;
		}

		@Override
		public JComponent createComponent(BufferedImage data)
		{
			if(data == null)
				return null;

			return ImageEditorManagerImpl.createImageEditorUI(data);
		}
	}



	protected QuickLookValueRenderer(@NotNull QuickLookValue value)
	{
		super(value.getPhysicalValue());

		this.value = value;
	}

	public void setEvaluator(Evaluator evaluator)
	{
		this.evaluator = evaluator;
	}

	@Nullable
	public Icon getIcon()
	{
		return null;
	}

	@Nullable
	public String getType()
	{
		return value.getValue().getBestType();
	}

	@Nullable
	public String getDisplayValue()
	{
		return value.getDescription();
	}

	@NotNull
	public QuickLookValue getQuickLookValue()
	{
		return value;
	}


	@Override
	@NotNull
	public String doComputeValue(@NotNull EvaluationContext context) throws ExecutionException, DBUserException
	{
		if(evaluator != null)
		{
			try
			{
				// In German we have a saying "through the back and chest into the eye", meaning a bullet finding
				// its way into the eye in the most complicated way possible
				// Well, this is exactly that! But I can't seem to find another way to access the XValueNodeImpl
				// so I can get my own full value evaluator in...

				Field field = EvaluationContext.class.getDeclaredField("myExpirable");
				field.setAccessible(true);
				XValueNodeExpirable expirable = (XValueNodeExpirable)field.get(context);

				field = XValueNodeExpirable.class.getDeclaredField("myNode");
				field.setAccessible(true);

				XValueNodeImpl node = (XValueNodeImpl)field.get(expirable);

				if(node != null)
				{
					CidrDebugProcess process = context.getFrame().getProcess();

					process.postCommand(new CidrDebugProcess.DebuggerImplicitCommand() {

						public void run(@NotNull DebuggerDriver driver) throws ExecutionException
						{
							QuickLookFullValueEvaluator fullValueEvaluator = new QuickLookFullValueEvaluator<>(process, evaluator);
							node.setFullValueEvaluator(fullValueEvaluator);
						}

					});
				}
			}
			catch(Exception e)
			{}
		}

		String value = getDisplayValue();
		if(value == null)
			return super.doComputeValue(context);

		return value;
	}

	@Override
	public Icon getIcon(boolean b)
	{
		Icon icon = getIcon();

		if(icon == null)
			return super.getIcon(b);

		return icon;
	}


	protected QuickLookValue getDataValue()
	{
		return null;
	}
	protected File getDataFile(String extension)
	{
		if(dataFailCount >= 3)
			return null;

		File file = null;

		try
		{
			QuickLookValue dataValue = getDataValue();
			if(dataValue == null || !dataValue.isValid() || !dataValue.isKindOfClass("NSData"))
			{
				dataFailCount ++;
				return null;
			}

			file = File.createTempFile("lldbOutput", "." + extension);

			QuickLookValue bytesPointer = dataValue.sendMessage("bytes");
			QuickLookValue length = dataValue.sendMessage("length");

			Long pointer = Long.parseLong(bytesPointer.getPointer().substring(2), 16);
			String eval = "memory read -o " + file.getPath() + " -b --force " + bytesPointer.getPointer() + " 0x" + Long.toHexString(pointer + length.getIntValue());

			dataValue.getContext().executeCommand(eval);

			// Give the command some time to complete
			int iteration = 0;
			while(file.length() < length.getIntValue() && iteration < 10)
			{
				Thread.sleep(100);
				iteration ++;
			}

			// Aaaaand give up...
			if(iteration >= 10 && file.length() < length.getIntValue())
			{
				file.delete();
				return null;
			}

			return file;
		}
		catch(Exception e)
		{
			if(file != null)
				file.delete();

			dataFailCount ++;
		}

		return null;
	}
}

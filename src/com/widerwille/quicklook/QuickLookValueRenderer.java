package com.widerwille.quicklook;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.util.Pair;
import com.intellij.xdebugger.frame.XFullValueEvaluator;
import com.jetbrains.cidr.execution.debugger.CidrDebugProcess;
import com.jetbrains.cidr.execution.debugger.CidrStackFrame;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerCommandException;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import com.jetbrains.cidr.execution.debugger.evaluation.renderers.ValueRenderer;
import org.intellij.images.editor.impl.ImageEditorManagerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;

public class QuickLookValueRenderer extends ValueRenderer
{
	private final QuickLookValue value;
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
		return value.getValue().getType();
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
	public Pair<String, XFullValueEvaluator> computeValueAndEvaluator(@NotNull EvaluationContext context) throws ExecutionException, DebuggerCommandException
	{
		if(evaluator != null)
		{
			try
			{
				Field field = EvaluationContext.class.getDeclaredField("myFrame");
				field.setAccessible(true);
				CidrStackFrame frame = (CidrStackFrame)field.get(context);

				CidrDebugProcess process = frame.getProcess();
				QuickLookFullValueEvaluator fullValueEvaluator = new QuickLookFullValueEvaluator<>(process, evaluator);

				String value = getDisplayValue();
				if(value == null)
					return super.computeValueAndEvaluator(context);

				return new Pair<>(value, fullValueEvaluator);
			}
			catch(Exception e)
			{}
		}

		return super.computeValueAndEvaluator(context);
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

			long pointer = Long.parseLong(bytesPointer.getPointer().substring(2), 16);
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

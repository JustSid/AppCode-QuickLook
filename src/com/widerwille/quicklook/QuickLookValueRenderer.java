package com.widerwille.quicklook;

import com.intellij.execution.ExecutionException;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.jetbrains.cidr.execution.debugger.CidrDebugProcess;
import com.jetbrains.cidr.execution.debugger.backend.DBUserException;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriver;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import com.jetbrains.cidr.execution.debugger.evaluation.XValueNodeExpirable;
import com.jetbrains.cidr.execution.debugger.evaluation.renderers.ValueRenderer;
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
	private File dataFile = null;
	private int dataFailCount = 0;
	private BufferedImage image;
	private QuickLookImageIcon imageIcon;

	public QuickLookValueRenderer(QuickLookValue value)
	{
		super(value.getPhysicalValue());

		this.value = value;
	}

	public void close()
	{
		if(dataFile != null)
			dataFile.delete();

		dataFile = null;
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
	public String getName()
	{
		return value.getValue().getName();
	}

	@Nullable
	public String getDisplayValue()
	{
		return value.getDescription();
	}


	@Override
	@NotNull
	public String doComputeValue(@NotNull EvaluationContext context) throws ExecutionException, DBUserException
	{
		if(hasImageContent())
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
							QuickLookFullValueEvaluator evaluator = new QuickLookFullValueEvaluator(process, new QuickLookFullValueEvaluator.Evaluator()
							{
								@Override
								public BufferedImage evaluate()
								{
									return getImageContent();
								}

							});

							node.setFullValueEvaluator(evaluator);
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


	public QuickLookValue getQuickLookValue()
	{
		return value;
	}


	public boolean hasImageContent()
	{
		return false;
	}
	public BufferedImage getImageContent()
	{
		synchronized(this)
		{
			if(image == null)
			{
				try
				{
					File file = getDataFile("png");

					image = ImageIO.read(file);
					if(image != null)
						imageIcon = new QuickLookImageIcon(image, 16, 16);
				}
				catch(Exception e)
				{
					image = null;
					imageIcon = null;
				}
			}
		}

		return image;
	}

	protected QuickLookValue getDataValue()
	{
		return null;
	}
	protected File getDataFile(String extension)
	{
		if(dataFile == null)
		{
			if(dataFailCount >= 3)
				return null;

			try
			{
				QuickLookValue dataValue = getDataValue();
				if(dataValue == null || !dataValue.isValid() || !dataValue.isKindOfClass("NSData"))
				{
					dataFailCount ++;
					return null;
				}

				dataFile = File.createTempFile("lldbOutput", "." + extension);

				QuickLookValue bytesPointer = dataValue.sendMessage("bytes");
				QuickLookValue length = dataValue.sendMessage("length");

				Long pointer = Long.parseLong(bytesPointer.getPointer().substring(2), 16);
				String eval = "memory read -o " + dataFile.getPath() + " -b --force " + bytesPointer.getPointer() + " 0x" + Long.toHexString(pointer + length.getIntValue());

				dataValue.getContext().executeCommand(eval);

				// Give the command some time to complete
				int iteration = 0;
				while(dataFile.length() < length.getIntValue() && iteration < 10)
				{
					Thread.sleep(100);
					iteration ++;
				}

				// Aaaaand give up...
				if(iteration >= 10 && dataFile.length() < length.getIntValue())
				{
					dataFile.delete();
					dataFile = null;
				}
			}
			catch(Exception e)
			{
				if(dataFile != null)
					dataFile.delete();

				dataFile = null;
				dataFailCount ++;
			}
		}

		return dataFile;
	}
}

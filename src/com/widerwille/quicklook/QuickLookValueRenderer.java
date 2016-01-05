package com.widerwille.quicklook;

import com.intellij.execution.ExecutionException;
import com.jetbrains.cidr.execution.debugger.evaluation.CidrPhysicalValue;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import com.jetbrains.cidr.execution.debugger.evaluation.renderers.ValueRenderer;
import org.antlr.v4.runtime.misc.Nullable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

public class QuickLookValueRenderer extends ValueRenderer
{
	private QuickLookValue value;
	private File dataFile = null;

	public QuickLookValueRenderer(QuickLookValue value)
	{
		super(value.getOriginalValue());
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
	public String getDisplayValue()
	{
		return null;
	}

	@Override
	@NotNull
	public String computeValue(@NotNull EvaluationContext context) throws ExecutionException
	{
		String value = getDisplayValue();
		if(value == null)
			return super.computeValue(context);

		return value;
	}

	@Override
	public Icon getIcon(boolean b, CidrPhysicalValue value)
	{
		Icon icon = getIcon();

		if(icon == null)
			return super.getIcon(b, value);

		return icon;
	}
	@Override
	protected boolean shouldPrintChildrenConsoleDescription()
	{
		return false;
	}



	public QuickLookValue getQuickLookValue()
	{
		return value;
	}
	public QuickLookValue getDataValue()
	{
		return null;
	}


	public File getDataFile(String extension)
	{
		if(dataFile == null)
		{
			try
			{
				QuickLookValue dataValue = getDataValue();
				if(dataValue == null || !dataValue.isValid() || !dataValue.isKindOfClass("NSData"))
					return null;

				dataFile = File.createTempFile("lldbOutput", "." + extension);

				QuickLookValue bytesPointer = dataValue.sendMessage("bytes");
				QuickLookValue length = dataValue.sendMessage("length");

				Long pointer = Long.parseLong(bytesPointer.getPointer().substring(2), 16);
				String eval = "memory read -o " + dataFile.getPath() + " -b --force " + bytesPointer.getPointer() + " 0x" + Long.toHexString(pointer + length.getIntValue());

				value.getDebuggerDriver().executeConsoleCommand(eval);

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
			}
		}

		return dataFile;
	}
}

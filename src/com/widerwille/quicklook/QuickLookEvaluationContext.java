package com.widerwille.quicklook;

import com.intellij.execution.ExecutionException;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerCommandException;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriver;
import com.jetbrains.cidr.execution.debugger.backend.LLValue;
import com.jetbrains.cidr.execution.debugger.backend.LLValueData;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class QuickLookEvaluationContext
{
	public enum Platform
	{
		Unknown,
		iPhone,
		Mac
	}

	private EvaluationContext underlyingContext;
	private static boolean evaluatedPlatform = false;
	private static Platform platform = Platform.Unknown;


	public QuickLookEvaluationContext(@NotNull EvaluationContext context)
	{
		underlyingContext = context;

		if(!evaluatedPlatform)
		{
			try
			{
				QuickLookValue UIDeviceValue = evaluate("(Class)NSClassFromString(@\"UIDevice\")");
				QuickLookValue NSImageValue = evaluate("(Class)NSClassFromString(@\"NSImage\")");

				if(!UIDeviceValue.isNilPointer())
				{
					platform = Platform.iPhone;
				}
				else if(!NSImageValue.isNilPointer())
				{
					platform = Platform.Mac;
				}
				else
				{
					platform = Platform.Unknown;
				}
			}
			catch(Exception e)
			{
				platform = Platform.Unknown;
			}
		}
	}

	public DebuggerDriver getDebuggerDriver()
	{
		try
		{
			Field field = EvaluationContext.class.getDeclaredField("myDriver");
			field.setAccessible(true);
			return (DebuggerDriver)field.get(underlyingContext);

		}
		catch(Exception e)
		{}

		return null;
	}
	public EvaluationContext getUnderlyingContext()
	{
		return underlyingContext;
	}



	public QuickLookValue evaluate(String string) throws ExecutionException, DebuggerCommandException
	{
		LLValue value = underlyingContext.evaluate(string);
		LLValueData data = underlyingContext.getData(value);

		return new QuickLookValue(null, value, data, this);
	}

	public QuickLookValue createVariable(String type, String name) throws ExecutionException, DebuggerCommandException
	{
		LLValue value = underlyingContext.evaluate(type + " $" + name);

		if(value.isValid())
			return evaluate("$" + name);

		return null;
	}

	public void executeCommand(String command) throws ExecutionException, DebuggerCommandException
	{
		getDebuggerDriver().executeConsoleCommand(command);
	}

	public Platform getPlatform()
	{
		return platform;
	}
}

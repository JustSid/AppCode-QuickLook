package com.widerwille.quicklook;

import com.intellij.execution.ExecutionException;
import com.jetbrains.cidr.execution.debugger.backend.DBCannotEvaluateException;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriver;
import com.jetbrains.cidr.execution.debugger.backend.LLValue;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class QuickLookEvaluationContext
{
	private EvaluationContext underlyingContext;

	public QuickLookEvaluationContext(@NotNull EvaluationContext context)
	{
		underlyingContext = context;
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
	EvaluationContext getUnderlyingContext()
	{
		return underlyingContext;
	}



	public QuickLookValue evaluate(String string) throws ExecutionException, DBCannotEvaluateException
	{
		LLValue value = underlyingContext.evaluate(string);
		return new QuickLookValue(null, value, this);
	}

	public QuickLookValue createVariable(String type, String name) throws ExecutionException, DBCannotEvaluateException
	{
		LLValue value = underlyingContext.evaluate(type + " $" + name);

		if(value.isValid())
			return evaluate("$" + name);

		return null;
	}

	public void executeCommand(String command) throws ExecutionException
	{
		getDebuggerDriver().executeConsoleCommand(command);
	}
}

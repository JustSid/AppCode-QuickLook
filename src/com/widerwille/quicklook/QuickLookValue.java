package com.widerwille.quicklook;

import com.intellij.execution.ExecutionException;
import com.jetbrains.cidr.execution.debugger.backend.DBCannotEvaluateException;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriver;
import com.jetbrains.cidr.execution.debugger.backend.LLValue;
import com.jetbrains.cidr.execution.debugger.evaluation.CidrPhysicalValue;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;

import java.lang.reflect.Field;

public class QuickLookValue
{
	private EvaluationContext context;
	private LLValue value;
	private CidrPhysicalValue originalValue;

	public QuickLookValue(CidrPhysicalValue physicalValue, LLValue lldbValue, EvaluationContext context)
	{
		this.originalValue = physicalValue;
		this.value = lldbValue;
		this.context = context;
	}


	public LLValue getValue()
	{
		return value;
	}
	public CidrPhysicalValue getOriginalValue()
	{
		return originalValue;
	}
	public DebuggerDriver getDebuggerDriver()
	{
		try
		{
			Field field = EvaluationContext.class.getDeclaredField("myDriver");
			field.setAccessible(true);
			return (DebuggerDriver)field.get(context);

		}
		catch(Exception e)
		{}

		return null;
	}

	public String getPointer() throws DBCannotEvaluateException
	{
		return value.getPointer();
	}
	public String getName()
	{
		return value.getName();
	}


	public boolean isValid()
	{
		return value.isValid();
	}
	public boolean isPointer()
	{
		return value.isPointer();
	}
	public boolean isNilPointer()
	{
		return value.isNilPointer();
	}


	@Override
	public String toString()
	{
		return getName();
	}


	public QuickLookValue evaluate(String string) throws ExecutionException, DBCannotEvaluateException
	{
		LLValue value = context.evaluate(string);
		return new QuickLookValue(originalValue, value, context);
	}


	public boolean isKindOfClass(String className) throws ExecutionException, DBCannotEvaluateException
	{
		return context.evaluate("(unsigned char)((Class)objc_getClass(\"" + className + "\")?" + EvaluationContext.cast("[" + EvaluationContext.cast(value.getPointer(), "id") + " " + "isKindOfClass:(Class)objc_lookUpClass(\"" + className + "\")" + "]", "unsigned char") + ":0)", DebuggerDriver.StandardDebuggerLanguage.OBJC_PLUS_PLUS).isTrue();
	}
	public boolean respondsToSelector(String selector)
	{
		try
		{
			return context.messageSend(value, "respondsToSelector:(SEL)NSSelectorFromString(@\"" + selector + "\")").isTrue();
		}
		catch(Exception e)
		{
			return false;
		}
	}
	public QuickLookValue sendMessage(String message) throws ExecutionException, DBCannotEvaluateException
	{
		return evaluate("[(" + value.getBestType() + ")(" + getPointer() + ") " + message + "]");
	}


	public QuickLookValue createVariable(String type, String name) throws ExecutionException, DBCannotEvaluateException
	{
		QuickLookValue temp = evaluate(type + " $" + name);
		if(temp.value.isValid())
			return evaluate("$" + name);

		return null;
	}


	public void refresh()
	{
		String expression = value.getReferenceExpression();
		if(expression != null && expression.length() > 0)
		{
			try
			{
				QuickLookValue temp = evaluate(expression);
				value = temp.value;
			}
			catch(Exception e)
			{}
		}
	}

	public String getStringValue()
	{
		refresh();
		return value.getValue();
	}
	public float getFloatValue()
	{
		return Float.parseFloat(getStringValue());
	}
	public int getIntValue()
	{
		return Integer.parseInt(getStringValue());
	}


	public void executeCommand(String command) throws ExecutionException
	{
		getDebuggerDriver().executeConsoleCommand(command);
	}
}

package com.widerwille.quicklook;

import com.intellij.execution.ExecutionException;
import com.jetbrains.cidr.execution.debugger.backend.DBCannotEvaluateException;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriver;
import com.jetbrains.cidr.execution.debugger.backend.LLValue;
import com.jetbrains.cidr.execution.debugger.evaluation.CidrPhysicalValue;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuickLookValue
{
	private QuickLookEvaluationContext context;
	private LLValue value;
	private CidrPhysicalValue physicalValue;

	public QuickLookValue(@Nullable CidrPhysicalValue physicalValue, @NotNull LLValue lldbValue, @NotNull QuickLookEvaluationContext context)
	{
		this.physicalValue = physicalValue;
		this.value = lldbValue;
		this.context = context;
	}


	public LLValue getValue()
	{
		return value;
	}
	public CidrPhysicalValue getPhysicalValue()
	{
		return physicalValue;
	}
	public String getDescription()
	{
		try
		{
			return sendMessage("description").sendMessage("UTF8String").getValue().getReadableValue();
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
	QuickLookEvaluationContext getContext()
	{
		return context;
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


	public boolean isKindOfClass(String className) throws ExecutionException, DBCannotEvaluateException
	{
		return context.getUnderlyingContext().evaluate("(unsigned char)((Class)objc_getClass(\"" + className + "\")?" + EvaluationContext.cast("[" + EvaluationContext.cast(value.getPointer(), "id") + " " + "isKindOfClass:(Class)objc_lookUpClass(\"" + className + "\")" + "]", "unsigned char") + ":0)", DebuggerDriver.StandardDebuggerLanguage.OBJC_PLUS_PLUS).isTrue();
	}
	public boolean respondsToSelector(String selector)
	{
		try
		{
			return context.getUnderlyingContext().messageSend(value, "respondsToSelector:(SEL)NSSelectorFromString(@\"" + selector + "\")").isTrue();
		}
		catch(Exception e)
		{
			return false;
		}
	}
	public QuickLookValue sendMessage(String message) throws ExecutionException, DBCannotEvaluateException
	{
		return context.evaluate("[(" + value.getBestType() + ")(" + getPointer() + ") " + message + "]");
	}
	public QuickLookValue sendMessage(String message, String returnType) throws ExecutionException, DBCannotEvaluateException
	{
		return context.evaluate("(" + returnType + ")[(" + value.getBestType() + ")(" + getPointer() + ") " + message + "]");
	}


	public void refresh()
	{
		String expression = value.getReferenceExpression();
		if(expression != null && expression.length() > 0)
		{
			try
			{
				LLValue temp = context.getUnderlyingContext().evaluate(expression);
				value = temp;
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
}

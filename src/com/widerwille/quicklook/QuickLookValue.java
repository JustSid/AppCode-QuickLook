package com.widerwille.quicklook;

import com.intellij.execution.ExecutionException;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerCommandException;
import com.jetbrains.cidr.execution.debugger.backend.DebuggerDriver;
import com.jetbrains.cidr.execution.debugger.backend.LLValue;
import com.jetbrains.cidr.execution.debugger.backend.LLValueData;
import com.jetbrains.cidr.execution.debugger.evaluation.CidrPhysicalValue;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuickLookValue
{
	private final QuickLookEvaluationContext context;
	private LLValue value;
	private LLValueData valueData;
	private final CidrPhysicalValue physicalValue;

	public QuickLookValue(@Nullable CidrPhysicalValue physicalValue, @NotNull LLValue lldbValue, @NotNull LLValueData lldbValueData, @NotNull QuickLookEvaluationContext context)
	{
		this.physicalValue = physicalValue;
		this.value = lldbValue;
		this.valueData = lldbValueData;
		this.context = context;
	}


	public LLValue getValue()
	{
		return value;
	}
	public LLValueData getValueData()
	{
		try
		{
			if(valueData == null)
				valueData = context.getUnderlyingContext().getData(value);
		}
		catch(Exception e)
		{
			valueData = null;
		}

		return valueData;
	}
	public CidrPhysicalValue getPhysicalValue()
	{
		return physicalValue;
	}
	public String getDescription()
	{
		try
		{
			return context.getUnderlyingContext().getData(sendMessage("description").sendMessage("UTF8String").getValue()).getPresentableValue();
		}
		catch(Exception e)
		{}

		return null;
	}


	public String getPointer() throws DebuggerCommandException
	{
		return getValueData().getPointer();
	}
	public String getName()
	{
		return value.getName();
	}
	public QuickLookEvaluationContext getContext()
	{
		return context;
	}


	public boolean isValid()
	{
		return value.isValid();
	}
	public boolean isPointer()
	{
		return getValueData().isPointer();
	}
	public boolean isNilPointer()
	{
		return getValueData().isNullPointer();
	}


	@Override
	public String toString()
	{
		return getName();
	}

	public boolean isKindOfClass(String className)
	{
		try
		{
			LLValueData data = context.getUnderlyingContext().evaluateData("(unsigned char)((Class)objc_getClass(\"" + className + "\")?" + EvaluationContext.cast("[" + EvaluationContext.cast(getPointer(), "id") + " " + "isKindOfClass:(Class)objc_lookUpClass(\"" + className + "\")" + "]", "unsigned char") + ":0)", DebuggerDriver.StandardDebuggerLanguage.OBJC_PLUS_PLUS);
			return data.isTrue();
		}
		catch(Exception e)
		{
			return false;
		}
	}
	public boolean respondsToSelector(String selector)
	{
		try
		{
			return context.getUnderlyingContext().messageSendData(value, "respondsToSelector:(SEL)NSSelectorFromString(@\"" + selector + "\")").isTrue();
		}
		catch(Exception e)
		{
			return false;
		}
	}
	public QuickLookValue sendMessage(String message) throws ExecutionException, DebuggerCommandException
	{
		return context.evaluate("[(" + value.getType() + ")(" + getPointer() + ") " + message + "]");
	}
	public QuickLookValue sendMessage(String message, String returnType) throws ExecutionException, DebuggerCommandException
	{
		return context.evaluate("(" + returnType + ")[(" + value.getType() + ")(" + getPointer() + ") " + message + "]");
	}

	public QuickLookValue sendMessage(String message, String returnType, String name) throws ExecutionException, DebuggerCommandException
	{
		QuickLookValue temp = context.createVariable(returnType, name);
		context.evaluate(temp.getName() + " = (" + returnType + ")[(" + value.getType() + ")(" + getPointer() + ") " + message + "]");

		return temp;
	}


	public void refresh()
	{
		String expression = value.getReferenceExpression();
		if(expression.length() > 0)
		{
			try
			{
				value = context.getUnderlyingContext().evaluate(expression);
				valueData = null;
			}
			catch(Exception e)
			{}
		}
	}

	public String getStringValue()
	{
		refresh();
		return getValueData().getValue();
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

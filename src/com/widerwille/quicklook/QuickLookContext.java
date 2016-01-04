package com.widerwille.quicklook;

import com.intellij.openapi.util.Expirable;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.TimerTask;

public class QuickLookContext
{
	private static ArrayList<QuickLookContext> contexts = new ArrayList<QuickLookContext>();
	private static boolean isPruning = false;

	private EvaluationContext context;
	private ArrayList<QuickLookValueRenderer> renderers = new ArrayList<QuickLookValueRenderer>();

	public static QuickLookContext contextForEvaluationContext(EvaluationContext tcontext)
	{
		if(!isContextValid(tcontext))
			return null;

		pruneContexts();

		for(QuickLookContext context : contexts)
		{
			if(context.context.equals(tcontext))
				return context;
		}

		QuickLookContext context = new QuickLookContext(tcontext);
		contexts.add(context);

		beginPruning();

		return context;
	}

	public void addValueRenderer(QuickLookValueRenderer renderer)
	{
		renderers.add(renderer);
	}


	private QuickLookContext(EvaluationContext context)
	{
		this.context = context;
	}

	private static void beginPruning()
	{
		if(!isPruning && contexts.size() > 0)
		{
			isPruning = true;

			java.util.Timer timer = new java.util.Timer();
			timer.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					isPruning = false;
					pruneContexts();
					beginPruning();
				}
			}, 500);
		}
	}

	private static void pruneContexts()
	{
		synchronized(contexts)
		{
			for(int i = 0; i < contexts.size(); i ++)
			{
				QuickLookContext context = contexts.get(i);
				if(!isContextValid(context.context))
				{
					context.prune();

					contexts.remove(i);
					i --;
				}
			}
		}
	}
	private void prune()
	{
		for(QuickLookValueRenderer renderer : renderers)
			renderer.close();
	}


	private static boolean isContextValid(EvaluationContext context)
	{
		try
		{
			Field field = EvaluationContext.class.getDeclaredField("myExpirable");
			field.setAccessible(true);

			Expirable expirable = (Expirable)field.get(context);
			return (expirable.isExpired() == false);
		}
		catch(Exception e)
		{
			System.out.println("Hello World");
		}

		return false;
	}
}

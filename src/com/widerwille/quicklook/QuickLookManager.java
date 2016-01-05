package com.widerwille.quicklook;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.util.Expirable;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.TimerTask;

public class QuickLookManager implements ProjectComponent
{
	private ArrayList<QuickLookContext> contexts = new ArrayList<>();
	private ArrayList<QuickLookValueRenderer> contentRenderers = new ArrayList<>();

	private boolean isPruning = false;
	private QuickLookToolWindow thumbnailUI = null;

	@NonNls
	@NotNull
	public String getComponentName()
	{
		return "QuickLookManager";
	}

	@Override
	public void initComponent()
	{}

	@Override
	public void disposeComponent()
	{}

	@Override
	public void projectOpened()
	{}
	@Override
	public void projectClosed()
	{}


	public QuickLookContext contextForEvaluationContext(EvaluationContext tcontext)
	{
		if(!isContextValid(tcontext))
			return null;

		pruneContexts();

		for(QuickLookContext context : contexts)
		{
			if(context.getContext().equals(tcontext))
				return context;
		}

		QuickLookContext context = new QuickLookContext(tcontext, this);
		contexts.add(context);

		beginPruning();

		QuickLookManager manager = this;

		ApplicationManager.getApplication().invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				synchronized(contexts)
				{
					if(thumbnailUI == null)
					{
						thumbnailUI = new QuickLookToolWindow(context.getProject(), manager);
						thumbnailUI.setVisible(true);
						thumbnailUI.refresh(contentRenderers);
					}
				}
			}
		});

		return context;
	}

	public void addContentRenderer(QuickLookValueRenderer renderer)
	{
		synchronized(contexts)
		{
			contentRenderers.add(renderer);
		}

		ApplicationManager.getApplication().invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				synchronized(contexts)
				{
					if(thumbnailUI != null)
						thumbnailUI.refresh(contentRenderers);
				}
			}
		});
	}

	private void beginPruning()
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

	private void pruneContexts()
	{
		synchronized(contexts)
		{
			for(int i = 0; i < contexts.size(); i ++)
			{
				QuickLookContext context = contexts.get(i);
				if(!isContextValid(context.getContext()))
				{
					context.prune();

					contexts.remove(i);
					i --;
				}
			}
		}

		ApplicationManager.getApplication().invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				synchronized(contexts)
				{
					if(contexts.size() == 0 && thumbnailUI != null)
					{
						thumbnailUI.dispose();
						thumbnailUI = null;
					}
				}
			}
		});
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
		{}

		return false;
	}
}

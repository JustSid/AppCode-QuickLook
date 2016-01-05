package com.widerwille.quicklook;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;

public class QuickLookToolWindow
{
	private Project project;
	private ToolWindow toolWindow;
	private JPanel content;

	private static String TOOLWINDOW_ID = "com.widerwille.quicklook.toolwindow";
	private static Icon icon = IconLoader.getIcon("/icons/quick-look.png");

	public QuickLookToolWindow(Project project)
	{
		this.project = project;

		ToolWindowManager windowManager = ToolWindowManager.getInstance(project);

		ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
		Content content = contentFactory.createContent(this.content, "", false);

		toolWindow = windowManager.registerToolWindow(TOOLWINDOW_ID, false, ToolWindowAnchor.LEFT, true);
		toolWindow.setIcon(icon);
		toolWindow.setStripeTitle("Quick Look");
		toolWindow.getContentManager().addContent(content);
	}

	public void dispose()
	{
		ToolWindowManager windowManager = ToolWindowManager.getInstance(project);
		windowManager.unregisterToolWindow(TOOLWINDOW_ID);
	}

	public void setVisible(boolean visible)
	{
		toolWindow.setAvailable(visible, null);
	}

}

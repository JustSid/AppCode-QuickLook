package com.widerwille.quicklook;


import com.intellij.openapi.util.Key;
import com.jetbrains.cidr.execution.debugger.evaluation.CidrPhysicalValue;
import com.jetbrains.cidr.execution.debugger.evaluation.EvaluationContext;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;

public class QuickLookNSURLValueRenderer extends QuickLookValueRenderer
{
	private static final Key<Boolean> IS_URL = Key.create("IS_URL");

	public static QuickLookValueRenderer createRendererIfPossible(QuickLookValue value)
	{
		try
		{
			CidrPhysicalValue physicalValue = value.getPhysicalValue();
			EvaluationContext context = value.getContext().getUnderlyingContext();


			String type = physicalValue.getType();
			Boolean isURL = context.getCachedTypeInfo(type, IS_URL);

			if(isURL == null)
			{
				isURL = isURLType(value);
				context.putCachedTypeInfo(type, IS_URL, isURL);
			}

			if(isURL)
				return new QuickLookNSURLValueRenderer(value);
		}
		catch(Exception e)
		{}

		return null;
	}

	private static boolean isURLType(QuickLookValue value)
	{
		try
		{
			return value.isKindOfClass("NSURL");
		}
		catch(Exception e)
		{
			return false;
		}
	}



	public class URLEvaluator implements Evaluator<URL>
	{
		@Override
		public URL evaluate()
		{
			try
			{
				QuickLookValue value = getQuickLookValue();
				String absoluteURL = value.sendMessage("absoluteString", "NSString *").sendMessage("UTF8String").getValue().getReadableValue();

				return new URL(absoluteURL.replace("\"", ""));
			}
			catch(Exception e)
			{
				return null;
			}
		}

		private Scene createScene(WebView webView)
		{
			Group root = new Group();
			Scene scene = new Scene(root);

			root.getChildren().add(webView);

			return scene;
		}

		@Override
		public JComponent createComponent(URL data)
		{
			if(data == null)
				return null;

			try
			{
				JFXPanel panel = new JFXPanel();

				Platform.runLater(() -> {

					Platform.setImplicitExit(false);

					WebView webView = new WebView();
					WebEngine webEngine = webView.getEngine();

					webEngine.load(data.toString());

					panel.setScene(createScene(webView));

				});

				// There must be a better way to do this, right?
				// I have no idea...

				panel.addComponentListener(new ComponentAdapter() {
					public void componentResized(ComponentEvent e)
					{
						int width = panel.getWidth();
						int height = panel.getHeight();

						Scene scene = panel.getScene();

						Platform.runLater(() -> {

							Platform.setImplicitExit(false);

							Group root = (Group)scene.getRoot();
							WebView webView = (WebView)root.getChildren().get(0);

							webView.setPrefSize((double)width, (double)height);

						});
					}
				});

				return panel;
			}
			catch(Exception e)
			{
				return null;
			}
		}
	}


	private QuickLookNSURLValueRenderer(QuickLookValue value)
	{
		super(value);
		setEvaluator(new URLEvaluator());
	}

	@Override
	@Nullable
	public String getDisplayValue()
	{
		try
		{
			QuickLookValue value = getQuickLookValue();
			String absoluteURL = value.sendMessage("absoluteString", "NSString *").sendMessage("UTF8String").getValue().getReadableValue();

			return absoluteURL.replace("\"", "");
		}
		catch(Exception e)
		{
			return null;
		}
	}
}

package com.widerwille.quicklook.helper;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;

public class QuickLookHelper
{
	static public JComponent createWebView(URL url)
	{
		if(url == null)
			return null;

		try
		{
			JFXPanel panel = new JFXPanel();

			Platform.runLater(() -> {

				Platform.setImplicitExit(false);

				WebView webView = new WebView();
				WebEngine webEngine = webView.getEngine();

				webEngine.load(url.toString());

				Group root = new Group();
				Scene scene = new Scene(root);

				root.getChildren().add(webView);

				panel.setScene(scene);

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

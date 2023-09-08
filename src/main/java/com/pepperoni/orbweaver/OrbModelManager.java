package com.pepperoni.orbweaver;

import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import net.runelite.api.Animation;
import net.runelite.api.Client;
import net.runelite.api.Model;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class OrbModelManager
{
	@Inject
	private OrbWeaverPlugin plugin;
	@Inject
	private Client client;

	@Getter
	private Map<Integer, OrbWeaverModel> models = new HashMap<Integer, OrbWeaverModel>();

	// add a new model, invoked primarily by the WorldObject packet when a server adds an object
	public int addModel(int modelStorageId, int modelId, WorldPoint location, int orientation, int animationId)
	{
		if (!this.models.containsKey(modelStorageId))
		{
			plugin.getClientThread().invokeLater(() -> {
				try
				{
					RuneLiteObject runeLiteObject = client.createRuneLiteObject();
					runeLiteObject.setRadius(50);
					runeLiteObject.setOrientation(orientation);
					Animation animation;
					if (animationId < 0)
					{
						animation = runeLiteObject.getAnimation();
					}
					else
					{
						animation = client.loadAnimation(animationId);
					}
					runeLiteObject.setAnimation(animation);
					runeLiteObject.setShouldLoop(true);
					runeLiteObject.setDrawFrontTilesFirst(true);
					Model model = client.loadModel(modelId);
					if (model != null)
					{
						LocalPoint localPoint = LocalPoint.fromWorld(client, location);
						OrbWeaverModel orbWeaverModel = new OrbWeaverModel(modelStorageId, runeLiteObject, model, location, localPoint, orientation, false);
						this.getModels().put(modelStorageId, orbWeaverModel);
						this.setModel(orbWeaverModel, model);
						this.setLocation(orbWeaverModel, location);
						return;
					}
					else
					{
						System.out.println("model was null");
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			});
			return modelStorageId;
		}
		return -1;
	}

	// update the location of the runeliteobject
	private void setLocation(OrbWeaverModel orbWeaverModel, WorldPoint worldPoint)
	{
		RuneLiteObject runeLiteObject = orbWeaverModel.getRuneLiteObject();
		LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
		plugin.getClientThread().invoke(() -> {
			runeLiteObject.setActive(false);
			runeLiteObject.setLocation(localPoint, worldPoint.getPlane());
			runeLiteObject.setActive(true);
		});
	}

	// set the model of a runeliteobject
	private void setModel(OrbWeaverModel orbWeaverModel, Model model)
	{
		RuneLiteObject runeLiteObject = orbWeaverModel.getRuneLiteObject();
		plugin.getClientThread().invoke(() -> {
			runeLiteObject.setModel(model);
		});
	}

	// remove one model by it's storage id
	public void removeModel(int objectStorageId)
	{
		plugin.getClientThread().invoke(() -> {
			RuneLiteObject runeLiteObject = getModels().get(objectStorageId).getRuneLiteObject();
			runeLiteObject.setActive(false);
		});
	}

	// remove all models
	public void removeModels()
	{
		for (Map.Entry<Integer, OrbWeaverModel> model : getModels().entrySet())
		{
			int modelStorageId = model.getKey();
			OrbWeaverModel orbWeaverModel = model.getValue();
			removeModel(modelStorageId);
		}
	}
}

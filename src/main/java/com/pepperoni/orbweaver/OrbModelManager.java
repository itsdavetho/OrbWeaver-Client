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
				if (client == null)
				{
					return;
				}
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
						OrbWeaverModel orbWeaverModel = new OrbWeaverModel(modelStorageId, runeLiteObject, model, location, localPoint, orientation, false, plugin, this);
						getModels().put(modelStorageId, orbWeaverModel);
						orbWeaverModel.setModel(model);
						orbWeaverModel.setLocation(location);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			});
			return modelStorageId;
		}
		else
		{
			OrbWeaverModel orbWeaverModel = this.getModels().get(modelStorageId);
			plugin.getClientThread().invokeLater(() -> {
				if (client == null)
				{
					return;
				}
				Model model = client.loadModel(modelId);
				if (model != null)
				{
					orbWeaverModel.setModel(model);
					orbWeaverModel.setActive(true);
					orbWeaverModel.setLocation(location);
				}
			});
		}
		return -1;
	}

	// remove all models and reset the model list
	public void removeModels()
	{
		for (Map.Entry<Integer, OrbWeaverModel> model : getModels().entrySet())
		{
			int modelStorageId = model.getKey();
			OrbWeaverModel orbWeaverModel = model.getValue();
			orbWeaverModel.removeModel();
		}

		this.models = new HashMap<Integer, OrbWeaverModel>();
	}
}

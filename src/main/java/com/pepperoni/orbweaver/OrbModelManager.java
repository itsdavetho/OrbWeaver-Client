package com.pepperoni.orbweaver;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Animation;
import net.runelite.api.Client;
import net.runelite.api.Model;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class OrbModelManager
{
	private OrbWeaverPlugin plugin;
	private Client client;
	private Map<Integer, OrbWeaverModel> models = new HashMap<Integer, OrbWeaverModel>();

	public int addModel(int modelStorageId, int modelId, LocalPoint location, int plane, int orientation, int animationId)
	{
		if (!this.models.containsKey(modelStorageId))
		{
			plugin.getClientThread().invoke(() -> {
				RuneLiteObject runeLiteObject = client.createRuneLiteObject();
				Model model = client.loadModel(modelId);
				Animation animation;
				if(model != null)
				{
					runeLiteObject.setModel(model);
					runeLiteObject.setLocation(location, plane);
					runeLiteObject.setOrientation(orientation);
					if(animationId < 0) {
						animation = runeLiteObject.getAnimation();
					} else {
						animation = client.loadAnimation(animationId);
					}
					runeLiteObject.setAnimation(animation);

					OrbWeaverModel orbWeaverModel = new OrbWeaverModel(modelStorageId, runeLiteObject, model, location, plane, orientation);
				}
			});
			return modelStorageId;
		}

		return -1;
	}

	public boolean removeObject(int modelStorageId)
	{ // named objectStorageId to avoid ambiguity between object ids and index ids
		if (this.models.containsKey(modelStorageId))
		{
			OrbWeaverModel orbWeaverModel = this.models.get(modelStorageId);
			orbWeaverModel.getRuneLiteObject().setActive(false);
			this.models.remove(modelStorageId);
		}
		else
		{
			return false;
		}

		return true;
	}
}

package com.pepperoni.orbweaver.objects;

import com.pepperoni.orbweaver.OrbWeaverPlugin;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.Model;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class Objects<object>
{
	private OrbWeaverPlugin plugin;
	private Client client;
	private Map<Integer, RuneLiteObject> objects = new HashMap<Integer, RuneLiteObject>();

	public Objects(OrbWeaverPlugin plugin, Client client)
	{
		this.plugin = plugin;
		this.client = client;
	}

	public boolean addObject(int objectStorageId, int modelId, WorldPoint location, int orientation)
	{
		if(!this.objects.containsKey(objectStorageId))
		{
			RuneLiteObject object = this.client.createRuneLiteObject();
			LocalPoint localLocation = LocalPoint.fromWorld(this.client, location);
			if (localLocation != null)
			{ // make sure the location we're trying to add an object to is within the engines renderable distance
				Model objectModel = client.loadModel(modelId);
				plugin.getClientThread().invoke(() -> {
					if (objectModel != null)
					{
					/*final Instant timeout = Instant.now().plus(Duration.ofSeconds(5));
					if(Instant.now().isAfter(timeout)) {
						return true;
					}
					final Model objectModelReload = client.loadModel(modelId);
					if(objectModelReload == null)
					{
						return false;
					}
					return true;*/
						object.setModel(objectModel);
						object.setLocation(localLocation, location.getPlane());
						object.setActive(true);
						this.objects.put(objectStorageId, object);
						return true;
					}
					return false;
				});
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean removeObject(int objectStorageId)
	{ // named objectStorageId to avoid ambiguity between object ids and index ids
		if (this.objects.containsKey(objectStorageId))
		{
			RuneLiteObject object = this.objects.get(objectStorageId);
			object.setActive(false);
			object.setModel(null);
			object.setLocation(null, 0);
			this.objects.remove(objectStorageId);
		}
		else
		{
			return false;
		}

		return true;
	}
}

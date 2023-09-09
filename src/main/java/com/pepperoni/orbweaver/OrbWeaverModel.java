package com.pepperoni.orbweaver;

import javax.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Model;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;


@AllArgsConstructor
public class OrbWeaverModel
{
	@Getter
	private int storageId;
	@Getter
	private RuneLiteObject runeLiteObject;
	private Model model;
	private WorldPoint location;
	private LocalPoint localPoint;
	private int orientation = -1;
	private boolean active = false;
	@Inject
	private OrbWeaverPlugin plugin;
	@Inject
	@Getter
	private OrbModelManager orbModelManager;

	//modelStorageId, getRuneLiteObject(), model, location, orientation, false
	public void setActive(boolean active)
	{
		plugin.getClientThread().invoke(() -> {
			if (getRuneLiteObject() == null)
			{
				return;
			}
			getRuneLiteObject().setActive(active);
		});
	}

	public void setLocation(WorldPoint worldPoint)
	{
		LocalPoint localPoint = LocalPoint.fromWorld(plugin.getClient(), worldPoint);
		plugin.getClientThread().invoke(() -> {
			if (getRuneLiteObject() == null)
			{
				return;
			}
			getRuneLiteObject().setActive(false);
			getRuneLiteObject().setLocation(localPoint, worldPoint.getPlane());
			getRuneLiteObject().setActive(true);
		});
	}

	public void setModel(Model model)
	{
		plugin.getClientThread().invoke(() -> {
			if (getRuneLiteObject() == null)
			{
				return;
			}
			getRuneLiteObject().setModel(model);
		});
	}

	// remove one model by it's storage id
	public void removeModel()
	{
		plugin.getClientThread().invoke(() -> {
			this.setActive(false);
		});
	}
}

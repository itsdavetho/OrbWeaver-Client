package com.pepperoni.orbweaver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Model;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;


@Getter
@Setter
@AllArgsConstructor
public class OrbWeaverModel
{
	private int storageId;
	private RuneLiteObject runeLiteObject;
	private Model model;
	private WorldPoint location;
	private LocalPoint localPoint;
	private int orientation = -1;
	private boolean active = false;

	//modelStorageId, runeLiteObject, model, location, orientation, false
}

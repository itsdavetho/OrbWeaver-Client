package com.pepperoni.orbweaver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Model;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;


@Getter
@Setter
@AllArgsConstructor
public class OrbWeaverModel
{
	private int storageId;
	private RuneLiteObject runeLiteObject;
	private Model model;
	private LocalPoint location;
	private int plane = 0;
	private int orientation = -1;
}

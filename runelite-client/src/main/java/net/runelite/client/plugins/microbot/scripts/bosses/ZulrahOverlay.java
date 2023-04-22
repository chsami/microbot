package net.runelite.client.plugins.microbot.scripts.bosses;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.MicrobotPlugin;
import net.runelite.client.plugins.microbot.scripts.bosses.enums.ZulrahPhase;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.api.Point;
import javax.inject.Inject;
import java.awt.*;

public class ZulrahOverlay extends Overlay
{
	private static final Color TILE_BORDER_COLOR = new Color(0, 0, 0, 100);
	private static final Color NEXT_TEXT_COLOR = new Color(255, 255, 255, 100);

	private final MicrobotPlugin plugin;

	@Inject
	ZulrahOverlay(final MicrobotPlugin plugin)
	{
		setPosition(OverlayPosition.DYNAMIC);
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (ZulrahScript.zulrah == null) return null;

		ZulrahInstance instance = ZulrahScript.instance;

		if (instance == null)
		{
			return null;
		}

		ZulrahPhase currentPhase = instance.getPhase();
		ZulrahPhase nextPhase = instance.getNextPhase();
		if (currentPhase == null)
		{
			return null;
		}

		LocalPoint startTile = instance.getStartLocation();
		if (nextPhase != null && currentPhase.getStandLocation() == nextPhase.getStandLocation())
		{
			drawStandTiles(graphics, startTile, currentPhase, nextPhase);
		}
		else
		{
			drawStandTile(graphics, startTile, currentPhase, false);
			drawStandTile(graphics, startTile, nextPhase, true);
		}
		drawZulrahTileMinimap(graphics, startTile, currentPhase, false);
		drawZulrahTileMinimap(graphics, startTile, nextPhase, true);

		return null;
	}

	private void drawStandTiles(Graphics2D graphics, LocalPoint startTile, ZulrahPhase currentPhase, ZulrahPhase nextPhase)
	{
		LocalPoint localTile = currentPhase.getStandTile(startTile);
		Polygon northPoly = getCanvasTileNorthPoly(Microbot.getClient(), localTile);
		Polygon southPoly = getCanvasTileSouthPoly(Microbot.getClient(), localTile);
		Polygon poly = Perspective.getCanvasTilePoly(Microbot.getClient(), localTile);
		Point textLoc = Perspective.getCanvasTextLocation(Microbot.getClient(), graphics, localTile, "Stand / Next", 0);
		if (northPoly != null && southPoly != null && poly != null && textLoc != null)
		{
			Color northColor = currentPhase.getColor();
			Color southColor = nextPhase.getColor();
			graphics.setColor(northColor);
			graphics.fillPolygon(northPoly);
			graphics.setColor(southColor);
			graphics.fillPolygon(southPoly);
			graphics.setColor(TILE_BORDER_COLOR);
			graphics.setStroke(new BasicStroke(2));
			graphics.drawPolygon(poly);
			graphics.setColor(NEXT_TEXT_COLOR);
			graphics.drawString("Stand / Next", textLoc.getX(), textLoc.getY());
		}
	}

	private void drawStandTile(Graphics2D graphics, LocalPoint startTile, ZulrahPhase phase, boolean next)
	{
		if (phase == null)
		{
			return;
		}

		LocalPoint localTile = phase.getStandTile(startTile);
		Polygon poly = Perspective.getCanvasTilePoly(Microbot.getClient(), localTile);
		Color color = phase.getColor();
		if (poly != null)
		{
			graphics.setColor(TILE_BORDER_COLOR);
			graphics.setStroke(new BasicStroke(2));
			graphics.drawPolygon(poly);
			graphics.setColor(color);
			graphics.fillPolygon(poly);
			Point textLoc = Perspective.getCanvasTextLocation(Microbot.getClient(), graphics, localTile, (next) ? "Next" : "Stand", 0);
			if (textLoc != null)
			{
				graphics.setColor(NEXT_TEXT_COLOR);
				graphics.drawString((next) ? "Next" : "Stand", textLoc.getX(), textLoc.getY());
			}
		}
	}

	private void drawZulrahTileMinimap(Graphics2D graphics, LocalPoint startTile, ZulrahPhase phase, boolean next)
	{
		if (phase == null)
		{
			return;
		}
		LocalPoint zulrahLocalTile = phase.getZulrahTile(startTile);
		Point zulrahMinimapPoint = Perspective.localToMinimap(Microbot.getClient(), zulrahLocalTile);
		Color color = phase.getColor();
		graphics.setColor(color);
		if (zulrahMinimapPoint != null)
		{
			graphics.fillOval(zulrahMinimapPoint.getX() - 2, zulrahMinimapPoint.getY() - 2, 4, 4);
		}
		graphics.setColor(TILE_BORDER_COLOR);
		graphics.setStroke(new BasicStroke(1));
		if (zulrahMinimapPoint != null)
		{
			graphics.drawOval(zulrahMinimapPoint.getX() - 2, zulrahMinimapPoint.getY() - 2, 4, 4);
		}
		if (next)
		{
			graphics.setColor(NEXT_TEXT_COLOR);
			FontMetrics fm = graphics.getFontMetrics();
			if (zulrahMinimapPoint != null)
			{
				graphics.drawString("Next", zulrahMinimapPoint.getX() - fm.stringWidth("Next") / 2, zulrahMinimapPoint.getY() - 2);
			}
		}
	}

	private Polygon getCanvasTileNorthPoly(Client client, LocalPoint localLocation)
	{
		int plane = client.getPlane();
		int halfTile = Perspective.LOCAL_TILE_SIZE / 2;

		Point p1 = Perspective.localToCanvas(client, new LocalPoint(localLocation.getX() - halfTile, localLocation.getY() - halfTile), plane);
		Point p2 = Perspective.localToCanvas(client, new LocalPoint(localLocation.getX() - halfTile, localLocation.getY() + halfTile), plane);
		Point p3 = Perspective.localToCanvas(client, new LocalPoint(localLocation.getX() + halfTile, localLocation.getY() + halfTile), plane);

		if (p1 == null || p2 == null || p3 == null)
		{
			return null;
		}

		Polygon poly = new Polygon();
		poly.addPoint(p1.getX(), p1.getY());
		poly.addPoint(p2.getX(), p2.getY());
		poly.addPoint(p3.getX(), p3.getY());

		return poly;
	}

	private Polygon getCanvasTileSouthPoly(Client client, LocalPoint localLocation)
	{
		int plane = client.getPlane();
		int halfTile = Perspective.LOCAL_TILE_SIZE / 2;

		Point p1 = Perspective.localToCanvas(client, new LocalPoint(localLocation.getX() - halfTile, localLocation.getY() - halfTile), plane);
		Point p2 = Perspective.localToCanvas(client, new LocalPoint(localLocation.getX() + halfTile, localLocation.getY() + halfTile), plane);
		Point p3 = Perspective.localToCanvas(client, new LocalPoint(localLocation.getX() + halfTile, localLocation.getY() - halfTile), plane);

		if (p1 == null || p2 == null || p3 == null)
		{
			return null;
		}

		Polygon poly = new Polygon();
		poly.addPoint(p1.getX(), p1.getY());
		poly.addPoint(p2.getX(), p2.getY());
		poly.addPoint(p3.getX(), p3.getY());

		return poly;
	}

}

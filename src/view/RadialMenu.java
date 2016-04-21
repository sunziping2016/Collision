package view;

import controller.PointerListener;
import model.GameModel;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Sun on 4/19/2016.
 *
 * Radial menu class.
 */
public class RadialMenu extends PointerListener {
    private float centerX;
    private float centerY;
    private RadialMenuListener listener;
    private ArrayList<RadialMenuItem> items;

    private RadialMenuItem selected = null;
    private int active = -1;
    private float selectExtent = 0;
    private boolean drawCursor = true;

    private static final float ITEM_GAP = 0.5f;

    private float cursorX = -1;
    private float cursorY = -1;

    public RadialMenu(float centerX, float centerY, RadialMenuListener listener) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.listener = listener;
        items = new ArrayList<>();
    }

    public void setActiveItem(int id) {
        active = id;
    }

    public void addItem(RadialMenuItem item) {
        items.add(item);
        Collections.sort(items);
    }

    public boolean angleContained(float candidate, float start, float extent) {
        return new Arc2D.Float(0, 0, 0, 0, start, extent, Arc2D.PIE).containsAngle(candidate);
    }

    @Override
    public void onPointerUpdate(float cx, float cy, boolean online) {
        drawCursor = online;
        if (!online) {
            selectExtent = 0;
            selected = null;
            listener.onMenuSelection(-1);
            return;
        }
        this.cursorX = cx;
        this.cursorY = cy;

        float dx = cursorX - centerX;
        float dy = cursorY - centerY;

        float r = (float)Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        float theta = (float)Math.atan2(-dy, dx) / (float)Math.PI * 180f;
        if (theta < 0) theta += 360;

        if (r < 2.5f || r > 4.5f) {
            selectExtent = 0;
            selected = null;
            listener.onMenuSelection(-1);
            return;
        }
        if (selected != null) {
            float leeway = (float) ((r - 2.8) * 7.5);
            if (!angleContained(theta, selected.startAngle-leeway, selected.arcAngle+2*leeway)) {
                selected = null;
                selectExtent = 0;
                listener.onMenuSelection(-1);
                return;
            }
            selectExtent = Math.max(0f, r - 2.8f);
            if (selectExtent > 1.2f) {
                listener.onMenuSelection(selected.id);
                selected = null;
                selectExtent = 0;
                return;
            }
            listener.onMenuSelection(-1);
            return;
        }
        if (r > 2.75f && r < 3.2f) {
            for (RadialMenuItem candidate : items) {
                if (angleContained(theta, candidate.startAngle, candidate.arcAngle)) {
                    selected = candidate;
                    selectExtent = Math.max(0f, r - 2.8f);
                    listener.onMenuSelection(-1);
                    return;
                }
            }
        }
        listener.onMenuSelection(-1);
    }

    public void draw(GraphicsWrapper g2) {
        g2.prepare();
        g2.maskCircle(centerX, centerY, 2.8f);
        for (int i = 0; i < items.size(); i++) {
            RadialMenuItem item = items.get(i);
            float gapStart = item.startAngle + item.arcAngle;
            float gapEnd = i == items.size()-1 ? items.get(0).startAngle : items.get(i+1).startAngle;
            float gapLength = gapEnd-gapStart;
            if (gapLength < 0) gapLength += 360;
            if (gapLength >= 5)
                g2.fillArc(centerX, centerY, 3.8f, gapStart + ITEM_GAP, gapLength - 2*ITEM_GAP, Colors.MENU_GAP);

            float extent = item == selected ? selectExtent : 0;
            Paint color = item == selected ? item.selectedColor : (active == item.id ? item.activeColor : Colors.MENU_ITEM);

            g2.maskCircle(centerX, centerY, 2.8f + extent);
            g2.fillArc(centerX, centerY, 3.8f + extent, item.startAngle + ITEM_GAP, item.arcAngle - 2*ITEM_GAP, color);
            g2.restore();

            float labelAngleDeg = item.startAngle + item.arcAngle/2.0f;
            float labelAngleRad = labelAngleDeg / 180.f * (float)Math.PI;
            g2.setOrigin(centerX + (3.3f+extent)*(float)Math.cos(labelAngleRad), centerY - (3.3f+extent)*(float)Math.sin(labelAngleRad));
            item.drawLabel(g2, item == selected);
            g2.restore();

        }
        g2.restore();
        if (drawCursor)
            g2.fillCircle(cursorX, cursorY, 0.25f, Colors.MENU_CURSOR);
    }
}

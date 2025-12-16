package controller;

import common.ShipPlacementHandler;
import model.Model;
import viewer.Viewer;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Controller implements MouseListener, MouseMotionListener {

    private final Model model;
    private final ShipPlacementHandler shipPlacementHandler;

    public Controller(Viewer viewer) {
        this.model = new Model(viewer);
        this.shipPlacementHandler = new ShipPlacementHandler(model, viewer);
    }

    public Model getModel() {
        return model;
    }

    /**
     * Обработка клика мыши. Используется только для выстрелов в фазе боя.
     */
    @Override
    public void mouseClicked(MouseEvent event) {
        // Выстрелы возможны только после завершения фазы расстановки
        if (!model.isSetupPhase()) {
            model.doAction(event.getX(), event.getY());
        }
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (model.isSetupPhase()) {
            shipPlacementHandler.mouseDragged(event);
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (model.isSetupPhase()) {
            shipPlacementHandler.mousePressed(event);
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (model.isSetupPhase()) {
            shipPlacementHandler.mouseReleased(event);
        }


        model.getViewer().update();
    }


    @Override
    public void mouseEntered(MouseEvent event) {

    }

    @Override
    public void mouseExited(MouseEvent event) {

    }


    @Override
    public void mouseMoved(MouseEvent event) {

    }


}
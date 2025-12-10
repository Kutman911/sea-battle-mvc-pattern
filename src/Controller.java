import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Controller implements MouseListener, MouseMotionListener {

    private Model model;
    private Viewer viewer;
    private final ShipPlacementHandler shipPlacementHandler;

    public Controller(Viewer viewer) {
        this.viewer = viewer;
        this.model = new Model(viewer);
        this.shipPlacementHandler = new ShipPlacementHandler(model, viewer);
    }

    public Model getModel() {
        return model;
    }

    public void restartGame() {
        Viewer currentViewer = this.viewer;
        this.model = new Model(currentViewer);

        if (currentViewer.getCanvas() != null) {
            this.model.setCanvas(currentViewer.getCanvas());
        }

        currentViewer.update();
        currentViewer.setVisibleFrame();
        System.out.println("Игра успешно перезапущена.");
    }

    /**
     * Обработка клика мыши. Используется только для выстрелов в фазе боя.
     */
    @Override
    public void mouseClicked(MouseEvent event) {
        // Выстрелы возможны только после завершения фазы расстановки
        if (!model.isSetupPhase()) {
            model.doAction(event.getX(), event.getY());

//            if (model.lost()) {
//                viewer.showResult(false);
//                return;
//            }
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

        if (!model.isSetupPhase()) {
            model.setPlayerTurn(true);
            model.startBattlePhase();
            viewer.update();
        }
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
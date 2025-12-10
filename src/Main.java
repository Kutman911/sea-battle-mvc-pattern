public class Main {
    public static void main(String[] args) {
        SplashWindow splashWindow = new SplashWindow();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ie) {
            System.err.println(ie);
        }
        splashWindow.closeWindow();

        Viewer viewer = new Viewer();
        viewer.showMainMenuFromCanvas();
    }
}

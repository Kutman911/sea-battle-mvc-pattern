public class Main {
    public static void main(String[] args) {
        Viewer viewer = new Viewer();

        SplashWindow splashWindow = new SplashWindow();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ie) {
            System.err.println(ie);
        }
        splashWindow.closeWindow();
        viewer.setVisibleFrame();

    }
}

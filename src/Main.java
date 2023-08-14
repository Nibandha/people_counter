import org.opencv.core.Core;
import splash_screen.MainApplicationWindow;
import splash_screen.SplashScreen;

//public class Main {
//    public static void main(String[] args) {
//        System.out.println("Hello world!");
//    }
//}
public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String args[]) {
        new SplashScreen();


        // Close the splash screen and launch the main application window
        new MainApplicationWindow();
        // System. out.println (Core. VERSION);
    }
}


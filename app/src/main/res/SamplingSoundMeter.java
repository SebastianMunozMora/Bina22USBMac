public class SamplingSoundMeter {
    private SoundLevelUpdateable updateable;
    private Handler handler;
    private Runnable sampler;
    private Visualizer visualizer;

    public SamplingSoundMeter(SoundLevelUpdateable updateableObject) {
        updateable = updateableObject;
        visualizer = new Visualizer(0);
        visualizer.setMeasurementMode(Visualizer.MEASUREMENT_MODE_PEAK_RMS);
        handler = new Handler();
    }
}
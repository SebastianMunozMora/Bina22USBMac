package com.example.sebastin.bina2;

/**
 * Created by Sebastian on 29/03/2016.
 */
public class RecordingsDataProvider {
    private int recording_image_resources;
    private String recording_title_resources,recording_data_resources;
    public RecordingsDataProvider (int recording_image_resources,String recording_title_resources,String recording_data_resources){
        this.setRecording_image_resources(recording_image_resources);
        this.setRecording_title_resources(recording_title_resources);
        this.setRecording_data_resources(recording_data_resources);
    }
    public int getRecording_image_resources() {
        return recording_image_resources;
    }

    public void setRecording_image_resources(int recording_image_resources) {
        this.recording_image_resources = recording_image_resources;
    }

    public String getRecording_title_resources() {
        return recording_title_resources;
    }

    public void setRecording_title_resources(String recording_title_resources) {
        this.recording_title_resources = recording_title_resources;
    }

    public String getRecording_data_resources() {
        return recording_data_resources;
    }

    public void setRecording_data_resources(String recording_data_resources) {
        this.recording_data_resources = recording_data_resources;
    }
}

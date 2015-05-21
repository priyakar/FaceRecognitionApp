package com.facerecog.shahmalav.facerecognitionapp;

/**
 * Created by Malav on 5/16/2015.
 * This interface helps MainActivity to communicate with the HttpPostAysnc class
 * After Http post is executed, return the result to MainActivity.
 *
 */
public interface HttpAsyncResponse {
    void processFinish(String output);
}

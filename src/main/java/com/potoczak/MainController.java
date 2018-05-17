package com.potoczak;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Timer;
import java.util.function.UnaryOperator;

public class MainController {

    //final TrayIcon trayIcon = new TrayIcon(image);
    final SystemTray tray = SystemTray.getSystemTray();

    @FXML private CheckBox ShortBreakCheckBox;
    @FXML private CheckBox LongBreakCheckBox;
    @FXML private CheckBox NotifyCheckBox;
    @FXML private Button StartButton;
    @FXML private TextField ShortTextField;
    @FXML private TextField LongTextField;
    @FXML private TextField BreakTextField;
    @FXML private TextField NotifyTextField;

    private Timeline timelineShort, timelineLong, timelineBreak;
    private int secondsShort,secondsLong,secondsBreak;
    boolean checkTimelineShort,checkTimelineLong;


    public void initialize() {
        ShortTextField.setTextFormatter(textFormatterShort);
        LongTextField.setTextFormatter(textFormatterLong);
        BreakTextField.setTextFormatter(textFormatterBreak);
        NotifyTextField.setTextFormatter(textFormatterNotify);

        NotifyCheckBox.setDisable(true);
        NotifyTextField.setDisable(true);
        LongBreakCheckBox.setOnAction((event) -> {
            if(NotifyCheckBox.isDisable()) {
                NotifyCheckBox.setDisable(false);
                NotifyTextField.setDisable(false);
            }
            else {
                NotifyCheckBox.setDisable(true);
                NotifyTextField.setDisable(true);
                NotifyCheckBox.setSelected(false);
                NotifyTextField.clear();
            }
        });

        StartButton.setOnAction((event -> {
            //tutaj znika
            if(checkFields())
                startProgram();
        }));
    }

    private boolean checkFields()
    {
        if(checkCorrect(ShortBreakCheckBox,ShortTextField) && checkCorrect(LongBreakCheckBox,LongTextField)
                && checkCorrect(LongBreakCheckBox,BreakTextField) && checkCorrect(NotifyCheckBox,NotifyTextField))
            return true;
        else {
            showAlertWrongValues();
            return false;
        }
    }

    private boolean checkCorrect(CheckBox checkBox, TextField field){
        boolean result = true;
        if(checkBox.isSelected() && checkTextField(field))
        {
            if(getValueTextField(field) != 0)
                result = true;
            else
                result = false;
        }
        else result = false;
        return result;
    }

    private boolean checkTextField(TextField field)
    {
        if(!(field.getText() == null || field.getText().trim().isEmpty()))
        {
            if(field.getText().charAt(0)!='0')
                return true;
            else
                return false;
        }
        else return false;
    }


    private void showAlertWrongValues()
    {
        Alert alertBadValues = new Alert(Alert.AlertType.WARNING, "Wrong values in fields!");
        Stage stage = (Stage) alertBadValues.getDialogPane().getScene().getWindow();
        stage.show();
        stage.setAlwaysOnTop(true);
        stage.toFront();
    }

    private void startProgram(){

        if(ShortBreakCheckBox.isSelected()){
            if(checkTimelineShort)
                timelineShort.stop();
            secondsShort = 0;
            checkTimelineShort=true;
            timelineShort = new Timeline(
                    new KeyFrame(
                            Duration.millis(1000),
                            e -> {
                                secondsShort++;
                                System.out.println("short" + secondsShort + " " + getShortBreakTime());
                                if(checkTime(secondsShort, getShortBreakTime(),"short")) secondsShort=0;
                            }
                    )
            );
            timelineShort.setCycleCount( Animation.INDEFINITE );
            timelineShort.play();
        }

        if(LongBreakCheckBox.isSelected()){
            if(checkTimelineLong)
                timelineLong.stop();
            secondsLong = 0;
            checkTimelineLong=true;
            timelineLong = new Timeline(
                    new KeyFrame(
                            Duration.millis(1000),
                            e -> {
                                secondsLong++;
                                System.out.println("long" + secondsLong);
                                if(checkTime(secondsLong, getLongBreakTime(),"long")) secondsLong=0;

                                if(NotifyCheckBox.isSelected()){
                                    if(secondsLong == getLongBreakTime()-getNotifyTime())
                                    {
                                        Alert alertNotify = new Alert(Alert.AlertType.WARNING, "The long break will start in: "+ NotifyTextField.getText() +" minutes!");
                                        Stage stage = (Stage) alertNotify.getDialogPane().getScene().getWindow();
                                        stage.show();
                                        stage.setAlwaysOnTop(true);
                                        stage.toFront();
                                    }
                                }
                            }
                    )
            );
            timelineLong.setCycleCount( Animation.INDEFINITE );
            timelineLong.play();
        }

        Stage stage;
        stage = (Stage) ShortTextField.getScene().getWindow();
        stage.setIconified(true);
    }

    private int getValueTextField(TextField field){
        int time = Integer.parseInt(field.getText()) * 60;
        return time;
    }

    private int getShortBreakTime(){
        int time = Integer.parseInt(ShortTextField.getText()) * 60;
        return time;
    }

    private int getLongBreakTime(){
        int time = Integer.parseInt(LongTextField.getText()) * 60;
        return time;
    }

    private int getBreakTime(){
        int time = Integer.parseInt(BreakTextField.getText()) * 60;
        return time;
    }

    private int getNotifyTime(){
        int time = Integer.parseInt(NotifyTextField.getText()) * 60;
        return time;
    }

    private String changeTimeBreak(int secondsBreak){
        String time;
        int minutes,seconds;
        int currentTime = getBreakTime() - secondsBreak;
        minutes = currentTime / 60;
        seconds = currentTime % 60;
        if(seconds>9)
            time=(Integer.toString(minutes)+":"+Integer.toString(seconds));
        else
            time=(Integer.toString(minutes)+":0"+Integer.toString(seconds));
        return time;
    }

    private boolean checkTime(int seconds, int BreakTime, String CheckBox){
        if(seconds==BreakTime && CheckBox=="short") {
            timelineShort.pause();
            Alert alert = new Alert(Alert.AlertType.WARNING, "Take a short break!");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setOnHidden(event -> timelineShort.play());
            stage.show();
            stage.setAlwaysOnTop(true);
            stage.toFront();
            return true;
        }
        if(seconds==BreakTime && CheckBox=="long") {
            secondsBreak=0;
            timelineLong.pause();
            if(checkTimelineShort)
                timelineShort.stop();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            timelineBreak = new Timeline(
                    new KeyFrame(
                            Duration.millis(1000),
                            e -> {
                                secondsBreak++;
                                System.out.println("break" + secondsBreak);
                                alert.setContentText("Break: "+ changeTimeBreak(secondsBreak));
                                alert.setOnHidden(event -> {
                                    timelineBreak.stop();
                                    timelineLong.play();
                                });
                                if(changeTimeBreak(secondsBreak).equals("0:00")) timelineBreak.stop();
                            }
                    )
            );
            timelineBreak.setCycleCount( Animation.INDEFINITE );
            timelineBreak.play();
            stage.show();
            stage.setAlwaysOnTop(true);
            stage.toFront();
            return true;
        }
        return false;
    }

    UnaryOperator<TextFormatter.Change> filter = change -> {
        String text = change.getText();
        if(text.matches("[0-9]*$"))
            return change;
        return null;
    };


    TextFormatter<String> textFormatterShort = new TextFormatter<String>(filter);
    TextFormatter<String> textFormatterLong = new TextFormatter<String>(filter);
    TextFormatter<String> textFormatterBreak = new TextFormatter<String>(filter);
    TextFormatter<String> textFormatterNotify = new TextFormatter<String>(filter);
}

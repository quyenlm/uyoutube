package com.mrmq.uyoutube.windows;

import com.mrmq.uyoutube.AppStartup;
import com.mrmq.uyoutube.YouTubeService;
import com.mrmq.uyoutube.config.Config;
import com.mrmq.uyoutube.helper.Validator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UYouTubeLoginController {
    private static final Logger logger = LoggerFactory.getLogger(AppStartup.class);

    @FXML private Text txtMessage;
    @FXML private TextField txtLoginEmail;
    @FXML private Button btnLogin;

    private ExecutorService executor = Executors.newCachedThreadPool();

    public UYouTubeLoginController() {
        super();
    }

    @FXML protected void handleSubmitButtonAction(ActionEvent event) {
        try {
            Stage stage = null;
            Parent root;
            Scene scene = null;
            if(event.getSource() == btnLogin){
                String email = txtLoginEmail.getText().trim();

                if(Validator.isEmail(email)) {

                    if(loadChannel(email)) {
                        //get reference to the button's stage
                        stage = (Stage) btnLogin.getScene().getWindow();
                        //load up OTHER FXML document
                        root = FXMLLoader.load(getClass().getResource("../../../../fxml/fxml_main.fxml"));
                        scene = new Scene(root, 1024, 768);
                    } else {
                        txtMessage.setText("Email/Pass not correct");
                    }
                } else
                    txtMessage.setText("Email Invalid: " + email);
            }

            //create a new scene with root and set the stage
            if(stage != null) {
                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    private boolean loadChannel(final String email) throws IOException {
        YouTubeService service = new YouTubeService();
        service.setChannelEmail(email);
        Config.uouTubeService = service;
        return service.login();
    }

}
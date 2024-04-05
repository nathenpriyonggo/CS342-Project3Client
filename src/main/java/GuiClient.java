
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiClient extends Application{

	String clientName;
	TextField text_chat, text_username;
	Button button_send, button_username;
	Client clientConnection;
	
	ListView<String> listItems2;
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		Font.loadFont(getClass().getResourceAsStream("gg-sans-2/gg sans Regular.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("gg-sans-2/gg sans Medium.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("gg-sans-2/gg sans Semibold.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("gg-sans-2/gg sans Bold.ttf"), 14);

		clientConnection = new Client(data->{
				Platform.runLater(()->{
					listItems2.getItems().add(0, data.toString());
			});
		});

		clientConnection.start();

		listItems2 = new ListView<String>();






		// Username Scene: Username text field
		text_username = new TextField();

		// Username Scene: Username button
		button_username = new Button("Confirm");
		button_username.setOnAction(e->{
			clientName = text_username.getText();
			text_username.clear();
			primaryStage.setTitle(clientName + "'s Text-it");
			primaryStage.setScene(createTextItGui());
		});


		// Text it Scene: Chat text field
		text_chat = new TextField();

		// Text it Scene: Send button
		button_send = new Button("Send");
		button_send.setOnAction(e->{
			Message newMessage = new Message(clientName, text_chat.getText());
			clientConnection.send(newMessage);
			text_chat.clear();
		});







		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

		primaryStage.setScene(createUsernameGui());
		primaryStage.setTitle("Text-it");
		primaryStage.show();
	}
	

	/*
	Username scene code
	 */
	public Scene createUsernameGui() {

		VBox paneVertical = new VBox(10, text_username, button_username);
		paneVertical.setStyle("-fx-font-family: 'gg sans Semibold'");

		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setCenter(paneVertical);

		return new Scene(pane, 500, 400);
	}

	/*
	Text-it scene code
	 */
	public Scene createTextItGui() {
		
		VBox paneVertical = new VBox(10, text_chat, button_send, listItems2);
		paneVertical.setStyle("-fx-font-family: 'gg sans Semibold'");

		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setCenter(paneVertical);

		return new Scene(pane, 500, 400);
	}

}

package seedu.coursepilot.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

/**
 * A UI component that displays a list of tutorials in the application.
 */
public class TutorialListPanel extends UiPart<Region> {
    private static final String FXML = "TutorialListPanel.fxml";

    /**
     * Sample tutorial identifiers used to populate the tutorial list.
     */
    private static final ObservableList<String> SAMPLE_TUTORIALS = FXCollections.observableArrayList(
            "CS2103T-W13",
            "CS2103T-W14",
            "CS2103T-W15"
    );

    /**
     * The {@code ListView} UI element that displays the list of tutorials.
     */
    @FXML
    private ListView<String> tutorialListView;

    /**
     * Creates a {@code TutorialListPanel} and populates the list view
     * with sample tutorial data.
     */
    public TutorialListPanel() {
        super(FXML);
        tutorialListView.setItems(SAMPLE_TUTORIALS);
    }
}

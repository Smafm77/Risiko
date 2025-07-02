module ModuleClient {

    exports client.ui;
    exports client.ui.cui;
    exports client.ui.gui;

    requires java.datatransfer; //??? bin bisschen lost
    requires ModuleCommon;
    requires java.desktop; requires ModuleServer;
}
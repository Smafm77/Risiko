module ModuleCommon {

    exports common.enums;
    exports common.exceptions;
    exports common.missionen;
    exports common.valueobjects;

    requires java.datatransfer;
    requires ModuleServer;
    requires ModuleClient;
}
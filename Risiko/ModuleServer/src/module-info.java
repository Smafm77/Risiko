module ModuleServer {

    exports server.domain;
    exports server.persistence;
    exports server.domain.missionen;

    requires ModuleCommon;
    requires java.desktop;
}
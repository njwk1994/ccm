package ccm.server.enums;

public enum relCollectionTypes {
    End1s,
    End2s,
    Unknown;

    public static relDirection toRelDirection(relCollectionTypes relCollectionTypes) {
        switch (relCollectionTypes) {
            case End1s:
                return relDirection._1To2;
            case End2s:
                return relDirection._2To1;
        }
        return relDirection._unknown;
    }
}

package crypto.did;

import com.fasterxml.jackson.databind.util.JSONPObject;
import netscape.javascript.JSObject;


public class DIDDocumentGenerator {
    private static DIDDocumentGenerator instance;
    private DIDDocumentGenerator() {}

    public static DIDDocumentGenerator getInstance() {
        if(instance == null) {
            synchronized (DIDDocumentGenerator.class) {
                if(instance == null) {
                    instance = new DIDDocumentGenerator();
                }
            }
        }
        return instance;
    }

    public DIDDocument generateDIDDocument(String did, String publicKey) {
        DIDDocument didDocument = new DIDDocument(did, did);

        didDocument.addPemPublicKey(did, "RSA", did, publicKey);
        didDocument.addService(did,"medico","medico", "medico.com");
        return didDocument;



    }
}

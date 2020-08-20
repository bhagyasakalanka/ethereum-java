package crypto.did;


import java.util.ArrayList;
import java.util.Date;

public class DIDDocument {
    private String id;
    private String controller;
    private ArrayList<DIDPublicKeyBase> publicKey;
    private ArrayList<Object> assertionMethod;
    private ArrayList<Object>  keyAgreement;
    private ArrayList<Object> capabilityInvocation;
    private ArrayList<Object> capabilityDelegation;
    private ArrayList<DIDService> service;
    private Date created;

    public DIDDocument(String did, String controller) {
        Date date  = new Date();
        this.created = date;
        this.updated = date;
        this.id = did;
        this.controller = controller;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    private Date updated;
    private ArrayList<Object> authentication;

    private class DIDBase {
        private String id;
        private String type;
        private String controller;
        private String publicKey;
        public DIDBase(String did, String type, String controller, String publicKey) {
            this.id = did;
            this.type = type;
            this.controller = controller;
            this.publicKey = publicKey;
        }
    }
    private class DIDPublicKeyBase {
        private String id;
        private String type;

        public DIDPublicKeyBase(String id, String type, String controller) {
            this.id = id;
            this.type = type;
            this.controller = controller;
        }

        private String controller;
    }
    private class DIDAssertionMethod extends DIDBase{

        public DIDAssertionMethod(String did, String type, String controller, String publicKey) {
            super(did, type, controller, publicKey);
        }
    }

    private class DIDKeyAgreement extends DIDBase {
        public DIDKeyAgreement(String did, String type, String controller, String publicKey) {
            super(did, type, controller, publicKey);
        }
    }

    private class DIDCapabilityInvocation extends DIDBase {
        public DIDCapabilityInvocation(String did, String type, String controller, String publicKey) {
            super(did, type, controller, publicKey);
        }
    }

    private class DIDCapabilityDelegation extends DIDBase{
        public DIDCapabilityDelegation(String did, String type, String controller, String publicKey) {
            super(did, type, controller, publicKey);
        }
    }

    private class DIDService {
        private String id;
        private String type;
        private String endPoint;

        public DIDService(String id, String type, String endPoint) {
            this.id = id;
            this.type = type;
            this.endPoint = endPoint;
        }
    }

    private class DIDAuthentication extends DIDBase {

        public DIDAuthentication(String id, String type, String controller, String publicKey) {
            super(id, type, controller, publicKey);
        }
    }

    public void addCapabilityInvocation(String did, String type, String controller, String publicKey) {
        if(capabilityInvocation == null) {
            capabilityInvocation = new ArrayList<>();
        }
        did = did + "#keys-"+(capabilityInvocation.size()+1);
        DIDCapabilityInvocation didCapabilityInvocation = new DIDCapabilityInvocation(did, type, controller, publicKey);


        this.capabilityInvocation.add(didCapabilityInvocation);

    }

    public void addCapabilityInvocation(String did) {
        if(capabilityInvocation == null) {
            capabilityInvocation = new ArrayList<>();
        }
        did = did + "#keys-"+(capabilityInvocation.size()+1);
        this.capabilityInvocation.add(did);

    }

    public void addCapabilityDelegation(String did, String type, String controller, String publicKey) {

        if(capabilityDelegation == null) {
            capabilityDelegation = new ArrayList<>();
        }
        did = did + "#keys-"+(capabilityDelegation.size()+1);
        DIDCapabilityDelegation didCapabilityDelegation = new DIDCapabilityDelegation(did, type, controller, publicKey);
        this.capabilityDelegation.add(didCapabilityDelegation);

    }
    public void addCapabilityDelegation(String did) {
        if(capabilityDelegation == null) {
            capabilityDelegation = new ArrayList<>();
        }
        did = did + "#keys-"+(capabilityDelegation.size()+1);
        this.capabilityDelegation.add(did);

    }

    public void addService(String did, String type, String typeShort,String endPoint) {

        if(service == null) {
            service = new ArrayList<>();
        }
        did = did + "#"+typeShort;
        DIDService didService = new DIDService(did, type, endPoint);
        this.service.add(didService);

    }

    public void addAuthentication(String did, String type, String controller, String publicKey) {

        if(authentication == null) {
            authentication = new ArrayList<>();
        }
        did = did + "#keys-"+(authentication.size()+1);
        DIDAuthentication didAuthentication = new DIDAuthentication(did, type, controller, publicKey);
        this.authentication.add(didAuthentication);

    }
    public void addAuthentication(String did) {
        if(authentication == null) {
            authentication = new ArrayList<>();
        }
        did = did + "#keys-"+(authentication.size()+1);
        this.authentication.add(did);

    }

    public void addAssertionMethod(String did, String type, String controller, String publicKey) {

        if(assertionMethod == null) {
            assertionMethod = new ArrayList<>();
        }
        did = did + "#keys-"+(assertionMethod.size()+1);
        DIDAssertionMethod didAssertionMethod = new DIDAssertionMethod(did, type, controller, publicKey);
        this.assertionMethod.add(didAssertionMethod);

    }
    public void addAssertionMethod(String method) {
        if(assertionMethod == null) {
            assertionMethod = new ArrayList<>();
        }
        method = method + "#keys-"+(assertionMethod.size()+1);
        this.assertionMethod.add(method);

    }

    public void addKeyAgreement(String did, String type, String controller, String publicKey) {
        if(keyAgreement == null) {
            keyAgreement = new ArrayList<>();
        }

        DIDKeyAgreement didKeyAgreement = new DIDKeyAgreement(did, type, controller, publicKey);
        did = did + "#keys-"+(keyAgreement.size()+1);
        this.keyAgreement.add(didKeyAgreement);

    }
    public void addKeyAgreement(String did) {
        if(keyAgreement == null) {
            keyAgreement = new ArrayList<>();
        }
        did = did + "#keys-"+(keyAgreement.size()+1);
        this.keyAgreement.add(assertionMethod);

    }

    private class DIDJwkPublicKey extends DIDPublicKeyBase {
        private String publicKeyJwk;
        public DIDJwkPublicKey(String did, String type, String controller, String publicKey) {
            super(did, type, controller);
            this.publicKeyJwk = publicKey;
        }
    }

    public void addJwkPublicKey(String did, String type, String controller, String newPublicKey) {

        if(publicKey == null) {
            publicKey = new ArrayList<>();
        }
        did = did + "#keys-"+(publicKey.size()+1);
        DIDJwkPublicKey didPublicKey = new DIDJwkPublicKey(did, type, controller, newPublicKey);
        this.publicKey.add(didPublicKey);

    }

    private class DIDEd25519PublicKey extends DIDPublicKeyBase {
        private String publicKeyBase58;
        public DIDEd25519PublicKey(String did, String type, String controller, String publicKey) {
            super(did, type, controller);
            this.publicKeyBase58 = publicKey;
        }
    }

    public void addEd25519PublicKey(String did, String type, String controller, String newPublicKey) {

        if(publicKey == null) {
            publicKey = new ArrayList<>();
        }
        did = did + "#keys-"+(publicKey.size()+1);
        DIDEd25519PublicKey didPublicKey = new DIDEd25519PublicKey(did, type, controller, newPublicKey);
        this.publicKey.add(didPublicKey);

    }

    private class DIDHexPublicKey extends DIDPublicKeyBase {
        private String publicKeyHex;
        public DIDHexPublicKey(String did, String type, String controller, String publicKey) {
            super(did, type, controller);
            this.publicKeyHex = publicKey;
        }
    }

    public void addHexPublicKey(String did, String type, String controller, String newPublicKey) {

        if(publicKey == null) {
            publicKey = new ArrayList<>();
        }
        did = did + "#keys-"+(publicKey.size()+1);
        DIDHexPublicKey didPublicKey = new DIDHexPublicKey(did, type, controller, newPublicKey);
        this.publicKey.add(didPublicKey);

    }

    private class DIDEthereumAddress extends DIDPublicKeyBase {
        private String ethereumAddress;
        public DIDEthereumAddress(String did, String type, String controller, String ethereumAddress) {
            super(did, type, controller);
            this.ethereumAddress = ethereumAddress;
        }
    }

    public void addDIDEthereumAddress(String did, String type, String controller, String ethereumAddress) {

        if(publicKey == null) {
            publicKey = new ArrayList<>();
        }
        did = did + "#keys-"+(publicKey.size()+1);
        DIDEthereumAddress didPublicKey = new DIDEthereumAddress(did, type, controller, ethereumAddress);
        this.publicKey.add(didPublicKey);

    }

    private class DIDPemPublicKey extends DIDPublicKeyBase {
        private String publicKeyPem;
        public DIDPemPublicKey(String did, String type, String controller, String publicKey) {
            super(did, type, controller);
            this.publicKeyPem = publicKey;
        }
    }

    public void addPemPublicKey(String did, String type, String controller, String newPublicKey) {

        if(publicKey == null) {
            publicKey = new ArrayList<>();
        }
        did = did + "#keys-"+(publicKey.size()+1);
        DIDPemPublicKey didPublicKey = new DIDPemPublicKey(did, type, controller, newPublicKey);
        this.publicKey.add(didPublicKey);

    }
}

pragma solidity ^0.4.21;

contract MainContract {
    address public owner;
    struct doctorDetails {
        bytes32 profileHash;
        bytes32 verifiableClaimHash;
        string profileLink;
        string verifiableClaimLink;
    }
    address[] public claimIssuers;

    mapping(address => doctorDetails) public doctorMapping;

    constructor() public {
        owner = msg.sender;
    }

    function addClaimIssuers(address newIssuer) public returns(bool){
        require(msg.sender == owner, "You Don't have access");
        claimIssuers.push(newIssuer);
        emit addIssuerEvent(newIssuer);
        return true;
    }

    event addIssuerEvent (address newIssuer);

    modifier hasAccess(address issuer) {
        bool result = false;
        for (uint256 i=0; i< claimIssuers.length; i++){
            if(claimIssuers[i] == issuer){
                result = true;
            }
        }
        require(result, "don't have access");
        _;
    }

    function _setVerifiableClaimDetails(string link, bytes32 hash, address issuer, address doctor) internal hasAccess(issuer) returns(bool){
        require(bytes(doctorMapping[doctor].profileLink).length > 0, "no such a address exist");

        doctorMapping[doctor].verifiableClaimHash = hash;
        doctorMapping[doctor].verifiableClaimLink = link;
        return true;

    }
    event updateClaimDetails(string hash, string link, address doctor);

    function setOrUpdateVerifiableClaimDetails(string link, string hash, address doctor) public returns(bool){
        bytes32 claimHash = stringToBytes32(hash);
        address issuer = msg.sender;
        bool updateState = _setVerifiableClaimDetails(link, claimHash, issuer, doctor);
        emit updateClaimDetails(hash, link, doctor);
        return updateState;
    }

    function _addNewDoctor(string link, bytes32 hash, address doctor) internal returns(bool){
        require(bytes(doctorMapping[doctor].profileLink).length == 0 , "doctor already exists");
        doctorMapping[doctor].profileHash = hash;
        doctorMapping[doctor].profileLink = link;
        return true;
    }

    function registerDoctor(string link, string hash) returns(bool){
        bytes32 profileHash = stringToBytes32(hash);
        address doctorAddress = msg.sender;
        bool updateState = _addNewDoctor(link, profileHash, doctorAddress);
        emit registerDoctorEvent(hash, link, doctorAddress);
        return true;
    }

    event registerDoctorEvent(string hash, string link, address doctor);

    function stringToBytes32(string memory source) public returns (bytes32 result) {
          bytes memory tempEmptyStringTest = bytes(source);
          if (tempEmptyStringTest.length == 0) {
              return 0x0;
          }

          assembly {
              result := mload(add(source, 32))
          }
    }

    function getDoctorDetails(address doctorAddress) public view returns(string, string){
        //require(bytes(doctorMapping[doctorAddress].profileLink).length > 0, "no such a address exist");

        return (doctorMapping[doctorAddress].profileLink, doctorMapping[doctorAddress].verifiableClaimLink);
    }

    function validateDoctor(address doctorAddress, string profileHash, string claimHash) public view returns(bool){
        require(bytes(doctorMapping[doctorAddress].profileLink).length > 0, "no such a address exist");

        bytes32 cHash = stringToBytes32(claimHash);
        bytes32 pHash = stringToBytes32(profileHash);

        require(cHash == doctorMapping[doctorAddress].verifiableClaimHash, "verifiable claim does not match");
        require(pHash == doctorMapping[doctorAddress].profileHash, "profile data does not match");

        return true;
    }


}
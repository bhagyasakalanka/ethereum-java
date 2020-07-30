pragma solidity ^0.4.21;


/**
*  @dev Smart Contract responsible to notarize documents on the Ethereum Blockchain
*/
contract DocumentRegistry {

  struct Document {
      address signer; // Notary
      uint date; // Date of notarization
      bytes32 hash; // Document Hash
  }

  /**
   *  @dev Storage space used to record all documents notarized with metadata
   */
  mapping(bytes32 => Document) registry;

  /**
   *  @dev Notarize a document identified by its 32 bytes hash by recording the hash, the sender and date in the registry
   *  @dev Emit an event Notarized in case of success
   *  @param _documentHash Document hash
   */
  function notarizeDocument(string _documentHash) external returns (bool) {
    bytes32 _doc = stringToBytes32(_documentHash);
    registry[_doc].signer = msg.sender;
    registry[_doc].date = now;
    registry[_doc].hash = _doc;

    emit Notarized(msg.sender, _doc);

    return true;
  }

  /**
   *  @dev Verify a document identified by its hash was noterized in the registry.
   *  @param _documentHash Document hash
   *  @return bool if document was noterized previsouly in the registry
   */
  function isNotarized(string _documentHash) external view returns (bool) {
    bytes32 _doc = stringToBytes32(_documentHash);
    return registry[_doc].hash ==  _doc;
  }

  /**
   *  @dev Definition of the event triggered when a document is successfully notarized in the registry
   */
  event Notarized(address indexed _signer, bytes32 _documentHash);

  function stringToBytes32(string memory source) private returns (bytes32 result) {
      bytes memory tempEmptyStringTest = bytes(source);
      if (tempEmptyStringTest.length == 0) {
          return 0x0;
      }

      assembly {
          result := mload(add(source, 32))
      }
  }
}
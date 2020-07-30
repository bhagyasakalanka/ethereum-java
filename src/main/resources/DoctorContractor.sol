pragma solidity ^0.4.21;

contract DoctorContract {
    address private owner;
    uint private currentRecordNumber;
    struct record {
        string challenge;
        string response;
        bool isActive;
        uint startTimestamp;
        address patient;
    }

    mapping(uint => record) public recordMapping;

    constructor() public {
        owner = msg.sender;
        currentRecordNumber = 0;
    }

    function createNewRecord(string challenge, uint startTimestamp) public returns(bool){
        recordMapping[currentRecordNumber].challenge = challenge;
        recordMapping[currentRecordNumber].isActive = true;
        recordMapping[currentRecordNumber].startTimestamp = startTimestamp;
        recordMapping[currentRecordNumber].patient = msg.sender;
        emit recordCreated(challenge, startTimestamp, msg.sender, currentRecordNumber);
        currentRecordNumber += 1;

        return true;
    }
    event recordCreated (string challenge, uint startTimestamp, address patient, uint recordNumber);

    function sendResponse(string response, uint recordNumber) public returns(bool){
        require (owner == msg.sender, "you don't have permission");
        require (recordMapping[recordNumber].isActive == true, "this record is closed");
        recordMapping[recordNumber].response = response;
        emit sendResponseEvent(response, recordNumber);
        return true;
    }
    event sendResponseEvent(string response, uint recordNumber);

    function readChallenge(uint recordNumber) public view returns(string){
        require(owner == msg.sender, "do not have permission");
        require(recordMapping[recordNumber].isActive == true, "the record is closed");
        return recordMapping[recordNumber].challenge;
    }
    function readResponse(uint recordNumber) public view returns(string){
        require(recordMapping[recordNumber].patient == msg.sender, "do not have permission");
        require(recordMapping[recordNumber].isActive == true, "the record is closed");
        return recordMapping[recordNumber].response;
    }
    function readStartTimestamp(uint recordNumber) public view returns(uint){
        require(recordMapping[recordNumber].patient == msg.sender || owner == msg.sender, "do not have permission");
        require(recordMapping[recordNumber].isActive == true, "the record is closed");
        return recordMapping[recordNumber].startTimestamp;
    }
    function closeRecordByPatient(uint recordNumber) public returns(bool){
        require(recordMapping[recordNumber].isActive == true, "record already closed");
        require(recordMapping[recordNumber].patient == msg.sender, "you do not have permission");
        recordMapping[recordNumber].isActive = false;
        emit recordCloseEvent(true, recordNumber);
        return true;
    }
    function closeRecordByDoctor(uint recordNumber) public returns(bool){
        require(recordMapping[recordNumber].isActive == true, "record already closed");
        require(owner == msg.sender, "you do not have permission");
        recordMapping[recordNumber].isActive = false;
        emit recordCloseEvent(true, recordNumber);
        return true;
    }
    event recordCloseEvent(bool success, uint recordNumber);

    function getLatestRecordNumber() public view returns(uint){
        require(owner == msg.sender,"you do not have permission");
        return currentRecordNumber;
    }

    function isRecordActive(uint recordNumber) public view returns(bool){
        require(owner== msg.sender || recordMapping[recordNumber].patient == msg.sender, "you do not have permission" );
        return recordMapping[recordNumber].isActive == true;
    }
}
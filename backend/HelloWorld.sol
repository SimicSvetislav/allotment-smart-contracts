pragma solidity ^0.6.0;

library TestLibrary {
    
    function increment(uint256 num) public pure returns (uint256 num2) {
        num2 = num + 1;
		return num2;
	}
}

contract HelloWorld { 
 //using TestLibrary for uint256;
    
 string private message;
 uint256 public number;
 
 constructor(string memory mssg, uint256 num) public {
	message = mssg;
	number = num;
 }
 
 function setMessage(string memory _message) public {
     message = _message;
 }
 
 function setNumber(uint256 num) public {
     number = num;
 }
 
 function inc() public view returns (uint256 num) {
	num = TestLibrary.increment(number);
	// num = number+r;
    return num;
 }
 
 function getMessage() public view returns (string memory) {
     return message;
 }  
}
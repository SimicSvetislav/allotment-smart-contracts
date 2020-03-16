pragma solidity ^0.6.0;

library TestLibrary {
    
    function increment(uint256 num) public pure returns (uint256) {
        return num+1;
    }
    
}
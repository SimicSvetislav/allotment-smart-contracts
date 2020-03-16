pragma solidity ^0.6.0;

import './BokkyPooBahsDateTimeLibrary.sol';

contract DateUtilsLibrary is BokkyPooBahsDateTimeLibrary {
    
    function toDate(uint timestamp) 
        public
        pure
        returns (uint year, uint month, uint day)
    {
        (year, month, day) = timestampToDate(timestamp);
    }
    
    function _compare (uint timestamp1, uint timestamp2)
        private
        pure
        returns (int8)
    {
        (uint year1, uint month1, uint day1) = timestampToDate(timestamp1);
        (uint year2, uint month2, uint day2) = timestampToDate(timestamp2);
        
        // Compare years
        if (year1 < year2) {
            return -1;
        } else if (year1 == year2) {
            // Compare months
            if (month1 < month2) {
                return -1;
            } else if (month1 == month2) {
                // Compare days
                if (day1 < day2) {
                    return -1;
                } else if (day1 == day2) {
                    return 0;
                }
            }
        }
        
        return 1;
    }
    
    function isBefore(uint timestamp1, uint timestamp2)
        public
        pure
        returns (bool)
    {
        return _compare(timestamp1, timestamp2) == -1;
    }
    
    function isAfter(uint timestamp1, uint timestamp2)
        public
        pure
        returns (bool)
    {
        return _compare(timestamp1, timestamp2) == 1;
    }
    
    function isSame(uint timestamp1, uint timestamp2)
        public
        pure
        returns (bool)
    {
        return _compare(timestamp1, timestamp2) == 0;
    }
    
    function isBeforeOrSame(uint timestamp1, uint timestamp2)
        public
        pure
        returns (bool)
    {
        return _compare(timestamp1, timestamp2) != 1;
    }
    
    function isAfterOrSame(uint timestamp1, uint timestamp2)
        public
        pure
        returns (bool)
    {
        return _compare(timestamp1, timestamp2) != -1;
    }
    
    function overlapping(uint from1, uint to1, uint from2, uint to2)
        public
        pure
        returns (bool)
    {
        require(isBefore(from1, to1) && isBefore(from2, to2), 'Intervals not valid');
        
        if (isBeforeOrSame(to1, from2) || isBeforeOrSame(to2, from1)) {
            return false;
        }
        
        return true;
    }
    
    function including(uint from1, uint to1, uint from2, uint to2)
        public
        pure
        returns (bool)
    {
        require(isBefore(from1, to1) && isBefore(from2, to2), 'Intervals not valid');
        
        if (isBeforeOrSame(from1, from2) && isAfterOrSame(to1, to2)) {
            return true;
        }
        
        return false;
    }
    
    function isInRange(uint checkDate, uint daysFromToday) 
        public
        view
        returns (bool retVal)
    {
        uint addedTimestamp = addDays(now, daysFromToday);

        if (isAfter(checkDate, addedTimestamp)) {
            return true;
        }
        
        return false;
        
    }
    
    function addDays2(uint timestamp, uint daysToAdd) 
        public
        pure
        returns (uint)
    {
        return addDays(timestamp, daysToAdd);
    }
    
    function dayInRange(uint checkDate, uint startRange, uint endRange)
        public
        pure
        returns (bool) 
    {
        if (isAfterOrSame(checkDate, startRange) && isBeforeOrSame(checkDate, endRange)) {
            return true;
        }
        
        return false;
    }
    
}